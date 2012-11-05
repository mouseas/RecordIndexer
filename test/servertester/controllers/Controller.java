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
		sc.logout();
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
		String projectIDString = getView().getParameterValues()[2];
		String request = setUsernameAndPassword() + projectIDString + "\n";
		getView().setRequest(request);
		try {
			User user = sc.verifyUser(username, password);
			if (user == null || user.getID() < 0) {
				getView().setResponse(FAIL);
			} else {
				Batch batch = sc.requestNextBatch(Integer.parseInt(projectIDString));
				List<Field> fields = sc.requestFieldsList(new Project(
									Integer.parseInt(projectIDString), 0, 0, 0, ""));
				List<Project> projects = sc.requestProjectsList();
				if (batch != null && fields != null && fields.size() > 0 
								&& projects != null && projects.size() > 0) {
					Project p = projects.get(batch.getProjectID());
					StringBuilder sb = new StringBuilder();
					outputBatch(sb, batch, p, fields);
					outputFields(sb, fields);
					getView().setResponse(sb.toString());
				} else {
					failedResponse();
				}
			}
		} catch (NumberFormatException e) {
			failedResponse();
		}
	}
	
	/**
	 * Appends the strings needed to represent a downloaded batch to a StringBuilder.
	 * @param sb
	 * @param batch
	 * @param p
	 * @param fields
	 */
	private void outputBatch(StringBuilder sb, Batch batch, Project p, List<Field> fields) {
		sb.append(batch.getID() + "\n");
		sb.append(batch.getProjectID() + "\n");
		sb.append(filenameHeader() + batch.getImage().getFilename() + "\n");
		sb.append(p.getY(0) + "\n");
		sb.append(p.getRowHeight() + "\n");
		sb.append(p.getRecordsPerImage() + "\n");
		sb.append(fields.size() + "\n");
		sb.append(batch.getID() + "\n");
	}
	
	/**
	 * Appends the strings needed to represent a list of Fields to a StringBuilder.
	 * @param sb
	 * @param fields
	 */
	private void outputFields (StringBuilder sb, List<Field> fields) {
		for (int i = 0; i < fields.size(); i++) {
			Field f = fields.get(i);
			sb.append(f.getID() + "\n");
			sb.append((i + 1) + "\n");
			sb.append(f.getTitle() + "\n");
			if (f.getHelpHtmlLoc().length() > 0) {
				sb.append(filenameHeader() + f.getHelpHtmlLoc());
			}
			sb.append("\n");
			sb.append(f.getXCoord() + "\n");
			sb.append(f.getWidth() + "\n");
			if (f.getKnownDataLoc().length() > 0) {
				sb.append(filenameHeader() + f.getKnownDataLoc());
			}
			sb.append("\n");
		}
	}
	
	private void getFields() {
		setHostAndPort();
		String projectIDString = getView().getParameterValues()[2];
		String request = setUsernameAndPassword() + projectIDString + "\n";
		getView().setRequest(request);
		String response = null;
		
		try {
			User user = sc.verifyUser(username, password);
			if (user != null && user.getID() >= 0) {
				if (projectIDString != null && projectIDString.length() > 0) {
					response = getProjectFields(Integer.parseInt(projectIDString));
				} else { // no project given; return ALL fields.
					response = getAllFields();
				}
				getView().setResponse(response);
			} else {
				failedResponse();
			}
		} catch (Exception e) {
			failedResponse();
		}
	}
	
	/**
	 * Used by getFields(), gets all the fields when no project is specified.
	 * @return
	 */
	private String getAllFields() {
		StringBuilder sb = new StringBuilder();
		List<Project> projects = sc.requestProjectsList();
		for (Project p : projects) {
			sb.append(getProjectFields(p.getID()));
		}
		return sb.toString();
	}
	
	/**
	 * Used by getFields(), gets just the fields for the specified project.
	 * getAllFields() also uses this to get all fields project-by-project.
	 * @param projectID
	 * @return
	 */
	private String getProjectFields(int projectID) {
		Project p = new Project(projectID, 0, 0, 0, "n/a");
		List<Field> fields = sc.requestFieldsList(p);
		if (fields == null || fields.size() < 1) {
			return FAIL;
		} else {
			StringBuilder sb = new StringBuilder();
			for (Field f : fields) {
				sb.append(f.getProjectID() + "\n");
				sb.append(f.getID() + "\n");
				sb.append(f.getTitle() + "\n");
			}
			return sb.toString();
		}
	}
	
	private void submitBatch() {
		setHostAndPort();
		String batchIDString = getView().getParameterValues()[2];
		String recordValuesString = getView().getParameterValues()[3];
		List<String> recordValues = convertToList(recordValuesString.split(","));
		String request = setUsernameAndPassword() + batchIDString + "\n"
						+ recordValuesString + "\n";
		getView().setRequest(request);
		
		if (recordValuesString.length() < 1) {
			failedResponse(); // no values to submit. Failed.
			return;
		}
		
		try {
			User user = sc.verifyUser(username, password);
			FinishedBatch fb = buildFinishedBatch(batchIDString, recordValues);
			if (user != null && user.getID() >= 0 && fb != null) {
				boolean success = sc.submitBatch(fb);
				if (success) {
					getView().setResponse("TRUE\n");
				} else {
					failedResponse();
				}
			} else {
				failedResponse();
			}
		} catch (Exception e) {
			failedResponse();
		}
	}
	
	private FinishedBatch buildFinishedBatch(String batchIDString,
			List<String> recordValues) {
		try {
			Batch b = sc.requestSpecificBatch(Integer.parseInt(batchIDString));
			FinishedBatch result = new FinishedBatch(b);
			result.setFields(sc.requestFieldsList(
									new Project(b.getProjectID(), 0, 0, 0, "")));
			List<Record> records = result.getRecords();
			if (result.getFields() != null && result.getFields().size() > 0 
					&& b != null && b.getUsername().equals(username)) {
				for (int i = 0; i < recordValues.size(); i++) {
					int fieldID = result.getFields().get(i % result.getFields().size()).getID();
					int rowNumber = (int)i / result.getFields().size();
					Record r = new Record(i, b.getID(), fieldID, rowNumber, recordValues.get(i));
					records.add(r);
				}
				return result;
			} else {
				return null;
			}
		} catch (Exception e) { /* return null below */ }
		return null;
	}

	private void search() {
		setHostAndPort();
		String request = setUsernameAndPassword() 
						+ getView().getParameterValues()[2] + "\n" 
						+ getView().getParameterValues()[3] + "\n";
		getView().setRequest(request);
		
		User user = sc.verifyUser(username, password);
//		System.out.println("\t" + user.getUsername() + " " + user.getID());
		if (user != null && user.getID() >= 0) {
			List<Field> fields = buildDummyFieldList(getView().getParameterValues()[2]);
			List<String> searchTerms = 
							convertToList(getView().getParameterValues()[3].split(","));
			List<Record> records = sc.searchRecords(fields, searchTerms);
			if (fields != null && searchTerms != null 
							&& records != null && records.size() > 0) {
				StringBuilder sb = new StringBuilder();
				for (Record r : records) {
					sb.append(r.getBatchID() + "\n");
					Batch b = sc.requestSpecificBatch(r.getBatchID());
					// it would be more efficient for many searches to put these in a
					// hash table and only request them the first time that ID is needed.
					if (b != null) {
						String filename = b.getImage().getFilename();
						sb.append(filenameHeader() + filename + "\n");
					} else {
						sb.append("File name error\n");
					}
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
			result.add(input[i].replace(" ", "%20"));
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

