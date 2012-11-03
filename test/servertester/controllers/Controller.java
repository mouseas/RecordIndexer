package servertester.controllers;

import java.util.*;

import client.serverCommunicator.ServerCommunicator;
import shared.dataTransfer.*;

import servertester.views.*;

public class Controller implements IController {

	private IView _view;
	
	private ServerCommunicator sc;
	
	public Controller() {
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
	
	private void validateUser() {
		setHostAndPort();
		String username = getView().getParameterValues()[0];
		String password = getView().getParameterValues()[1];
		getView().setRequest(username + "\n" 
				+ password + "\n");
		
		User user = sc.verifyUser(username, password);
		if (user == null) {
			getView().setResponse("FALIED\n");
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
		String username = getView().getParameterValues()[0];
		String password = getView().getParameterValues()[1];
		getView().setRequest(username + "\n" + password + "\n");
	}
	
	private void getSampleImage() {
		setHostAndPort();
		String username = getView().getParameterValues()[0];
		String password = getView().getParameterValues()[1];
	}
	
	private void downloadBatch() {
		setHostAndPort();
		String username = getView().getParameterValues()[0];
		String password = getView().getParameterValues()[1];
	}
	
	private void getFields() {
		setHostAndPort();
		String username = getView().getParameterValues()[0];
		String password = getView().getParameterValues()[1];
	}
	
	private void submitBatch() {
		setHostAndPort();
		String username = getView().getParameterValues()[0];
		String password = getView().getParameterValues()[1];
	}
	
	private void search() {
		setHostAndPort();
		String username = getView().getParameterValues()[0];
		String password = getView().getParameterValues()[1];
	}

}

