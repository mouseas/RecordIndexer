package client.state;

import java.awt.Dimension;
import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;

import shared.dataTransfer.*;
import client.controller.MainController;
import client.views.mainFrame.MainFrame;
import client.views.mainFrame.viewingArea.ViewingAreaPanel;

public class StateLoader {

	private MainController controller;
	private Document doc;
	
	private StateLoader(MainController controller) {
		this.controller = controller;
		doc = null;
	}
	
	/**
	 * Public method to load the current user's user state.
	 * @param controller
	 */
	public static void loadUserState(MainController controller) {
		if (!controller.loggedIn()) {
			controller.errorDialog("Cannot load: no user currently logged in.");
		} else {
			try {
				StateLoader loader = new StateLoader(controller);
				File file = StateHelper.getUserSaveFile(controller);
				loader.doLoad(file);
			} catch (Exception e) {
				e.printStackTrace();
				controller.errorDialog("Error while loading user state");
			}
		}
	}

	/**
	 * Loads the user's data file, if any, and applies it to the data model
	 * and the window.
	 */
	private void doLoad(File file) throws Exception {
		boolean continueLoading = loadXMLFileToDoc(file);
		if (continueLoading) {
			loadWindowState();
			loadBatch();
			loadBatchWindowState();
		} else { // new user
			controller.getMainView().getMenubar().setDownloadEnabled(true);
		}
	}

	/**
	 * 
	 * @param file
	 */
	private boolean loadXMLFileToDoc(File file) throws Exception {
		if (file.exists()) {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = db.parse(file);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Load the fields, project, and records for the current batch, if any.
	 */
	private void loadBatch() {
		// The batch image must be loaded first and placed in the DataModel, or
		// the other methods will fail.
		loadBatchImage();
		if (controller.userHasBatch()) {
			// fields and project must be loaded before records in order to properly load 
			// the records to the data model.
			loadFields();
			loadProject();
			loadRecords();
			controller.getMainView().getMenubar().setDownloadEnabled(false);
		} else {
			controller.getMainView().getMenubar().setDownloadEnabled(true);
		}
	}

	/**
	 * Load the image for the current batch, if any.
	 */
	private void loadBatchImage() {
		NodeList batchImageNode = doc.getElementsByTagName("batchImage");
		if (batchImageNode == null || batchImageNode.getLength() < 1) {
			controller.setUserHasBatch(false);
			return; // no batch to load.
		}
		
		Element batchImageElem = (Element)batchImageNode.item(0);
		int ID = StateHelper.getIntFromParent(batchImageElem, "ID");
		int projectID = StateHelper.getIntFromParent(batchImageElem, "projectID");
		String imageFilename = StateHelper.getStringFromParent(batchImageElem, "imageFilename");
		String username = StateHelper.getStringFromParent(batchImageElem, "username");
		
		BatchImage bi = new BatchImage(ID, projectID, imageFilename, username);
		Batch newBatch = new Batch(bi);
		controller.getDataModel().setCurrentBatch(newBatch);
		
		controller.setUserHasBatch(true);
	}

	/**
	 * Loads the batch-specific view details, such as scale (zoom), offset, 
	 * highlights on/off, and inverted state.
	 */
	private void loadBatchWindowState() {
		// get values
		NodeList windowElemList = doc.getElementsByTagName("batchWindow");
		if (windowElemList == null || windowElemList.getLength() < 1) {
			return; // nothing to load
		}
		Element windowElem = (Element)windowElemList.item(0);
		double scale = StateHelper.getDoubleFromParent(windowElem, "scale");
		double offsetX = StateHelper.getDoubleFromParent(windowElem, "offsetX");
		double offsetY = StateHelper.getDoubleFromParent(windowElem, "offsetY");
//		boolean hilightsVisible = StateHelper.getBoolFromParent(windowElem, "hilightsVisible");
		boolean invertedImage = StateHelper.getBoolFromParent(windowElem, "invertedImage");
		
		// apply them to window
		ViewingAreaPanel v = controller.getMainView().getViewingArea();
		v.setZoom(scale);
		v.setOffset(offsetX, offsetY);
		// TODO implement setting hilight state
		v.setInverted(invertedImage);
	}

	/**
	 * Loads the batch-independent window details, like position, width and height,
	 * and splitter positions.
	 */
	private void loadWindowState() {
		// get values
		NodeList windowElemList = doc.getElementsByTagName("window");
		if (windowElemList == null || windowElemList.getLength() < 1) {
			return; // nothing to load
		}
		Element windowElem = (Element)windowElemList.item(0);
		int windowX = StateHelper.getIntFromParent(windowElem, "windowX");
		int windowY = StateHelper.getIntFromParent(windowElem, "windowY");
		int windowWidth = StateHelper.getIntFromParent(windowElem, "windowWidth");
		int windowHeight = StateHelper.getIntFromParent(windowElem, "windowHeight");
		int dataSplitterPos = StateHelper.getIntFromParent(windowElem, "dataSplitterPos");
		int mainSplitterPos = StateHelper.getIntFromParent(windowElem, "mainSplitterPos");
		
		// apply them to window
		MainFrame m = controller.getMainView();
		m.setLocation(windowX, windowY);
		m.setSize(windowWidth, windowHeight);
		m.setPreferredSize(new Dimension(windowWidth, windowHeight));
		m.setSplitterPos(mainSplitterPos);
		m.getDataArea().setSplitterPos(dataSplitterPos);
	}

	/**
	 * Loads the fields from the document.
	 */
	private void loadFields() {
		NodeList fieldNodes = doc.getElementsByTagName("field");
		if (fieldNodes == null) {
			return; // no fields to load.
		}
		List<Field> resultList = new ArrayList<Field>();
		for (int i = 0; i < fieldNodes.getLength(); i++) {
			Element fieldElem = (Element)fieldNodes.item(i);
			
			int ID = StateHelper.getIntFromParent(fieldElem, "ID");
			int projectID = StateHelper.getIntFromParent(fieldElem, "projectID");
			String title = StateHelper.getStringFromParent(fieldElem, "title");
			int xCoord = StateHelper.getIntFromParent(fieldElem, "xCoord");
			int width = StateHelper.getIntFromParent(fieldElem, "width");
			String helpHtml = StateHelper.getStringFromParent(fieldElem, "helpHtmlLoc");
			String knownData = StateHelper.getStringFromParent(fieldElem, "knownDataLocation");
			
			Field result = new Field(ID, projectID, title, xCoord, width, helpHtml, knownData);
			resultList.add(result);
		}
		controller.getDataModel().getCurrentBatch().setFields(resultList);
	}

	/**
	 * Loads the project from the document.
	 */
	private void loadProject() {
		NodeList projectNode = doc.getElementsByTagName("project");
		if (projectNode == null || projectNode.getLength() < 1) {
			return; // no project to load.
		}
		Element projectElem = (Element)projectNode.item(0);

		int ID = StateHelper.getIntFromParent(projectElem, "ID");
		String title = StateHelper.getStringFromParent(projectElem, "title");
		int firstYCoord = StateHelper.getIntFromParent(projectElem, "firstYCoord");
		int fieldHeight = StateHelper.getIntFromParent(projectElem, "fieldHeight");
		int numRows = StateHelper.getIntFromParent(projectElem, "numRows");
		
		Project result = new Project(ID, firstYCoord, fieldHeight, numRows, title);
		controller.getDataModel().setCurrentProject(result);
	}
	
	/**
	 * Loads the record values from the doc into the data model.
	 */
	private void loadRecords() {
		if (controller.getDataModel().getCurrentBatch() == null) { return; } // no batch
		int columns = controller.getDataModel().getCurrentBatch().getFields().size();
		int rows = controller.getDataModel().getCurrentProject().getRecordsPerImage();
		if (columns < 1 || rows < 1) {
			controller.errorDialog("Fields and Project not loaded yet.");
		}
//		Record[][] recordGrid = new Record[columns][rows];
		List<Record> recordList = new ArrayList<Record>();
		
		NodeList recordNodes = doc.getElementsByTagName("record");
		for (int i = 0; i < recordNodes.getLength(); i++) {
			Element recordElem = (Element)recordNodes.item(i);
			Record result = buildRecord(recordElem);
			recordList.add(result);
		}
		controller.getDataModel().getCurrentBatch().setRecords(recordList);
	}
	
	/**
	 * Builds a Record from an Element
	 * @param recordElem
	 * @return
	 */
	private Record buildRecord (Element recordElem) {
		int ID = StateHelper.getIntFromParent(recordElem, "ID");
		int batchID = StateHelper.getIntFromParent(recordElem, "batchID");
		int fieldID = StateHelper.getIntFromParent(recordElem, "fieldID");
		int rowNum = StateHelper.getIntFromParent(recordElem, "rowNum");
		String value = StateHelper.getStringFromParent(recordElem, "value");
		
		return new Record(ID, batchID, fieldID, rowNum, value);
	}
	
}
