package controller;

import java.awt.Image;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import javax.imageio.ImageIO;

import shared.dataTransfer.*;
import client.model.DataModel;
import client.serverCommunicator.*;

/**
 * Holds a model of the data which may be transferred to the server,
 * saved, loaded, et cetera.
 * @author Martin
 *
 */
public class Controller {
	
	private ServerCommunicator sc;
	private DataModel dm;
	
	public Controller(String domain, int port) {
		sc = new ServerCommunicator(domain, port);
		dm = new DataModel();
	}
	
	public boolean login(String username, String password) {
		User user = sc.verifyUser(username, password, true);
		if (user != null) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Determines if there is a user logged in.
	 * @return
	 */
	public boolean loggedIn() {
		return sc.getCurrentUser() != null;
	}
	
	/**
	 * Gets the projectList. Like most functions, requires that a user
	 * be logged in.
	 * @return
	 */
	public List<Project> getProjectList() {
		if (!loggedIn()) { return null; }
		if (dm.getProjects() == null) { downloadProjectList(); }
		return dm.getProjects();
	}
	
	/**
	 * Downloads a batch along with the other needed information from 
	 * the current project.
	 * @return Whether the download was successful.
	 */
	public boolean downloadNextBatch(Project p) {
		if (!loggedIn()) { return false; }
		if (p == null) { return false; }
		if (dm.getCurrentBatch() != null) { return false; } // already have a batch!
		
		// download the needed parts
		BatchImage batchImage = sc.requestNextBatch(p.getID());
		Batch batch = new Batch(batchImage);
		List<Field> fields = sc.requestFieldsList(p);
		batch.setFields(fields);
		int rows = p.getRecordsPerImage();
		int columns = fields.size();
		
		// generate empty records in 2D array, and in Batch object
		Record[][] records = new Record[columns][rows];
		for (int column = 0; column < records.length; column++) {
			for (int row = 0; row < records[column].length; row++) {
				records[column][row] = new Record(
						(row * columns) + column, 
						batch.batchImage.getID(), 
						fields.get(column).getID(),
						row, "");
				batch.addRecord(records[column][row]);
			}
		}

		if (p == null || batch == null || fields == null) { return false; } // something went wrong
		
		// if no problems along the way, set the current references and return true.
		dm.setCurrentProject(p);
		dm.setCurrentBatch(batch);
		dm.setCurrentRecordGrid(records);
		return true;
	}
	
	/**
	 * Saves the current state for that user, then logs out.
	 * @return Whether the process was successful.
	 */
	public boolean logout() {
		if (!loggedIn()) { return false; }
		boolean result = true;
		// TODO save the current batch (if any) to file
		result &= sc.logout();
		
		return result;
	}
	
	/**
	 * Submits a finished batch to the server.
	 */
	public void submitBatch() {
		if (!loggedIn()) { return; }
		if (dm.getCurrentBatch() == null) { return; }
		sc.submitBatch(dm.getCurrentBatch());
		dm.setCurrentBatch(null);
		dm.setCurrentRecordGrid(null);
		dm.setCurrentProject(null);
	}

	/**
	 * Gets the current batch
	 * @return
	 */
	public Batch getCurrentBatch() {
		return dm.getCurrentBatch();
	}

	/**
	 * Gets the project associated with the current batch.
	 * @return
	 */
	public Project getCurrentProject() {
		return dm.getCurrentProject();
	}

	/**
	 * Gets the fields (aka columns) for the current batch.
	 * @return
	 */
	public List<Field> getCurrentFields() {
		if (dm.getCurrentBatch() == null) { return null; }
		return dm.getCurrentBatch().getFields();
	}
	
	/**
	 * Gets the field help in String format for a specified column
	 * @param column
	 * @return
	 */
	public String getFieldHelp(int column) {
		if (dm.getCurrentBatch() == null || // verify inputs
				column < 0 || 
				column >= getCurrentFields().size()) {
			return null;
		}
		// download the html file to a String
		String location = getCurrentFields().get(column).getHelpHtmlLoc();
		if (location == null || location.length() <= 0) { return null; }
		StringBuilder result = new StringBuilder();
		InputStream stream = getFileFromServer(location);
		Scanner in = new Scanner(stream);
		while (in.hasNext()) {
			result.append(in.nextLine() + "\n");
		}
		// return it
		return result.toString();
	}
	
	/**
	 * Gets the batch's image. Downloads it from the server if it has 
	 * not already been downloaded this session.
	 * @return
	 */
	public Image getImage() {
		if (dm.getCurrentBatch() == null) {
			return null;
		}
		if (dm.getCurrentBatchImage() != null) {
			return dm.getCurrentBatchImage(); // already have it.
		}
		try {
			String location = dm.getCurrentBatch().batchImage.getImage().getFilename();
			InputStream stream = getFileFromServer(location);
			Image result = ImageIO.read(stream);
			dm.setCurrentBatchImage(result);
			return result;
		} catch (Exception e) {
			System.out.println("Error while reading image from stream.");
			System.out.println(e.getMessage());
			return null;
		}
	}

	/**
	 * Gets a 2D array of the records associated with the current batch.
	 * This is meant for easy, logical access for the GUI table.
	 * It is organized: records[column][row].
	 * @return
	 */
	public Record[][] getCurrentRecordArray() {
		return dm.getCurrentRecordGrid();
	}
	
	/**
	 * Gets a 1D list of records associated with the current batch.
	 * This is useful for loop processing.
	 * @return
	 */
	public List<Record> getCurrentRecordList() {
		if (dm.getCurrentBatch() == null) { return null; }
		return dm.getCurrentBatch().getRecords();
	}
	
	/**
	 * Downloads the latest project list from the server.
	 */
	private void downloadProjectList() {
		if (!loggedIn()) { return; }
		dm.setProjects(sc.requestProjectsList());
	}
	
	/**
	 * Connects to the server and gets a a stream for the request URL.
	 * Used to download images, help text, and field dictionaries.
	 * @param urlStr The URL string to get. This is usually provided by
	 * the ServerCommunicator.
	 * @return A valid InputStream or null if there was a problem.
	 */
	private InputStream getFileFromServer(String urlStr) {
		try {
			URL url = new URL(urlStr);
			return url.openStream();
		} catch (Exception e) {
			System.out.println("Error while getting an inputstream from the server.");
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	
	
}
