package server.httpHandler;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.*;

import server.dataAccess.DataAccess;
import shared.HelperFunction;
import shared.dataTransfer.User;

/**
 * Handles a user verification; returns a user object if the username and password
 * given to it are valid, or a 400 if the username/password was invalid.
 * @author Martin
 *
 */
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
		User user = HelperFunction.verifyUser(database, exchange);
		String response = null;
		int responseCode = 200;
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

}
