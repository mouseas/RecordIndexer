package client.serverCommunicator;

import java.util.*;
import java.net.*;
import java.io.*;

import shared.dataTransfer.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ServerCommunicator {
	
	/**
	 * Holds a User once a valid result has been returned by verifyUser().
	 * This user object is held as long as the user is logged in, and is used
	 * in subsequent SQL requests.
	 */
	private User currentUser;
	
	/**
	 * The Domain to connect to for this ServerCommunicator
	 */
	private String domain;
	
	/**
	 * The port number to connect to for this ServerCommunicator
	 */
	private int port;
	
	private XStream xstream;
	
	private static final String HTTP = "http";
	
	/**
	 * Constructor for the ServerCommunicator. Takes in a domain and port number
	 * for use in constructing URLs to request and send information.
	 * @param domain
	 * @param port
	 */
	public ServerCommunicator(String domain, int port) {
		this.domain = domain;
		this.port = port;
		xstream = new XStream(new DomDriver());
	}
	
	/**
	 * Verifies a username + password combo, and returns the user's info (or null
	 * if username/password was incorrect.
	 * @param username Username supplied by the user
	 * @param password Password supplied by the user
	 * @param setCurrentUser Whether to set currentUser to the result of this 
	 * method. Only changes currentUser if the result is not null.
	 * @return A user object with the validated user's information, or null for an
	 * invalid username or password
	 */
	public User verifyUser(String username, String password, boolean setCurrentUser) {
		try {
			URL url = new URL(HTTP, domain, port, 
					"/login?username=" + username + "&password=" + password);
			Object xstreamResult = processRequest(url);
			User result = (User)xstreamResult;
			if (setCurrentUser) { currentUser = result; }
			return result;
		} catch (MalformedURLException e) {
			System.out.println("Something wrong with the Project List url.");
			System.out.println(e.getMessage());
		}
		return null; // if there was an error.
		
	}
	
	/**
	 * Verifies a username + password combo, and returns the user's info (or null
	 * if username/password was incorrect. Also sets the current user to the result
	 * if not null.
	 * @param username Username supplied by the user
	 * @param password Password supplied by the user
	 * @return A user object with the validated user's information, or null for an
	 * invalid username or password
	 */
	public User verifyUser(String username, String password) {
		return verifyUser(username, password, true);
	}
	
	/**
	 * Requests a list of all the Projects available for indexing
	 * @return All projects currently available to the user for indexing.
	 */
	@SuppressWarnings("unchecked")
	public List<Project> requestProjectsList() {
		try {
			URL url = new URL(HTTP, domain, port, 
					"/project-list" + usernameAndPasswordForURLS());
			Object xstreamResult = processRequest(url);
			return (List<Project>)xstreamResult;
		} catch (MalformedURLException e) {
			System.out.println("Something wrong with the Project List url.");
			System.out.println(e.getMessage());
		}
		return null; // if there was an error.
	}
	
	/**
	 * Requests a sample image for a given project, to help the user decide if
	 * this project is right for them.
	 * @param p The project to request a sample from
	 * @return An image from the project.
	 */
	public Image requestSampleImage(Project p) {
		try {
			URL url = new URL(HTTP, domain, port, 
					"/sample-image" + usernameAndPasswordForURLS() + 
					"&project=" + p.getID());
			Object xstreamResult = processRequest(url);
			Batch b = (Batch)xstreamResult;
			return b.getImage();
		} catch (MalformedURLException e) {
			System.out.println("Something wrong with the Sample Image url.");
			System.out.println(e.getMessage());
		}
		return null; // if there was an error.
	}
	
	/**
	 * Requests a batch for a given project. The batch data from the server 
	 * should include an image url and record information, if any.
	 * @param p The project to request a batch from
	 * @return The batch received, or null if no batch received.
	 */
	public Batch requestBatch(int projectID) {
		try {
			URL url = new URL(HTTP, domain, port, 
					"/get-next-batch" + usernameAndPasswordForURLS() + 
					"&project=" + projectID);
			System.out.println(url.toString());
			Object xstreamResult = processRequest(url);
			return (Batch)xstreamResult;
		} catch (MalformedURLException e) {
			System.out.println("Something wrong with the Batch url.");
			System.out.println(e.getMessage());
		}
		return null; // if there was an error.
	}

	/**
	 * Submits a batch to the server, along with associated data.
	 * @param b Batch to submit. Records are attached to this batch.
	 * @return Whether the submission was accepted by the server.
	 */
	public boolean submitBatch(Batch b) {
		return false;
		// This one will be more complicated than the requests; I will
		// need to POST and send over XML data in the connection body.
	}
	
	/**
	 * Deactivates the currently logged-in user object held locally.
	 * @return Whether the user was logged out. If this returns false, it is usually
	 * because there was no user logged in.
	 */
	public boolean logout() {
		if (currentUser != null) {
			currentUser = null;
			return true;
		}
		return false;
	}
	
	/**
	 * Searches for a series of terms in a series of fields among all records, and
	 * returns all records with matches.
	 * @param fields One or more fields to check for all of the search terms in
	 * @param searchValues One or more search terms to check for in all of the fields
	 * @return All records with a field-value match.
	 */
	public List<Record> searchRecords(List<Field> fields, List<String> searchValues) {
		return null;
	}
	
	/**
	 * Gets a list of all the fields in a project, used when downloading a batch.
	 * @return A list containing one of every field in the database.
	 */
	@SuppressWarnings("unchecked")
	public List<Field> requestFieldsList(Project p) {
		try {
			URL url = new URL(HTTP, domain, port, 
					"/field-list" + usernameAndPasswordForURLS() + 
					"&project=" + p.getID());
			Object xstreamResult = processRequest(url);
			return (List<Field>)xstreamResult;
		} catch (MalformedURLException e) {
			System.out.println("Something wrong with the Field List url.");
			System.out.println(e.getMessage());
		}
		return null; // if there was an error.
	}
	
	/**
	 * Sends a request with the provided URL, and returns the XStream output created
	 * from an XML response.
	 * @param url
	 * @return
	 */
	private Object processRequest(URL url) {
		HttpURLConnection connection = null;
		Object result = null;
		
		try {
			connection = (HttpURLConnection)url.openConnection();
			int code = getResponseCode(connection);
			if (code < 200 || code >= 300) {
				// 200-range codes are successful, everything else is not.
				return null; // not successful; username/password mismatch, or other error.
			}
			String xmlString = readInput(connection.getInputStream());
			result = xstream.fromXML(xmlString);
		} catch (Exception e) {
			handleException(e);
		} finally {
			if (connection != null) { connection.disconnect(); }
		}
		return result;
	}
	
	/**
	 * Handles closing a closeable object, such as a stream.
	 * @param obj
	 */
	private void safeClose(Closeable obj) {
		if (obj != null) {
			try {
				obj.close();
			} catch (Exception e) {
				// do nothing. Nothing we *can* do.
			}
		}
	}
	
	/**
	 * Takes a HttpURLConnection, connects to it, and gets the response code.
	 * @param url
	 * @return
	 */
	private int getResponseCode(HttpURLConnection connection) {
		int code = -1;
		try {
			connection.setRequestMethod("GET");
			code = connection.getResponseCode();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return code;
	}
	
	/**
	 * General-use exception handler for ServerCommunicator. If the server is 
	 * inaccessible, prints out a message to that effect. Otherwise it prints
	 * a stack trace.
	 * @param e
	 */
	private void handleException(Exception e) {
		if (e.getMessage().contains("refused")) {
			System.out.println("Unable to connect to server.");
		} else {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads an input stream to a single string.
	 * @param is
	 * @return
	 */
	private String readInput(InputStream is) {
		StringBuilder sb = new StringBuilder();
		Scanner in = new Scanner(is);
		while (in.hasNext()) {
			sb.append(in.nextLine());
			if (in.hasNext()) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}
	
	/**
	 * Generates the username and password part of url query strings, used in most
	 * of the server requests that require a user for verification.
	 * @return String fragment to be used in a URL path, containing the beginning
	 * 			of the query with the username and password.
	 */
	private String usernameAndPasswordForURLS() {
		if (currentUser != null) {
			String result = "?username=" + currentUser.getUsername()
						+ "&password=" + currentUser.getPassword();
			return result;
		}
		return null;
	}

	
}
