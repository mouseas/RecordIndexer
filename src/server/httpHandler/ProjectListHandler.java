package server.httpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import server.dataAccess.DataAccess;
import shared.HelperFunction;
import shared.dataTransfer.Project;
import shared.dataTransfer.User;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Handles getting a list of projects.
 * @author Martin
 *
 */
public class ProjectListHandler implements HttpHandler {

	private DataAccess database;
	
	public ProjectListHandler(DataAccess database) {
		this.database = database;
	}
	
	public void handle(HttpExchange exchange) throws IOException {
		User user = HelperFunction.verifyUser(database, exchange);
		OutputStream os = null;
		String response = null;
		if (user != null) {
			response = buildProjectList();
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
	
	private String buildProjectList() {
		database.startTransaction();
		List<Project> projectList = database.getProjectList();
		database.endTransaction(false);
		XStream xstream = new XStream(new DomDriver());
		return xstream.toXML(projectList);
	}
	
}
