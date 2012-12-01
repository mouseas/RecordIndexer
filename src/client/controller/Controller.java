package client.controller;

import java.awt.Dimension;
import java.awt.Image;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import shared.dataTransfer.*;
import client.model.DataModel;
import client.serverCommunicator.*;
import client.views.download.ProjectDialog;
import client.views.login.LoginDialog;
import client.views.mainFrame.MainFrame;

/**
 * Controls the main view. Event listeners call methods on it, such as download
 * batch, and it adjusts the view accordingly.
 * @author Martin Carney
 *
 */
public class Controller {
	
	private MainFrame mainView;
	private LoginDialog loginView;
	private ProjectDialog downloadView;
	private ServerCommunicator sc;
	private DataModel dm;
	
	/**
	 * Constructor. Requires the domain and port number of the server to
	 * connect to.
	 * @param domain
	 * @param port
	 */
	public Controller(String domain, int port) {
		sc = new ServerCommunicator(domain, port);
		dm = new DataModel();
	}
	
	/**
	 * Gets the projectList. Like most functions, requires that a user
	 * be logged in.
	 * @return
	 */
	public List<Project> getProjectList() {
		// TODO this needs to be part of the Download Batch window's MVC system.
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
		// TODO this code needs to be somewhere else, and this function
		// needs to open a Download Batch window.
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
	
	public void login(String username, String password) {
		// TODO this should be part of the Login window's MVC system.
		User user = sc.verifyUser(username, password, true);
		if (user != null && user.getID() >= 0) { // correct login
			if (loginView != null) {
				loginView.dispose();
				loginView = null;
			}
			loadState(user.getUsername());
			if (mainView != null) {
				mainView.setVisible(true);
			}
			String welcome = "Welcome, " + user.getFullName() + ".\n" +
					"You have indexed " + user.getNumIndexedRecords() + " records.";
			JOptionPane.showMessageDialog(mainView, 
					welcome,
					"Welcome",
					JOptionPane.INFORMATION_MESSAGE);
		} else if (user == null) { // probably no server connection
			JOptionPane.showMessageDialog(loginView,
				    "Error connection to server.",
				    "Server Error",
				    JOptionPane.WARNING_MESSAGE);
		} else { // invalid credentials
			JOptionPane.showMessageDialog(loginView,
				    "That username and/or password are incorrect. Try again.",
				    "Login Failed",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Saves the current state for that user, then logs out.
	 * @return Whether the process was successful.
	 */
	public void logout() {
		if (!loggedIn()) { return; } // already logged out
		saveState(sc.getCurrentUser().getUsername());
		sc.logout();
		mainView.setVisible(false);
		createAndShowLoginDialog();
	}
	
	/**
	 * Determines if there is a user logged in.
	 * @return
	 */
	public boolean loggedIn() {
		return sc.getCurrentUser() != null;
	}
	
	/**
	 * Submits a finished batch to the server.
	 */
	public void submitBatch() {
		System.out.println("Submit Batch");
		if (!loggedIn()) { return; }
		if (dm.getCurrentBatch() == null) { return; }
//		sc.submitBatch(dm.getCurrentBatch());
//		dm.setCurrentBatch(null);
//		dm.setCurrentRecordGrid(null);
//		dm.setCurrentProject(null);
	}
	
	public void zoomIn() {
		System.out.println("Zoom in");
		Dimension window = mainView.getSize();
		System.out.println("Width: " + window.width + " Height: " + window.height);
		// TODO implement zoom in
	}
	
	public void zoomOut() {
		System.out.println("Zoom out");
		// TODO implement zoom out
	}

	public void invertImage() {
		System.out.println("Invert image");
		// TODO implement invertImage
		
	}

	public void toggleHilight() {
		System.out.println("Toggle highlights");
		// TODO implement toggleHilight
		
	}

	public void exit() {
		// TODO close the program.
		System.out.println("Exit.");
		mainView.dispose();
	}
	
	public MainFrame getMainView() {
		return mainView;
	}

	public void setMainView(MainFrame view) {
		this.mainView = view;
	}

	public LoginDialog getLoginView() {
		return loginView;
	}

	public void setLoginView(LoginDialog loginView) {
		this.loginView = loginView;
	}

	public ProjectDialog getDownloadView() {
		return downloadView;
	}

	public void setDownloadView(ProjectDialog downloadView) {
		this.downloadView = downloadView;
	}

	public void save() {
		// TODO Auto-generated method stub
		if (!loggedIn()) { return; }
		saveState(sc.getCurrentUser().getUsername());
	}

	public void openDownloadWindow() {
		downloadProjectList();
		List<Project> projects = dm.getProjects();
		ProjectDialog downloadDialog = new ProjectDialog(mainView, this, projects);
		setDownloadView(downloadDialog);
		downloadDialog.setVisible(true);
	}

	public void createAndShowLoginDialog() {
		LoginDialog loginDialog = new LoginDialog(mainView, this);
		setLoginView(loginDialog);
		loginDialog.setVisible(true);
	}

	/**
	 * Loads the user's state from local file, if any is on file. If not,
	 * loads the default settings.
	 * @param username
	 */
	private void loadState(String username) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Gets the fields (aka columns) for the current batch.
	 * @return
	 */
	private List<Field> getCurrentFields() {
		if (dm.getCurrentBatch() == null) { return null; }
		return dm.getCurrentBatch().getFields();
	}
	
	/**
	 * Gets the field help in String format for a specified column
	 * @param column
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getFieldHelp(int column) {
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
	@SuppressWarnings("unused")
	private Image getImage() {
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
	 * Gets a 1D list of records associated with the current batch.
	 * This is useful for loop processing.
	 * @return
	 */
	@SuppressWarnings("unused")
	private List<Record> getCurrentRecordList() {
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

	private void saveState(String username) {
		System.out.println("Save current state");
		// TODO implement save.
		
	}

	
	
	
}
