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
	
}
