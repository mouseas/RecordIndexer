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
	public User getCurrentUser() { return currentUser; }
	
	/**
	 * The Domain to connect to for this ServerCommunicator
	 */
	private String host;
	public void setHost(String newHost) {
		host = newHost;
	}
	public String getHost() {
		return host;
	}
	
	/**
	 * The port number to connect to for this ServerCommunicator
	 */
	private int port;
	public void setPort(int newPort) {
		port = newPort;
	}
	public int getPort() {
		return port;
	}
	
	private XStream xstream;
	
	private static final String HTTP = "http";
	
	/**
	 * Constructor for the ServerCommunicator. Takes in a domain and port number
	 * for use in constructing URLs to request and send information.
	 * @param domain
	 * @param port
	 */
	public ServerCommunicator(String domain, int port) {
		this.host = domain;
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
			URL url = new URL(HTTP, host, port, 
					"/login?username=" + username + "&password=" + password);
			Object xstreamResult = processRequest(url);
			User result = (User)xstreamResult;
			if (result == null || result.getID() < 0) {
				if (setCurrentUser) { currentUser = null; } // invalid user
			} else {
				if (setCurrentUser) { currentUser = result; }
			}
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
			URL url = new URL(HTTP, host, port, 
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
	public ImageReference requestSampleImage(Project p) {
		try {
			URL url = new URL(HTTP, host, port, 
					"/sample-image" + usernameAndPasswordForURLS() + 
					"&project=" + p.getID());
			Object xstreamResult = processRequest(url);
			ImageReference image = (ImageReference)xstreamResult;
			image.setFilename(filenameHeader() + image.getFilename());
			return image;
		} catch (MalformedURLException e) {
			System.out.println("Something wrong with the Sample Image url.");
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; // if there was an error.
	}
	
	/**
	 * Requests a batch for a given project. The batch data from the server 
	 * should include an image url and record information, if any.
	 * @param p The project to request a batch from
	 * @return The batch received, or null if no batch received.
	 */
	public BatchImage requestNextBatch(int projectID) {
		try {
			URL url = new URL(HTTP, host, port, 
					"/get-next-batch" + usernameAndPasswordForURLS() + 
					"&project=" + projectID);
//			System.out.println(url.toString());
			Object xstreamResult = processRequest(url);
			BatchImage result = (BatchImage)xstreamResult;
			result.setUsername(currentUser.getUsername());
			// prepend the http handle to the filename.
			if (result != null) {
				result.setImageLoc(filenameHeader() + result.getImageLoc());
			}
			return result;
		} catch (MalformedURLException e) {
			System.out.println("Something wrong with the Batch url.");
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
	public BatchImage requestSpecificBatch(int batchID) {
		try {
			URL url = new URL(HTTP, host, port, 
					"/get-specific-batch" + usernameAndPasswordForURLS() + 
					"&batch=" + batchID);
//			System.out.println(url.toString());
			Object xstreamResult = processRequest(url);
			BatchImage result = (BatchImage)xstreamResult;
			// prepend the http handle to the filename.
			if (result != null) {
				result.setImageLoc(filenameHeader() + result.getImageLoc());
			}
			return result;
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
	public boolean submitBatch(Batch batch) {
		URL url = null;
		int responseCode = -1;
		try {
			String xml = Batch.serialize(batch);
			url = new URL(HTTP, host, port, 
						"/submit-batch" + usernameAndPasswordForURLS());
			responseCode = processPost(url, xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseCode == 200;
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
	@SuppressWarnings("unchecked")
	public List<Record> searchRecords(List<Field> fields, List<String> searchValues) {
		try {
			URL url = new URL(HTTP, host, port, 
					"/search" + usernameAndPasswordForURLS() + 
					buildSearchString(fields, searchValues));
//			System.out.println(url.toString());
			Object xstreamResult = processRequest(url);
			return (List<Record>)xstreamResult;
		} catch (MalformedURLException e) {
			System.out.println("Something wrong with the Batch url.");
			System.out.println(e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Gets a list of all the fields in a project, used when downloading a batch.
	 * @return A list containing one of every field in the database.
	 */
	@SuppressWarnings("unchecked")
	public List<Field> requestFieldsList(Project p) {
		try {
			URL url = new URL(HTTP, host, port, 
					"/field-list" + usernameAndPasswordForURLS() + 
					"&project=" + p.getID());
			Object xstreamResult = processRequest(url);
			List<Field> fields = (List<Field>)xstreamResult;
			for (Field field : fields) { // prepend http header for all file locations.
				if (field.getHelpHtmlLoc() != null && field.getHelpHtmlLoc().length() > 0) {
					field.setHelpHtmlLoc(filenameHeader() + field.getHelpHtmlLoc());
				}
				if (field.getKnownDataLoc() != null && field.getKnownDataLoc().length() > 0) {
					field.setKnownDataLoc(filenameHeader() + field.getKnownDataLoc());
				}
			}
			return fields;
		} catch (MalformedURLException e) {
			System.out.println("Something wrong with the Field List url.");
			System.out.println(e.getMessage());
		}
		return null; // if there was an error.
	}
	
	/**
	 * Returns the user's current batch unfinished.
	 * @param u User to return a batch for
	 * @return Was the user's batch returned successfully?
	 */
	public boolean returnBatch(User u) {
		try {
			URL url = new URL(HTTP, host, port, 
					"/return-batch" + usernameAndPasswordForURLS());
			String result = processRequestToString(url);
			if (result.equals("success")) {
				return true;
			} else {
				return false;
			}
		} catch (MalformedURLException e) {
			System.out.println("Something wrong with the Field List url.");
			System.out.println(e.getMessage());
		}
		return false;
	}
	
	/**
	 * Builds the semi-complicated string used for the search query.
	 * @param fields
	 * @param searchValues
	 * @return Finished search query string, to be used in a URL.
	 */
	private String buildSearchString(List<Field> fields, List<String> searchValues) {
		StringBuilder sb = new StringBuilder();
		sb.append("&field=");
		for (int i = 0; i < fields.size(); i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(fields.get(i).getID());
		}
		sb.append("&search=");
		for (int i = 0; i < searchValues.size(); i++) {
			if (i > 0) {
				sb.append(",");
			}
			String searchVal = searchValues.get(i);
			searchVal.replace(" ", "%20");
			sb.append(searchVal);
		}
		System.out.println(sb.toString());
		return sb.toString();
	}
	
	/**
	 * Puts in the necessary filename header, to be used at the beginning
	 * of a file string.
	 * @return
	 */
	private String filenameHeader() {
		return "http://" + host + ":" + port + "/get/";
	}
	
	/**
	 * Sends a request with the provided URL, and returns the XStream output created
	 * from an XML response.
	 * @param url
	 * @return
	 */
	private Object processRequest(URL url) {
		String xml = processRequestToString(url);
		if (xml != null) {
			Object result = null;
			result = xstream.fromXML(xml);
			return result;
		} else {
			return null;
		}
	}
	
	/**
	 * Sends a request with the provided URL, and returns the String output created
	 * from a response.
	 * @param url
	 * @return
	 */
	private String processRequestToString(URL url) {
		HttpURLConnection connection = null;
		String result = null;
		
		try {
			connection = (HttpURLConnection)url.openConnection();
			int code = getResponseCode(connection);
			if (code < 200 || code >= 300) {
				// 200-range codes are successful, everything else is not.
				return null; // not successful; username/password mismatch, or other error.
			}
			result = readInput(connection.getInputStream());
//			System.out.println(result);
		} catch (Exception e) {
			handleException(e);
		} finally {
			if (connection != null) { connection.disconnect(); }
		}
		return result;
	}
	
	private int processPost(URL url, String body) {
		HttpURLConnection connection = null;
		int responseCode = -1;
		try {
			connection = (HttpURLConnection)url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
	
			connection.setRequestProperty("Content-Length", "" + body.getBytes().length);
			DataOutputStream output = new DataOutputStream(connection.getOutputStream());
			output.writeBytes(body);
			output.flush();
			output.close();
			
			responseCode = connection.getResponseCode(); // grabs the response code,
			// and also ensures that the exchange is processed.
		} catch (Exception e) {
			return -1;
		} finally {
			if (connection != null) { connection.disconnect(); }
		}
		return responseCode;
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
		in.close();
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
