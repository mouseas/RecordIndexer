package client.controller;

import java.awt.Image;
import java.io.*;
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
public class MainController {
	
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
	public MainController(String domain, int port) {
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
	public void downloadNextBatch(Project p) {
		if (!loggedIn()) { return; }
		if (p == null) { return; }
		if (dm.getCurrentBatch() != null) { return; } // already have a batch!
		
		downloadBatchToModel(p);
		downloadImageToModel();
		placeImageInView();
		
		if (downloadView != null) {
			downloadView.dispose();
			downloadView = null;
		}
	}

	/**
	 * Logs a user in from the login screen.
	 * @param username
	 * @param password
	 */
	public void login(String username, String password) {
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
				    "Error connecting to server.\n" +
				    "Most likely the server is not running.",
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
		openLoginDialog();
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
	
	/**
	 * Zooms the view in by one step.
	 */
	public void zoomIn() {
		double previousScale = mainView.getViewingArea().getCurrentZoom();
		double newScale = previousScale / 0.9;
		if (newScale > 0.95 && newScale < 1.05) { newScale = 1.0; } // close to 1
		mainView.getViewingArea().setZoom(newScale);
	}
	
	/**
	 * Zooms out one step.
	 */
	public void zoomOut() {
		double previousScale = mainView.getViewingArea().getCurrentZoom();
		double newScale = previousScale * 0.9;
		if (newScale > 0.95 && newScale < 1.05) { newScale = 1.0; } // close to 1
		mainView.getViewingArea().setZoom(newScale);
	}

	public void invertImage() {
		mainView.getViewingArea().invertImage();
		
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

	public void openDownloadDialog() {
		downloadProjectList();
		List<Project> projects = dm.getProjects();
		ProjectDialog downloadDialog = new ProjectDialog(mainView, this, projects);
		setDownloadView(downloadDialog);
		downloadDialog.setVisible(true);
	}
	
	public void closeDownloadDialog() {
		if (downloadView != null) {
			downloadView.dispose();
			downloadView = null;
		}
	}

	public void openLoginDialog() {
		LoginDialog loginDialog = new LoginDialog(mainView, this);
		setLoginView(loginDialog);
		loginDialog.setVisible(true);
	}

	public void viewSample(Project p) {
		SampleController s = new SampleController(sc, p);
		s.buildAndOpenViewer();
	}
	
	/**
	 * Part of the Download Next Batch process, this method does the actual
	 * downloading, and loads the values into the controller's DataModel 
	 * object.
	 * @param p Project to download a batch from.
	 */
	private void downloadBatchToModel(Project p) {
		// download the needed parts
		BatchImage batchImage = sc.requestNextBatch(p.getID());
		if (batchImage == null) { 
			System.out.println("");
			return; // something failed.
		} 
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

		if (p == null || batch == null || fields == null) { return; } // something went wrong
		
		// if no problems along the way, set the current references and return true.
		mainView.getMenubar().setDownloadEnabled(false);
		dm.setCurrentProject(p);
		dm.setCurrentBatch(batch);
		dm.setCurrentRecordGrid(records);
	}

	/**
	 * Part of the batch download process, this downloads the image file and
	 * saves it to the DataModel.
	 */
	private void downloadImageToModel() {
		if (dm.getCurrentBatch() == null) { return; }
		String urlStr = dm.getCurrentBatch().batchImage.getImage().getFilename();
//		System.out.println(urlStr);
		InputStream inStream = getFileFromServer(urlStr);
		Image result = null;
		try {
			result = ImageIO.read(inStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (result != null) {
			dm.setCurrentBatchImage(result);
		}
	}
	
	/**
	 * Part of the batch download process, this places the downloaded image
	 * into the ViewArea of the MainFrame window.
	 */
	private void placeImageInView() {
		if (dm.getCurrentBatchImage() == null) { return; }
		Image image = dm.getCurrentBatchImage();
		mainView.getViewingArea().setImage(image);
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