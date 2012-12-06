package client.state;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import client.controller.MainController;
import client.views.mainFrame.MainFrame;

import shared.dataTransfer.*;

public class StateSaver {

	private MainController controller;
	private Document doc;
	private Element root;
	
	/**
	 * Constructor used only internally.
	 * @param controller
	 * @throws ParserConfigurationException 
	 */
	private StateSaver(MainController controller) throws ParserConfigurationException {
		this.controller = controller;
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		root = doc.createElement("root");
		doc.appendChild(root);
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
				File userSaveFile = StateHelper.getUserSaveFile(controller);
				ss.doSave(userSaveFile);
			} catch (Exception e) {
				e.printStackTrace();
				controller.errorDialog("An error occurred while trying to save.");
			}
		}
	}
	
	/**
	 * Runs through each of the save functions and saves the needed data into
	 * the Document, then saves the Document to file.
	 * @param saveFile Where to save the completed Document
	 */
	private void doSave(File saveFile) throws Exception{
		docRecords();
		docBatchImage();
		docWindowLayout();
		docBatchWindowLayout();
		docFields();
		docProject();
		saveToFile(saveFile);
	}

	/**
	 * Get the values from each Record in the batch and add them to the doc.
	 * If there is not a current batch, this adds nothing to the doc.
	 */
	private void docRecords() {
		if (controller.userHasBatch()) {
			Element recordsElem = doc.createElement("records");
			List<Record> recordsList = controller.getDataModel().getCurrentBatch().getRecords();
			if (recordsList == null) { return; } // no records, don't include them.
			for (int i = 0; i < recordsList.size(); i++) {
				Record record = recordsList.get(i);
				Element recordElem = doc.createElement("record");
				recordElem.appendChild(StateHelper.buildTextElement("ID", 
						"" + record.getID(), doc));
				recordElem.appendChild(StateHelper.buildTextElement("batchID", 
						"" + record.getBatchID(), doc));
				recordElem.appendChild(StateHelper.buildTextElement("value", 
						record.getValue(), doc));
				recordElem.appendChild(StateHelper.buildTextElement("fieldID", 
						"" + record.getFieldID(), doc));
				recordElem.appendChild(StateHelper.buildTextElement("rowNum", 
						"" + record.getRowNumber(), doc));
				
				recordsElem.appendChild(recordElem);
			}
			root.appendChild(recordsElem);
		}
	}
	
	/**
	 * Saves the batch image to the doc. This is by far the largest part.
	 */
	private void docBatchImage() {
		if (controller.userHasBatch()) {
			BatchImage bi = controller.getDataModel().getCurrentBatch().getBatchImage();
			Element batchElem = doc.createElement("batchImage");
			
			batchElem.appendChild(StateHelper.buildTextElement("ID", 
					"" + bi.getID(), doc));
			batchElem.appendChild(StateHelper.buildTextElement("projectID", 
					"" + bi.getProjectID(), doc));
			batchElem.appendChild(StateHelper.buildTextElement("imageFilename", 
					bi.getImageLoc(), doc));
			batchElem.appendChild(StateHelper.buildTextElement("completed", 
					"" + bi.isCompleted(), doc));
			batchElem.appendChild(StateHelper.buildTextElement("username", 
					"" + bi.getUsername(), doc));
			
			root.appendChild(batchElem);
		}
	}
	
	/**
	 * Saves the window layout that are not related to working on a batch.
	 * These settings to not require a current batch.
	 */
	private void docWindowLayout() {
		MainFrame m = controller.getMainView();
		int dataSplitterPos = m.getDataArea().getSplitterPos();
		int mainSplitterPos = m.getSplitterPos();
		// TODO add window size, maybe selected tabs
		
		Element windowSettings = doc.createElement("window");
		windowSettings.appendChild(StateHelper.buildTextElement("dataSplitterPos", 
				"" + dataSplitterPos, doc));
		windowSettings.appendChild(StateHelper.buildTextElement("mainSplitterPos", 
				"" + mainSplitterPos, doc));
		root.appendChild(windowSettings);
	}
	
	/**
	 * Saves window layout details that are specific to the current batch, such
	 * as scale (zoom), offset, highlights on/off, inverted state
	 */
	private void docBatchWindowLayout() {
		if (controller.userHasBatch()) {
			MainFrame m = controller.getMainView();
			double scale = m.getViewingArea().getCurrentZoom();
			double offsetX = m.getViewingArea().getOffset().getX();
			double offsetY = m.getViewingArea().getOffset().getY();
			boolean hilightsVisible = false; // TODO implement with highlights
			boolean invertedImage = m.getViewingArea().isInverted();
			
			Element batchSettings = doc.createElement("batchWindow");
			batchSettings.appendChild(StateHelper.buildTextElement("scale", 
					"" + scale, doc));
			batchSettings.appendChild(StateHelper.buildTextElement("offsetX", 
					"" + offsetX, doc));
			batchSettings.appendChild(StateHelper.buildTextElement("offsetY", 
					"" + offsetY, doc));
			batchSettings.appendChild(StateHelper.buildTextElement("hilightVisible", 
					"" + hilightsVisible, doc));
			batchSettings.appendChild(StateHelper.buildTextElement("invertedImage", 
					"" + invertedImage, doc));
			root.appendChild(batchSettings);
		}
	}

	/**
	 * Saves the current batch's fields to the document.
	 */
	private void docFields() {
		if (controller.userHasBatch()) {
			Element fieldsElem = doc.createElement("fields");
			List<Field> fieldsList = controller.getDataModel().getCurrentBatch().getFields();
			for (int i = 0; i < fieldsList.size(); i++) {
				Field field = fieldsList.get(i);
				Element fieldElem = doc.createElement("field");
				fieldElem.appendChild(StateHelper.buildTextElement("ID",
						"" + field.getID(), doc));
				fieldElem.appendChild(StateHelper.buildTextElement("projectID",
						"" + field.getProjectID(), doc));
				fieldElem.appendChild(StateHelper.buildTextElement("title",
						field.getTitle(), doc));
				fieldElem.appendChild(StateHelper.buildTextElement("xCoord",
						"" + field.getXCoord(), doc));
				fieldElem.appendChild(StateHelper.buildTextElement("width",
						"" + field.getWidth(), doc));
				fieldElem.appendChild(StateHelper.buildTextElement("helpHtmlLoc",
						field.getHelpHtmlLoc(), doc));
				fieldElem.appendChild(StateHelper.buildTextElement("knownDataLocation",
						field.getKnownDataLoc(), doc));
				fieldsElem.appendChild(fieldElem);
			}
			root.appendChild(fieldsElem);
			// TODO Optional: include the field help html
		}
	}
	
	/**
	 * Saves the current batch's project to the document.
	 */
	private void docProject() {
		if (controller.userHasBatch()) {
			Element projectElem = doc.createElement("project");
			Project proj = controller.getDataModel().getCurrentProject();
			projectElem.appendChild(StateHelper.buildTextElement("ID",
					"" + proj.getID(), doc));
			projectElem.appendChild(StateHelper.buildTextElement("title",
					proj.getTitle(), doc));
			projectElem.appendChild(StateHelper.buildTextElement("firstYCoord",
					"" + proj.getY(0), doc));
			projectElem.appendChild(StateHelper.buildTextElement("fieldHeight", 
					"" + proj.getRowHeight(), doc));
			projectElem.appendChild(StateHelper.buildTextElement("numRows",
					"" + proj.getRecordsPerImage(), doc));
			root.appendChild(projectElem);
		}
	}
	
	/**
	 * Saves the doc to file, including formatting the xml nicely.
	 * @param file
	 */
	private void saveToFile(File file) throws Exception {
//		System.out.println("Saving to " + file.getName());
		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.setOutputProperty(OutputKeys.INDENT, "yes");
		t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new FileOutputStream(file));
		t.transform(source, result);
	}
}
