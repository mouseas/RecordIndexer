package server.dataAccess;

import java.util.*;
import java.io.*;
import javax.xml.parsers.*;
import java.sql.SQLException;

import org.w3c.dom.*;

import shared.dataTransfer.*;
import server.ServerException;


public class DataImporter {

	private static final String databaseLoc = 
					"database" + File.separator + "indexer-app.sqlite";
	
	private String importLocation;
	private Document doc;
	
	private DataAccess da;
	
	private List<Record> recordList;
	private List<Batch> batchList;
	private List<Project> projectList;
	private List<Field> fieldList;
	
	/**
	 * Constructs and initializes a DataImporter with a source file.
	 * @param location Where to import data from.
	 */
	public DataImporter(String location) {
		this.importLocation = location;
		try {
			da = new DataAccess(databaseLoc);
			DocumentBuilder builder = DocumentBuilderFactory
									  .newInstance().newDocumentBuilder();
			File file = new File(importLocation);
			doc = builder.parse(file);
		} catch (Exception e) {
			System.out.println("Exception during Data Importer construction.");
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Wipes the database, loads an XML file, and loads the contents of the XML
	 * file into the database.
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			try {
				DataImporter di = new DataImporter(args[0]);
				di.wipeDatabase();
				di.importUsers();
				di.importProjectsAndComponents();
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("An error occurred. Check your XML file location.");
				usage();
			}
		} else {
			usage();
		}
		
	}
	
	protected static void usage() {
		System.out.println("USAGE: java DataImporter inputXmlFileLocation");
	}
	
	/**
	 * Wipes the contents of the existing database, to be used prior to
	 * importing data from an XML file.
	 * @return Whether the wipe was successful.
	 */
	public boolean wipeDatabase() {
		da.startTransaction();
		boolean result = da.wipeDatabase(true);
		da.endTransaction(false); // the wipe was already committed.
		return result;
	}
	
	/**
	 * Imports users.
	 * @return
	 */
	public boolean importUsers() {
		if (doc == null || da == null) { return false; }
		NodeList userList = doc.getElementsByTagName("user");
		da.startTransaction();
		boolean result = true;
		for (int i = 0; i < userList.getLength(); i++) {
			Element userNode = (Element)userList.item(i);
			User user = parseUser(userNode, i);
			// Add the user to the database, but if it fails, stop and return false.
			if (!da.addUser(user)) { 
				result = false; 
				System.out.println(user.getUsername() + " failed to add");
			} 
		}
		da.endTransaction(true);
		return result;
	}
	
	/**
	 * Imports projects, along with their associated fields, batches, and records.
	 * Making all the IDs all line up relies on the database starting out empty.
	 * @return
	 */
	public boolean importProjectsAndComponents() throws ServerException, SQLException{
		if (doc == null || da == null) { return false; }
		boolean result = true;
		// initialize empty lists to be filled during the import process
		projectList = new ArrayList<Project>();
		fieldList = new ArrayList<Field>();
		batchList = new ArrayList<Batch>();
		recordList = new ArrayList<Record>();

		importProjects(); // will import projects along with their child objects
		
		da.startTransaction();
		for (int i = 0; i < projectList.size(); i++) {
			da.addProject(projectList.get(i));
		}
		for (int i = 0; i < fieldList.size(); i++) {
			da.addField(fieldList.get(i));
		}
		for (int i = 0; i < batchList.size(); i++) {
			da.addBatch(batchList.get(i));
		}
		da.saveSeveralRecords(recordList);
		da.endTransaction(true);
		
		return result;
	}
	
	/**
	 * Get the DataAccess object, used mostly for DataImporterTest.
	 * @return
	 */
	public DataAccess getDataAccess() {
		return da;
	}
	
	/**
	 * Parses the Project objects from DOM
	 * @return
	 */
	private boolean importProjects() {
		if (doc == null || da == null || projectList == null) { return false; }
		NodeList projectNodeList = doc.getElementsByTagName("project");
		for (int i = 0; i < projectNodeList.getLength(); i++) {
			Element projectNode = (Element)projectNodeList.item(i);
			Project project = parseProject(projectNode);
			projectList.add(project);
		}
		return true;
	}
	
	/**
	 * Parses the Batches from DOM
	 * @param batchNodeList
	 * @param firstFieldID
	 * @return
	 */
	private boolean importBatches(NodeList batchNodeList, int firstFieldID) {
		if (batchNodeList == null || da == null || batchList == null) { return false; }
		for (int i = 0; i < batchNodeList.getLength(); i++) {
			Element batchNode = (Element)batchNodeList.item(i);
			Batch batch = parseBatch(batchNode, firstFieldID);
			batchList.add(batch);
		}
		return true;
	}
	
	/**
	 * Parses the Fields from DOM
	 * @param fieldNodeList
	 * @return
	 */
	private boolean importFields(NodeList fieldNodeList) {
		if (fieldNodeList == null || da == null || fieldList == null) { return false; }
		for (int i = 0; i < fieldNodeList.getLength(); i++) {
			Node fieldNode = fieldNodeList.item(i);
			Field field = parseField((Element)fieldNode);
			fieldList.add(field);
		}
		return true;
	}
	
	/**
	 * Parses the Records from DOM
	 * @param recordNodeList
	 * @param firstFieldID
	 * @return
	 */
	private boolean importRecords(NodeList recordNodeList, int firstFieldID) {
		if (recordNodeList == null || da == null || recordList == null) { return false; }
		if (recordNodeList.getLength() < 1) { return false; }
		for (int i = 0; i < recordNodeList.getLength(); i++) {
			Element recordElem = (Element)(recordNodeList.item(i));
			NodeList recordValueNodeList = recordElem.getElementsByTagName("value");
			
			for (int j = 0; j < recordValueNodeList.getLength(); j++) {
				Element valueElem = (Element)recordValueNodeList.item(j);
				parseRecord(valueElem, firstFieldID + j, i);
			}
		}
		return true;
	}
	
	/**
	 * Parses an individual user from its DOM element. This is independent
	 * of project parsing.
	 * @param user
	 * @param id
	 * @return
	 */
	private User parseUser(Element user, int id) {
		try {
			String username = getStringFromElement(user, "username");
			String firstname = getStringFromElement(user, "firstname");
			String lastname = getStringFromElement(user, "lastname");
			String email = getStringFromElement(user, "email");
			int indexedRecords = getIntFromElement(user, "indexedrecords");
			String password = getStringFromElement(user, "password");
			
			return new User(id, username, firstname, lastname, 
							email, indexedRecords, password);
		} catch (NullPointerException e) {
			System.out.println("Null pointer exception at DataImporter.parseUser.");
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Parses an individual Project object from its DOM element. Calls importFields
	 * on the fields associated with this project. Calls importBatches on the images
	 * associated with this project, which in turn imports the records associated
	 * with each image.
	 * @param project
	 * @param id
	 * @return
	 */
	private Project parseProject (Element project) {
		int firstFieldID = fieldList.size();
		NodeList fieldNodeList = project.getElementsByTagName("field");
		importFields(fieldNodeList);
		NodeList imageNodeList = project.getElementsByTagName("image");
		importBatches(imageNodeList, firstFieldID);
		
		try {
			String title = getStringFromElement(project, "title");
			int numRows = getIntFromElement(project, "recordsperimage");
			int firstYCoord = getIntFromElement(project, "firstycoord");
			int rowHeight = getIntFromElement(project, "recordheight");
			return new Project(projectList.size(), firstYCoord, rowHeight, numRows, title);
		} catch (NullPointerException e) {
			System.out.println("Null pointer exception at DataImporter.parseProject.");
		}
		return null;
	}
	
	/**
	 * Parses an individual batch from its DOM element, then parses all the records
	 * associated with this batch.
	 * @param batch
	 * @param firstFieldID The id of the first field associated with this batch.
	 * @return
	 */
	private Batch parseBatch (Element batch, int firstFieldID) {
		NodeList recordNodeList = batch.getElementsByTagName("record");
		boolean completed = importRecords(recordNodeList, firstFieldID);
		
		try {
			String filename = getStringFromElement(batch, "file");
			return new Batch(batchList.size(), projectList.size(), filename, "0", completed);
		} catch (NullPointerException e) {
			System.out.println("Null pointer exception at DataImporter.parseBatch.");
		}
		return null;
	}
	
	/**
	 * Parses an individual field from its DOM element.
	 * @param field
	 * @return
	 */
	private Field parseField (Element field) {
		try {
			String title = getStringFromElement(field, "title");
			int xCoord = getIntFromElement(field, "xcoord");
			int width = getIntFromElement(field, "width");
			String helpHtml = getStringFromElement(field, "helphtml");
			String knownData = getStringFromElement(field, "knowndata");
			
			return new Field(fieldList.size(), projectList.size(), title, 
					xCoord, width, helpHtml, knownData);
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.out.println("Null pointer exception at DataImporter.parseField.");
		}
		return null;
	}
	
	/**
	 * Parses an individual Record from its DOM element, and some information
	 * about which field and row number the record belongs to.
	 * @param record
	 * @param fieldID
	 * @param rowNum
	 */
	private void parseRecord (Element record, int fieldID, int rowNum) {
		try {
			String value = record.getTextContent().trim();
			recordList.add(new Record(recordList.size(), batchList.size(), 
							fieldID, rowNum, value));
		} catch (NullPointerException e) {
			System.out.println("Null pointer exception at DataImporter.parseField.");
		}
	}
	
	/**
	 * Takes an element, grabs a child element by name, and returns its int value.
	 * @param elem
	 * @param leafName
	 * @return
	 */
	private int getIntFromElement(Element elem, String leafName) {
		Element leaf = (Element)(elem.getElementsByTagName(leafName).item(0));
		return Integer.parseInt(leaf.getTextContent().trim());
	}
	
	/**
	 * Takes an element, grabs a child element by name, and returns its string value.
	 * @param elem
	 * @param leafName
	 * @return
	 */
	private String getStringFromElement(Element elem, String leafName) {
		Element leaf = (Element)(elem.getElementsByTagName(leafName).item(0));
		String result;
		if (leaf != null) {
			result = leaf.getTextContent().trim();
		} else {
			result = "";
		}
		return result;
	}
	
}
