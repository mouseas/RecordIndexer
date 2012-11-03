package server;

import java.io.Closeable;
import java.util.*;

import server.dataAccess.DataAccess;
import shared.dataTransfer.User;

import com.sun.net.httpserver.HttpExchange;

/**
 * Contains static methods used by HttpHandlers and elseware.
 * @author Martin
 *
 */
public class ServerHelper {

	/**
		 * Used by all the HttpHandlers for this server, will grab a query item
		 * by name from HttpExchange, or null if it is not found in the query.
		 * @param exchange HttpExchange object received by a handle() method
		 * @param itemName Name of the item to get from the exchange's query.
		 * This should usually end with an = sign, such as "username=".
		 * @return The value of the matching item, or null if no query item
		 * matched the itemName string.
		 */
		public static String getQueryItem(HttpExchange exchange, String itemName) {
			String query = exchange.getRequestURI().getQuery();
			if (query.contains(itemName)) {
				int start = query.indexOf(itemName) + itemName.length();
				int end = query.indexOf("&", start);
				String result = null;
				if (end < 0) {
					result = query.substring(start);
				} else {
					result = query.substring(start, end);
				}
	//			System.out.println(result);
				return result;
			}
			return null;
		}
		
		/**
		 * Similar to getQueryItem, but gets a list of items as Strings instead
		 * of just one item.
		 * @param exchange HttpExchange object received by a handle() method
		 * @param itemName Name of the item to get from the exchange's query.
		 * This should usually end with an = sign, such as "username=".
		 * @return List<String> of all the entries matching the item name.
		 * An empty List (size = 0) indicates itemName was not found.
		 */
		public static List<String> getMultipleQueryItems(
								HttpExchange exchange, String itemName) {
			List<String> result = new ArrayList<String>();
			String query = exchange.getRequestURI().getQuery();
			if (query.contains(itemName)) {
				int start = query.indexOf(itemName) + itemName.length();
				int end = -1;
				if (query.indexOf("&", start) > 0) {
					//trim to just the part of the query matching the item name.
					query = query.substring(start, query.indexOf("&", start));
				} else {
					query = query.substring(start);
				}
				start = 0; // start of the now-trimmed string
				do {
					if (query.indexOf(",", start) > 0) { // another entry exists
						end = query.indexOf(",", start);
					} else {
						end = query.length(); // last entry
					}
					String str = query.substring(start, end);
					result.add(str);
					start = end + 1;
					end = -1;
				} while (start < query.length() && query.indexOf(",", start - 1) > 0);
			}
			return result;
		}

	/**
	 * Used by most of the HttpHandlers for this server, will return
	 * the user linked with the provided username and password, or
	 * null if there was not a valid one to be found from the query.
	 * @return
	 */
	public static User verifyUser(DataAccess database, HttpExchange exchange) {
		String username, password = null;
		try {
			username = getQueryItem(exchange, "username=");
			password = getQueryItem(exchange, "password=");
		} catch (NullPointerException e) {
			return null; // no username or password provided.
		}
		User user = null;
		if (username != null && password != null) {
			database.startTransaction();
			user = database.getUser(username, password);
			database.endTransaction(false);
		}
		return user;
	}
	
	/**
	 * Handles closing a closeable object, such as a stream.
	 * @param obj
	 */
	public static void safeClose(Closeable obj) {
		if (obj != null) {
			try {
				obj.close();
			} catch (Exception e) {
				// do nothing. Nothing we *can* do.
			}
		}
	}
	
}
