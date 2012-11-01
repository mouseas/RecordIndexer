package server.httpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import server.dataAccess.DataAccess;
import shared.dataTransfer.Project;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ProjectListHandler implements HttpHandler {

	private DataAccess database;
	
	public ProjectListHandler(DataAccess database) {
		this.database = database;
	}
	
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println(exchange.getRequestURI().toString());
		String response = buildProjectList();
		exchange.sendResponseHeaders(200, response.length());
		OutputStream os = exchange.getResponseBody();
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
