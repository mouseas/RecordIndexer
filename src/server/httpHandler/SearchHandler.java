package server.httpHandler;

import java.io.*;
import java.util.*;

import server.dataAccess.DataAccess;
import shared.HelperFunction;
import shared.dataTransfer.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Handles searches within multiple fields and with multiple search terms.
 * Query format is:
 * "?username=name&password=pass&field=1,2,5,6&search=term,term2,term3"
 * @author Martin
 *
 */
public class SearchHandler implements HttpHandler {

	private DataAccess database;
	
	public SearchHandler(DataAccess database) {
		this.database = database;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println(exchange.getRequestURI().toString());
		
		// initialize variables
		User user = HelperFunction.verifyUser(database, exchange);
		int[] fieldIDs = StringListToIntArray(
				HelperFunction.getMultipleQueryItems(exchange, "field="));
		List<String> searchTerms = HelperFunction.getMultipleQueryItems(exchange, "search=");
		StringBuilder response = new StringBuilder();
		int responseCode = 200;
		
		// process the search(es), putting results into response.
		if (user != null) {
			processSearch(response, fieldIDs, searchTerms);
		} else {
			response.append("Forbidden: invalid username or password.");
			responseCode = 403;
		}
		
		// send final result.
		exchange.sendResponseHeaders(responseCode, response.length());
		OutputStream os = exchange.getResponseBody();
		os.write(response.toString().getBytes());
		os.close();
	}
	
	/**
	 * Takes in an array of field IDs, a list of search terms, and a
	 * StringBuilder for output, and runs all the search queries possible.
	 * Every positive result is input into the StringBuilder as a Record
	 * object converted to XML, all of which are in a List object.
	 * @param response
	 * @param fieldIDs
	 * @param searchTerms
	 */
	private void processSearch(StringBuilder response, int[] fieldIDs,
								List<String> searchTerms) {
		database.startTransaction();
		response.append("<list>\n");
		for (int i = 0; i < fieldIDs.length; i++) {
			for (int j = 0; j < searchTerms.size(); j++) {
				List<Record> records = database.search(fieldIDs[i], searchTerms.get(j));
				for (Record r : records) {
					response.append(Record.serialize(r) + "\n");
				}
			}
		}
		response.append("</list>\n");
		database.endTransaction(false);
	}

	/**
	 * Converts a List of IDs in string format into an array of integers.
	 * @param fieldIDStrings
	 * @return
	 */
	private int[] StringListToIntArray(List<String> fieldIDStrings) {
		int[] result = null;
		String str = null;
		try {
			result = new int[fieldIDStrings.size()];
			for (int i = 0; i < fieldIDStrings.size(); i++) {
				str = fieldIDStrings.get(i);
				result[i] = Integer.parseInt(str);
			}
		} catch (NumberFormatException e) {
			System.out.println("Field ID \"" + str + "\" was not a valid integer.");
		}
		return result;
	}

}
