package server.httpHandler;

import java.io.IOException;
import java.io.OutputStream;

import server.dataAccess.DataAccess;
import shared.HelperFunction;
import shared.dataTransfer.BatchImage;
import shared.dataTransfer.User;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Gets a specified batch from the server, regardless of its completion
 * or if a user has checked it out for indexing. Usually used during searches.
 * @author Martin
 *
 */
public class GetSpecificBatchHandler implements HttpHandler {

private DataAccess database;
	
	public GetSpecificBatchHandler(DataAccess database) {
		this.database = database;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println(exchange.getRequestURI().toString());
		User user = HelperFunction.verifyUser(database, exchange);
		int batchID = Integer.parseInt(HelperFunction.getQueryItem(exchange, "batch="));
		OutputStream os = null;
		String response = buildBatch(batchID);
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

	private String buildBatch(int batchID) {
		try {
			database.startTransaction();
			BatchImage batch = database.getSpecificBatch(batchID);
			database.endTransaction(false);
			XStream xstream = new XStream(new DomDriver());
			return xstream.toXML(batch);
		} catch (Exception e) {
			System.out.println("Error while getting batch:");
			System.out.println(e.getMessage());
		}
		return null;
	}
}
