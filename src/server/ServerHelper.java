package server;

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
	 * Used by most of the HttpHandlers for this server, will return
	 * the user linked with the provided username and password, or
	 * null if there was not a valid one to be found from the query.
	 * @return
	 */
	public static User verifyUser(DataAccess database, HttpExchange exchange) {
		String username = getQueryItem(exchange, "username=");
		String password = getQueryItem(exchange, "password=");
		User user = null;
		if (username != null && password != null) {
			database.startTransaction();
			user = database.getUser(username, password);
			database.endTransaction(false);
		}
		return user;
	}

}
