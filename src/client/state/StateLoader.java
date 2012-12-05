package client.state;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import client.controller.MainController;

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
		loadXMLFileToDoc(file);
		loadWindowState();
		loadBatchWindowState();
		loadBatchImage();
		loadFieldsAndProject();
	}

	/**
	 * 
	 * @param file
	 */
	private void loadXMLFileToDoc(File file) throws Exception {
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		doc = db.parse(file);
	}
	
	/**
	 * Load the fields and the project for the current batch, if any.
	 */
	private void loadFieldsAndProject() {
		// TODO Auto-generated method stub
		

		// fields must be loaded first in order to properly load the records 
		// to the data model.
		loadRecords();
	}

	/**
	 * Load the image for the current batch, if any.
	 */
	private void loadBatchImage() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Loads the batch-specific view details, such as scale (zoom), offset, 
	 * highlights on/off, and inverted state.
	 */
	private void loadBatchWindowState() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Loads the batch-independent window details, like position, width and height,
	 * and splitter positions.
	 */
	private void loadWindowState() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Loads the record values from the doc into the data model.
	 */
	private void loadRecords() {
		
	}
	
}
