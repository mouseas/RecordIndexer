package client.controller;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import shared.dataTransfer.*;

public class StateSaver {

	private MainController controller;
	private Document doc;
	
	/**
	 * Constructor used only internally.
	 * @param controller
	 * @throws ParserConfigurationException 
	 */
	@SuppressWarnings("unused")
	private StateSaver(MainController controller) throws ParserConfigurationException {
		this.controller = controller;
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	}
	
	/**
	 * Save the current window and batch state to a file specific to a user.
	 * @param controller
	 */
	public static void saveState(MainController controller) {
		if (!controller.loggedIn()) {
			controller.errorDialog("Cannot save: no user currently logged in.");
		} else {
			try {
				StateSaver ss = new StateSaver(controller);
				File userSaveFile = ss.getUserSaveFile();
				ss.doSave(userSaveFile);
			} catch (Exception e) {
				e.printStackTrace();
				controller.errorDialog("An error occurred while trying to save.");
			}
		}
	}
	
	/**
	 * Takes the current user and returns a file reference at "users/[username].dat"
	 * Will create the "users" directory if it does not exist.
	 * @return A File referencing the user's data file
	 */
	private File getUserSaveFile() {
		File userFolder = new File("users");
		if (!userFolder.exists()) {
			userFolder.mkdir(); // make the directory if it doesn't exist.
		}
		String username = controller.getServerCommunicator().getCurrentUser().getUsername();
		File result = new File("users/" + username + ".dat");
		return result;
	}
	
	/**
	 * Runs through each of the save functions and saves the needed data into
	 * the Document, then saves the Document to file.
	 * @param saveFile Where to save the completed Document
	 */
	private void doSave(File saveFile) throws Exception{
		docREcordValues();
		docBatchImage();
		docWindowLayout();
		docBatchWindowLayout();
		docFieldsAndProject();
		saveToFile(saveFile);
		System.out.println("Done!");
	}

	/**
	 * Get the values from each Record in the batch and add them to the doc.
	 * If there is not a current batch, this adds nothing to the doc.
	 */
	private void docREcordValues() {
		System.out.println("Gathering Record Values");
		Element recordsElem = doc.createElement("records");
		List<Record> recordsList = controller.getDataModel().getCurrentBatch().getRecords();
		if (recordsList == null) { return; } // no records, don't include them.
		for (int i = 0; i < recordsList.size(); i++) {
			String value = recordsList.get(i).getValue();
			Element record = buildTextElement("record", value);
			recordsElem.appendChild(record);
		}
		doc.appendChild(recordsElem);
	}
	
	/**
	 * Saves the batch image to the doc. This is by far the largest part.
	 */
	private void docBatchImage() {
		System.out.println("Gathering batch image.");
		if (controller.loggedIn()) {
			// TODO implement - take the current image, and save the bytes to an
			// element. If that proves too difficult, save the url, and then it
			// can be loaded from the server when the user logs back in.
		}
	}
	
	/**
	 * Saves the window layout that are not related to working on a batch.
	 * These settings to not require a current batch.
	 */
	private void docWindowLayout() {
		System.out.println("Gathering user-specific window layout data.");
		// TODO implement
	}
	
	/**
	 * Saves window layout details that are specific to the current batch, such
	 * as scale (zoom), offset, highlights on/off, inverted state
	 */
	private void docBatchWindowLayout() {
		if (controller.loggedIn()) {
			System.out.println("Gathering batch-specific window layout.");
			// TODO implement
		}
	}

	/**
	 * Saves the current batch's project and fields.
	 */
	private void docFieldsAndProject() {
		if (controller.loggedIn()) {
			System.out.println("Gathering batch fields and project data.");
			// TODO implement
			// TODO include the field help html? Or just re-load from server?
		}
	}
	
	/**
	 * Builds a single text leaf node with a name and contents.
	 * @param elemName
	 * @param elemText
	 * @return
	 */
	private Element buildTextElement(String elemName, String elemText) {
		Element textElem = doc.createElement(elemName);
		textElem.appendChild(doc.createTextNode(elemText));
		return textElem;
	}
	
	/**
	 * Saves the doc to file.
	 * @param file
	 */
	private void saveToFile(File file) throws Exception {
		System.out.println("Saving to " + file.getName());
		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.setOutputProperty(OutputKeys.INDENT, "yes");
		t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new FileOutputStream(file));
		t.transform(source, result);
	}
}
