package server.httpHandler;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.*;

import server.dataAccess.DataAccess;
import shared.dataTransfer.User;

import com.sun.net.httpserver.HttpExchange;

public class LoginHandler implements HttpHandler {

	private DataAccess database;
	
	public LoginHandler(DataAccess database) {
		this.database = database;
	}
	
	/**
	 * Handles verifying a user; receives a username and password from the query string,
	 * and returns either mismatch or the XML of the user object matching the query.
	 */
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println(exchange.getRequestURI().toString());
		String username = getQueryItem(exchange, "username=");
		String password = getQueryItem(exchange, "password=");
		User user = null;
		String response = null;
		int responseCode = 200;
		if (username != null && password != null) {
			database.startTransaction();
			user = database.getUser(username, password);
			database.endTransaction(false);
		}
		if (user == null) {
			responseCode = 400;
			response = "username/password missmatch";
		} else {
			response = User.serialize(user);
		}
		exchange.sendResponseHeaders(responseCode, response.length());
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
	
	private String getQueryItem(HttpExchange ex, String u) {
		String query = ex.getRequestURI().getQuery();
		if (query.contains(u)) {
			int start = query.indexOf(u) + u.length();
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
}
