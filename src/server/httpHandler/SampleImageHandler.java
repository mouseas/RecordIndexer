package server.httpHandler;

import java.io.IOException;
import java.io.OutputStream;

import server.ServerHelper;
import server.dataAccess.DataAccess;
import shared.dataTransfer.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Handler to get the location of a sample image for a given project.
 * @author Martin Carney
 *
 */
public class SampleImageHandler implements HttpHandler {

	private DataAccess database;
	
	public SampleImageHandler(DataAccess database) {
		this.database = database;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println(exchange.getRequestURI().toString());
		User user = ServerHelper.verifyUser(database, exchange);
		int responseCode = 200;
		OutputStream os = null;
		String response = null;
		String projectIDString = ServerHelper.getQueryItem(exchange, "project=");
		int projectID = -1;
		if (projectIDString != null) {
			projectID = Integer.parseInt(projectIDString);
		}
		if (user != null) {
			if (projectID >= 0) {
				try {
					database.startTransaction();
					response = database.getSampleImageLocation(projectID);
					database.endTransaction(false);
//					System.out.println("success: " + response);
				} catch (Exception e) {
//					System.out.println("Server error.");
					response = "Server Error: Unable to produce sample file location. " +
							"This can happen if an invalid project ID was provided.";
					responseCode = 404;
				}
				if (response == null) {
					response = "No sample image found for Project ID " + projectID;
					responseCode = 404;
				}
			} else {
//				System.out.println("Client Error");
				response = "Client Error: No project specified, or project id format error.";
				responseCode = 404;
			}
		} else {
//			System.out.println("Invalid user");
			response = "Forbidden: Invalid user credentials.";
			responseCode = 403;
		}
//		System.out.println(response + " " + responseCode);
		exchange.sendResponseHeaders(responseCode, response.length());
		os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

}
