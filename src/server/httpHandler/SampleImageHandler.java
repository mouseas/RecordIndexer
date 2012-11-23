package server.httpHandler;

import java.io.IOException;
import java.io.OutputStream;

import server.dataAccess.DataAccess;
import shared.HelperFunction;
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
	private int responseCode;
	
	public SampleImageHandler(DataAccess database) {
		this.database = database;
		responseCode = 200;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println(exchange.getRequestURI().toString());
		User user = HelperFunction.verifyUser(database, exchange);
		responseCode = 200;
		OutputStream os = null;
		String response = null;
		String projectIDString = HelperFunction.getQueryItem(exchange, "project=");
		int projectID = -1;
		if (projectIDString != null) {
			projectID = Integer.parseInt(projectIDString);
		}
		
		response = determineResponse(user, projectID);
		
//		System.out.println(response + " " + responseCode);
		exchange.sendResponseHeaders(responseCode, response.length());
		os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
	
	/**
	 * Attempts to get the file location, and produces whichever response
	 * is applicable.
	 * @param user
	 * @param projectID
	 * @return
	 */
	private String determineResponse(User user, int projectID) {
		String response = null;
		if (user != null) {
			if (projectID >= 0) {
				try {
					database.startTransaction();
					response = ImageReference.serialize(database.getSampleImage(projectID));
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
		return response;
	}

}
