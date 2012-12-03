package server.httpHandler;

import com.sun.net.httpserver.*;

import java.io.*;

import server.dataAccess.DataAccess;
import shared.HelperFunction;
import shared.dataTransfer.*;

/**
 * Resets any batch currently owned by the specified user so that
 * the user may get a new batch, and so the batch may be downloaded
 * again.
 * 
 * @author Martin Carney
 *
 */
public class ReturnBatchHandler implements HttpHandler {

	private DataAccess database;
	
	public ReturnBatchHandler(DataAccess database) {
		this.database = database;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println(exchange.getRequestURI().toString());
		database.startTransaction();
		boolean commit = false;
		User user = HelperFunction.verifyUser(database, exchange);
		OutputStream os = null;
		String response = null;
		if (user != null && user.getID() >= 0) {
			try {
				database.returnBatchUnfinished(user);
				commit = true;
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			response = "success";
		}
		int responseCode = 200;
		database.endTransaction(commit);
		
		if (user == null){ // invalid user credentials
			response = "Forbidden: Invalid user credentials.";
			responseCode = 403;
		} else if (response == null){ // user is valid, but batch not available.
			response = "Not found: No batch or SQL error.";
			responseCode = 404;
		} // if no problems, send the response
		
		exchange.sendResponseHeaders(responseCode, response.length());
		os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
//		System.out.println(response);
	}
}
