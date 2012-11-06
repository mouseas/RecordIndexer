package server.httpHandler;

import java.io.*;
import java.util.List;

import server.dataAccess.DataAccess;
import shared.HelperFunction;
import shared.dataTransfer.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Handles getting a list of fields from the database. Given a project ID,
 * it will return just that project's fields. Given no project ID, it will
 * return a list of ALL the fields, useful for populating a search list.
 * @author Martin
 *
 */
public class GetFieldsHandler implements HttpHandler {

	private DataAccess database;
	
	public GetFieldsHandler(DataAccess database) {
		this.database = database;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		User user = HelperFunction.verifyUser(database, exchange);
		int projectID = Integer.parseInt(HelperFunction.getQueryItem(exchange, "project="));
		OutputStream os = null;
		String response = null;
		if (user != null) {
			response = buildFieldList(projectID);
			exchange.sendResponseHeaders(200, response.length());
		} else {
			response = "Forbidden: Invalid user credentials.";
			exchange.sendResponseHeaders(403, response.length());
			System.out.println(exchange.getRequestURI().toString());
		}
		os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	private String buildFieldList(int projectID) {
		database.startTransaction();
		List<Field> fieldList = database.getFields(projectID);
		database.endTransaction(false);
		XStream xstream = new XStream(new DomDriver());
		return xstream.toXML(fieldList);
	}

}
