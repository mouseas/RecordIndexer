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
			doc = builder.parse(file); // does this leave the file open?
		} catch (Exception e) {
			System.out.println("Exception during Data Importer construction.");
			System.out.println(e.getMessage());
		}
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
		for (int i = 0; i < userList.getLength(); i++) {
			Element userNode = (Element)userList.item(i);
			User user = parseUser(userNode, i);
			// Add the user to the database, but if it fails, stop and return false.
//			if (!da.addUser(user)) { return false; } 
		}
		da.endTransaction(true);
		return true;
	}
	
	/**
	 * Imports projects, along with their associated fields, batches, and records.
	 * @return
	 */
	public boolean importProjects() {
		if (doc == null || da == null) { return false; }
		NodeList projectList = doc.getElementsByTagName("PROJECT");
		da.startTransaction();
		for (int i = 0; i < projectList.getLength(); i++) {
			Element projectNode = (Element)projectList.item(i);
			Project project = parseProject(projectNode, i);
			// Add the project to the database, but if it fails, stop and return false.
//			if (!da.addProject(project)) { return false; } 
		}
		da.endTransaction(true);
		return true;
	}
	
	private boolean importBatches() {
		if (doc == null || da == null) { return false; }
		NodeList batchList = doc.getElementsByTagName("BATCH");
		da.startTransaction();
		for (int i = 0; i < batchList.getLength(); i++) {
			Element batchNode = (Element)batchList.item(i);
			Batch batch = parseBatch(batchNode, i);
			// Add the batch to the database, but if it fails, stop and return false.
//			if (!da.addBatch(batch)) { return false; } 
		}
		da.endTransaction(true);
		return true;
	}
	
	private boolean importFields() {
		if (doc == null || da == null) { return false; }
		NodeList fieldList = doc.getElementsByTagName("FIELD");
		da.startTransaction();
		for (int i = 0; i < fieldList.getLength(); i++) {
			Element fieldNode = (Element)fieldList.item(i);
			Field field = parseField(fieldNode, i);
			// Add the field to the database, but if it fails, stop and return false.
//			if (!da.addField(field)) { return false; } 
		}
		da.endTransaction(true);
		return true;
	}
	
	private boolean importRecords() throws SQLException, ServerException {
		if (doc == null || da == null) { return false; }
		NodeList recordList = doc.getElementsByTagName("RECORD");
		List<Record> records = new ArrayList<Record>();
		da.startTransaction();
		for (int i = 0; i < recordList.getLength(); i++) {
			Element recordNode = (Element)recordList.item(i);
			Record record = parseRecord(recordNode, i);
			records.add(record);
		}
		boolean result = da.saveSeveralRecords(records);
		da.endTransaction(true);
		return result;
	}
	
	public DataAccess getDataAccess() {
		return da;
	}
	
	private User parseUser(Element user, int id) {
		Element usernameElem = (Element)user.getElementsByTagName("USERNAME");
		Element firstNameElem = (Element)user.getElementsByTagName("FIRSTNAME");
		Element lastNameElem = (Element)user.getElementsByTagName("LASTNAME");
		Element emailElem = (Element)user.getElementsByTagName("EMAIL");
		Element indexedRecordsElem = (Element)user.getElementsByTagName("INDEXEDRECORDS");
		Element passwordElem = (Element)user.getElementsByTagName("PASSWORD");
		
		String username = usernameElem.getTextContent();
		String firstname = firstNameElem.getTextContent();
		String lastname = lastNameElem.getTextContent();
		String email = emailElem.getTextContent();
		int indexedRecords = Integer.parseInt(indexedRecordsElem.getTextContent());
		String password = passwordElem.getTextContent();
		
		return new User(id, username, firstname, lastname, email, indexedRecords, password);
	}
	
	private Project parseProject (Element project, int id) {
		return null;
	}
	
	private Batch parseBatch (Element batch, int id) {
		return null;
	}
	
	private Field parseField (Element field, int id) {
		return null;
	}
	
	private Record parseRecord (Element record, int id) {
		return null;
	}
	
}
