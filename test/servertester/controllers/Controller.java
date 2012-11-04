package servertester.controllers;

import java.util.*;

import client.serverCommunicator.ServerCommunicator;
import shared.dataTransfer.*;

import servertester.views.*;

public class Controller implements IController {

	private IView _view;
	
	private ServerCommunicator sc;
	private String username;
	private String password;
	
	private static final String FAIL = "FAILED\n";
	
	public Controller() {
		username = null;
		password = null;
		return;
	}
	
	public IView getView() {
		return _view;
	}
	
	public void setView(IView value) {
		_view = value;
	}
	
	// IController methods
	//
	
	@Override
	public void initialize() {
		getView().setHost("localhost");
		getView().setPort("39640");
		operationSelected();
		sc = new ServerCommunicator(getView().getHost(), 
				Integer.parseInt(getView().getPort()));
	}

	@Override
	public void operationSelected() {
		ArrayList<String> paramNames = new ArrayList<String>();
		paramNames.add("User");
		paramNames.add("Password");
		
		switch (getView().getOperation()) {
		case VALIDATE_USER:
			break;
		case GET_PROJECTS:
			break;
		case GET_SAMPLE_IMAGE:
			paramNames.add("Project");
			break;
		case DOWNLOAD_BATCH:
			paramNames.add("Project");
			break;
		case GET_FIELDS:
			paramNames.add("Project");
			break;
		case SUBMIT_BATCH:
			paramNames.add("Batch");
			paramNames.add("Record Values");
			break;
		case SEARCH:
			paramNames.add("Fields");
			paramNames.add("Search Values");
			break;
		default:
			assert false;
			break;
		}
		
		getView().setRequest("");
		getView().setResponse("");
		getView().setParameterNames(paramNames.toArray(new String[paramNames.size()]));
	}

	@Override
	public void executeOperation() {
		switch (getView().getOperation()) {
		case VALIDATE_USER:
			validateUser();
			break;
		case GET_PROJECTS:
			getProjects();
			break;
		case GET_SAMPLE_IMAGE:
			getSampleImage();
			break;
		case DOWNLOAD_BATCH:
			downloadBatch();
			break;
		case GET_FIELDS:
			getFields();
			break;
		case SUBMIT_BATCH:
			submitBatch();
			break;
		case SEARCH:
			search();
			break;
		default:
			assert false;
			break;
		}
	}

	
	private void setHostAndPort() {
		sc.setHost(getView().getHost());
		sc.setPort(Integer.parseInt(getView().getPort()));
	}
	
	private String setUsernameAndPassword() {
		username = getView().getParameterValues()[0];
		password = getView().getParameterValues()[1];
		return username + "\n" + password + "\n";
	}
	
	private void validateUser() {
		setHostAndPort();
		String request = setUsernameAndPassword();
		getView().setRequest(request);
		
		User user = sc.verifyUser(username, password);
		if (user == null) {
			failedResponse();
		} else if (user.getID() < 0) {
			getView().setResponse("FALSE\n");
		} else {
			getView().setResponse("TRUE\n" 
					+ user.getFirstName() + "\n"
					+ user.getLastName() + "\n" 
					+ user.getNumIndexedRecords() + "\n");
		}
		sc.logout();
	}
	
	private void getProjects() {
		setHostAndPort();
		String request = setUsernameAndPassword();
		getView().setRequest(request);
		
		User user = sc.verifyUser(username, password);
		if (user == null || user.getID() < 0) {
			failedResponse();
		} else {
			List<Project> projects = sc.requestProjectsList();
			if (projects == null || projects.size() < 1) {
				failedResponse();
			} else {
				StringBuilder sb = new StringBuilder();
				for (Project p : projects) {
					sb.append(p.getID() + "\n");
					sb.append(p.getTitle() + "\n");
				}
				getView().setResponse(sb.toString());
			}
		}
		sc.logout();
	}
	
	private void getSampleImage() {
		setHostAndPort();
		String projectIDString = getView().getParameterValues()[2];
		String request = setUsernameAndPassword() + projectIDString + "\n";
		getView().setRequest(request);
		
		try {
			User user = sc.verifyUser(username, password);
			if (user == null || user.getID() < 0) {
				getView().setResponse(FAIL);
			} else {
				Project p = new Project(Integer.parseInt(projectIDString),
						0, 0, 0, "n/a"); // dummy project with project id. In the actual
						// app, a project from the Project List will be used.
				Image image = sc.requestSampleImage(p);
				if (image != null) {
					getView().setResponse(filenameHeader() + image.getFilename() + "\n");
				} else {
					failedResponse();
				}
			}
		} catch (NumberFormatException e) {
			failedResponse();
		}
	}
	
	private void downloadBatch() {
		setHostAndPort();
		String request = setUsernameAndPassword();
	}
	
	private void getFields() {
		setHostAndPort();
		String projectIDString = getView().getParameterValues()[2];
		String request = setUsernameAndPassword() + projectIDString + "\n";
		getView().setRequest(request);
		
		try {
			User user = sc.verifyUser(username, password);
			if (user != null && user.getID() >= 0) {
				Project p = new Project(Integer.parseInt(projectIDString), 0, 0, 0, "n/a");
				List<Field> fields = sc.requestFieldsList(p);
				if (fields == null || fields.size() < 1) {
					failedResponse();
				} else {
					StringBuilder sb = new StringBuilder();
					for (Field f : fields) {
						sb.append(f.getProjectID() + "\n");
						sb.append(f.getID() + "\n");
						sb.append(f.getTitle() + "\n");
					}
					getView().setResponse(sb.toString());
				}
			} else {
				failedResponse();
			}
		} catch (Exception e) {
			failedResponse();
		}
	}
	
	private void submitBatch() {
		setHostAndPort();
		String request = setUsernameAndPassword();
	}
	
	private void search() {
		setHostAndPort();
		String request = setUsernameAndPassword() 
						+ getView().getParameterValues()[2] + "\n" 
						+ getView().getParameterValues()[3] + "\n";
		getView().setRequest(request);
		
		User user = sc.verifyUser(username, password);
		if (user != null && user.getID() >= 0) {
			List<Field> fields = buildDummyFieldList(getView().getParameterValues()[2]);
			List<String> searchTerms = 
							convertToList(getView().getParameterValues()[3].split(","));
			List<Record> records = sc.searchRecords(fields, searchTerms);
			if (fields != null && searchTerms != null && records != null) {
				StringBuilder sb = new StringBuilder();
				for (Record r : records) {
					sb.append(r.getBatchID() + "\n");
					Batch b = sc.requestSpecificBatch(r.getBatchID());
					// it would be more efficient for many searches to put these in a
					// hash table and only request them the first time that ID is needed.
					String filename = b.getImage().getFilename();
					sb.append(filenameHeader() + filename + "\n");
					sb.append(r.getID() + "\n");
					sb.append(r.getFieldID() + "\n");
				}
				getView().setResponse(sb.toString());
			} else {
				failedResponse();
			}
		} else {
			failedResponse();
		}
		
	}
	
	/**
	 * Builds a list of dummy Field objects to use in the Search method of the
	 * Server Communicator. Each Field only has a valid ID; everything else is invalid.
	 * @return
	 */
	private List<Field> buildDummyFieldList(String source) {
		if (source == null) { return null; }
		int[] fieldIDs = splitIntString(source);
		if (fieldIDs == null) { return null; }
		List<Field> fields = new ArrayList<Field>();
		for (int i = 0; i < fieldIDs.length; i++) {
			Field f = new Field(fieldIDs[i], 0, "", 0, 0, "", "");
			fields.add(f);
		}
		return fields;
	}
	
	/**
	 * Splits a string by commas, and converts each string into an integer.
	 * @param input
	 * @return array of integers extracted from the string, or null if there
	 * where any errors parsing a string to integer.
	 */
	private int[] splitIntString(String input) {
		if (input == null) { return null; }
		String[] strings = input.split(",");
		int[] result = new int[strings.length];
		try {
			for (int i = 0; i < strings.length; i++) {
				result[i] = Integer.parseInt(strings[i]);
			}
		} catch (NumberFormatException e) {
			return null;
		}
		return result;
	}
	
	private List<String> convertToList(String[] input) {
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < input.length; i++) {
			result.add(input[i]);
		}
		return result;
	}
	
	private String filenameHeader() {
		return "http://" + sc.getHost() + ":" + sc.getPort() + "/get/";
	}
	
	private void failedResponse() {
		getView().setResponse(FAIL);
	}

}

