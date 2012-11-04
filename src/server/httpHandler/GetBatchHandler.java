package server.httpHandler;

import java.io.*;

import server.ServerHelper;
import server.dataAccess.DataAccess;
import shared.dataTransfer.*;

import com.sun.net.httpserver.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Handles getting a batch from the database and sending it to a client.
 * @author Martin
 *
 */
public class GetBatchHandler implements HttpHandler {

	private DataAccess database;
	
	public GetBatchHandler(DataAccess database) {
		this.database = database;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println(exchange.getRequestURI().toString());
		User user = ServerHelper.verifyUser(database, exchange);
		int projectID = Integer.parseInt(ServerHelper.getQueryItem(exchange, "project="));
		OutputStream os = null;
		String response = buildBatch(projectID, user.getUsername());
		int responseCode = 200;
		
		if (user == null){ // invalid user credentials
			response = "Forbidden: Invalid user credentials.";
			responseCode = 403;
		} else if (response == null){ // user is valid, but batch not available.
			response = "Not found: No batch available or SQL error.";
			responseCode = 404;
		} // if no problems, send the xml.
		
		exchange.sendResponseHeaders(responseCode, response.length());
		os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
//		System.out.println(response);
	}

	private String buildBatch(int projectID, String username) {
		try {
			database.startTransaction();
			Batch batch = database.getNextBatch(projectID, username);
			database.endTransaction(true);
			XStream xstream = new XStream(new DomDriver());
			return xstream.toXML(batch);
		} catch (Exception e) {
			System.out.println("Error while getting batch:");
			System.out.println(e.getMessage());
		}
		return null;
	}

}
