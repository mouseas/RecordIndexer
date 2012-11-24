package client.dataModel;

import java.util.*;

import shared.dataTransfer.*;
import client.serverCommunicator.*;
import client.gui.mainFrame.MainFrameDimensions;

/**
 * Holds a model of the data which may be transferred to the server,
 * saved, loaded, et cetera.
 * @author Martin
 *
 */
public class DataModel {
	
	private ServerCommunicator sc;
	
	private Batch currentBatch;
	private Project currentProject;
	
	private MainFrameDimensions savedDimensions;
	
	private List<Project> projectList;
	
	public DataModel(String domain, int port) {
		sc = new ServerCommunicator(domain, port);
	}
	
	public boolean login(String username, String password) {
		User user = sc.verifyUser(username, password, true);
		if (user != null) {
			return true;
		} else {
			return false;
		}
	}
	
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
		if (projectList == null) { downloadProjectList(); }
		return projectList;
	}
	
	/**
	 * 
	 */
	private void downloadProjectList() {
		if (!loggedIn()) { return; }
		projectList = sc.requestProjectsList();
	}
	
	/**
	 * Downloads a batch along with the other needed information from the current project.
	 */
	public void downloadBatch(Project p) {
		if (!loggedIn()) { return; }
		if (p == null) { return; }
		currentProject = p;
		BatchImage batchImage = sc.requestNextBatch(p.getID());
		Batch batch = new Batch(batchImage);
		List<Field> fields = sc.requestFieldsList(p);
		int rows = p.getRecordsPerImage();
		int columns = fields.size();
		
		// generate empty records in 2D array
		Record[][] records = new Record[columns][rows];
		for (int column = 0; column < records.length; column++) {
			for (int row = 0; row < records[column].length; row++) {
				records[column][row] = new Record(
						(row * columns) + column, 
						batch.batchImage.getID(), 
						fields.get(column).getID(),
						row, "");
			}
		}
	}
	
	/**
	 * Loads any saved window state and batch associated with the current user.
	 * If this is the user's first time logging in, loads the defaults.
	 */
	public void loadState() {
		if (!loggedIn()) { return; }
		
	}
	
	/**
	 * Gets the saved dimensions, which can be modified if the window is being
	 * resized/modified.
	 * @return
	 */
	public MainFrameDimensions getMFDimensions() {
		return savedDimensions;
	}
	
	/**
	 * Saves the current state for that user, then logs out.
	 * @return Whether the process was successful.
	 */
	public boolean logout() {
		if (!loggedIn()) { return false; }
		boolean result = true;
		// TODO save the current state and batch (if any) to file
		result &= sc.logout();
		
		return result;
	}
	
	/**
	 * Submits a finished batch
	 */
	public void submitBatch() {
		if (!loggedIn()) { return; }
		if (currentBatch == null) { return; }
		sc.submitBatch(currentBatch);
	}
	
	
	
}
