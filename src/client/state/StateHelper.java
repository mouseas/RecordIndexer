package client.state;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import client.controller.MainController;

/**
 * Contains helper functions used by StateSaver and StateLoader
 * @author Martin Carney
 *
 */
public class StateHelper {
	
	/**
	 * Gets a file reference to the user-specific data file
	 * @return
	 */
	public static File getUserSaveFile(MainController controller) {
		File userFolder = new File("users");
		if (!userFolder.exists()) {
			userFolder.mkdir(); // make the directory if it doesn't exist.
		}
		String username = controller.getServerCommunicator().getCurrentUser().getUsername();
		File result = new File("users/" + username + ".dat");
		return result;
	}
	
	/**
	 * Builds a single text leaf node with a name and contents.
	 * @param elemName
	 * @param elemText
	 * @return
	 */
	public static Element buildTextElement(String elemName, String elemText, Document doc) {
		Element textElem = doc.createElement(elemName);
		textElem.appendChild(doc.createTextNode(elemText));
		return textElem;
	}
	
	/**
	 * Takes a parent node and the name of the leaf to check for, and returns
	 * the text of the leaf node, or "" if not found.
	 * @param elem
	 * @param leafName
	 * @return
	 */
	public static String getStringFromParent(Element elem, String leafName) {
		Element leaf = (Element)(elem.getElementsByTagName(leafName).item(0));
		String result;
		if (leaf != null) {
			result = leaf.getTextContent().trim();
		} else {
			result = "";
		}
		return result;
	}
	
	/**
	 * Takes a parent node and the name of the leaf to check for, and returns
	 * the parsed int of the leaf node, or -1 if not found.
	 * @param elem
	 * @param leafName
	 * @return
	 */
	public static int getIntFromParent(Element elem, String leafName) {
		Element leaf = (Element)(elem.getElementsByTagName(leafName).item(0));
		int result = -1;
		try {
			result = Integer.parseInt(leaf.getTextContent().trim());
		} catch (Exception e) {}
		
		return result;
	}
	
}
