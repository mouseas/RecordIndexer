package server.httpHandler;

import java.io.*;
import java.util.*;

import server.dataAccess.DataAccess;
import shared.HelperFunction;
import shared.dataTransfer.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Handles when a batch and a series of records associated with it is posted 
 * to the server.
 * @author Martin
 *
 */
public class SubmitBatchHandler implements HttpHandler {

	DataAccess database;
	int responseCode;
	
	public SubmitBatchHandler(DataAccess database) {
		this.database = database;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try {
			System.out.println(exchange.getRequestURI().toString());
			User user = HelperFunction.verifyUser(database, exchange);
			responseCode = 200;
			InputStream body = null;
			
			if (user == null) { responseCode = 403; }
			body = exchange.getRequestBody();
			Batch finishedBatch = readBatch(body);
			if (finishedBatch != null && finishedBatch.getRecords().size() > 0) {
				boolean commit = true;
				database.startTransaction();
				if (!saveRecords(finishedBatch)) { commit = false; }
				if (!saveBatch(finishedBatch.batchImage)) { commit = false; }
				if (!addUserNumIndexedRecords(finishedBatch, user)) { commit = false; }
				database.endTransaction(commit);
				if (!commit) { responseCode = 404; }
			} else {
				responseCode = 404; // error of some sort.
			}
			
			sendResponse(exchange);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads in the xml and outputs it as a FinishedBatch object.
	 * @param body
	 * @return
	 */
	private Batch readBatch(InputStream body) {
		Batch result = null;
		try {
			XStream xstream = new XStream(new DomDriver());
			result = (Batch)xstream.fromXML(body);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Saves the records as they were received.
	 * @param batch
	 */
	private boolean saveRecords(Batch batch) {
		if (batch == null) { return false; }
		List<Record> records = batch.getRecords();
		boolean commit = true;
		try {
			commit = database.saveSeveralRecords(records);
		} catch (Exception e) {
			commit = false;
		}
		return commit;
	}

	/**
	 * Mark the finished Batch as completed = true (1).
	 * @param batch
	 */
	private boolean saveBatch(BatchImage batch) {
		if (batch == null) { return false; }
		boolean commit = true;
		try {
			commit = database.saveBatch(batch, true);
		} catch (Exception e) {
			commit = false;
		}
		return commit;
	}

	/**
	 * Updates the user's number of indexed records by adding their current
	 * total to the number of records.
	 * @param batch
	 * @return
	 */
	private boolean addUserNumIndexedRecords(Batch batch, User user) {
		if (batch == null) { return false; }
		boolean commit = true;
		try {
			int newNumRecordsIndexed = batch.getRecords().size() 
					+ user.getNumIndexedRecords();
			commit = database.setNumRecordsIndexed(user, newNumRecordsIndexed);
		} catch (Exception e) {
			commit = false;
		}
		return commit;
	}
	
	/**
	 * Sends the response after receiving and processing the request.
	 * @param exchange
	 * @param responseCode
	 */
	private void sendResponse(HttpExchange exchange) {
		OutputStream os = null;
		String response = null;
		try {
			if (responseCode == 200){ // ok
				response = "OK";
			} else if (responseCode == 403){ // user is valid, but batch not available.
				response = "Forbidden: Username/password was invalid.";
			} else {
				response = "Error of some sort.";
			}
			exchange.sendResponseHeaders(responseCode, response.length());
			os = exchange.getResponseBody();
			os.write(response.getBytes());
		} catch(Exception e) {
			System.out.println("Error sending a response while handling a submitted batch.");
		} finally {
			HelperFunction.safeClose(os);
		}
	}
	
//	/**
//	 * A quick early test of a post operation.
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		String HTTP = "http";
//		String domain = "localhost";
//		int port = 8080;
//		URL url = null;
//		HttpURLConnection connection = null;
//		try {
//			url = new URL(HTTP, domain, port, "/submit-batch?username=test1&password=test1");
//			connection = (HttpURLConnection)url.openConnection();
//			connection.setDoOutput(true);
//			connection.setDoInput(true);
//			connection.setRequestMethod("POST");
//			connection.setUseCaches(false);
//			
//			FinishedBatch batch = new FinishedBatch(new Batch(0, 0, "images/1890_image0.png", true));
//			Field f = new Field(0, 0, "Last Name", 60, 300, "fieldhelp/last_name.html", "knowndata/1890_last_names.txt");
//			batch.add(f);
//			f = new Field(0, 0, "First Name", 360, 280, "fieldhelp/last_name.html", "knowndata/1890_last_names.txt");
//			batch.add(f);
//			f = new Field(0, 0, "Gender", 640, 205, "fieldhelp/last_name.html", "knowndata/1890_last_names.txt");
//			batch.add(f);
//			f = new Field(0, 0, "Age", 845, 120, "fieldhelp/last_name.html", "");
//			batch.add(f);
//			for (int i = 0; i < 8; i++) {
//				for (int j = 0; j < batch.getFields().size(); j++) {
//					Record r = new Record(i * batch.getFields().size() + j, 0, j, i, "testValue new " + i + " " + j);
//					batch.add(r);
//				}
//			}
//			String xml = FinishedBatch.serialize(batch);
//
//			connection.setRequestProperty("Content-Length", "" + xml.getBytes().length);
//			DataOutputStream output = new DataOutputStream(connection.getOutputStream());
//			output.writeBytes(xml);
//			output.flush();
//			output.close();
//			
//			System.out.println(connection.getResponseCode());
//			System.out.println(connection.getResponseMessage());
//			
//			connection.disconnect();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//	}

}
