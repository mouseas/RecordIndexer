package server;

import java.io.*;
import java.net.*;
import java.util.*;

import com.sun.net.httpserver.*;
import com.thoughtworks.xstream.*;
import com.thoughtworks.xstream.io.xml.DomDriver;

import server.dataAccess.*;
import shared.dataTransfer.*;

public class Server {

	
	private DataAccess database;
	private HttpServer httpServer;
	
	private static final String DATABASE_LOCATION = 
		"database" + File.separator + "indexer-app.sqlite";
	
	public Server(DataAccess database, int port) {
		httpServer = null;
		this.database = database;
		try {
			httpServer = HttpServer.create(new InetSocketAddress(port), 10);
			httpServer.createContext("/login", new LoginHandler(database));
			httpServer.createContext("/project-list", new ProjectListHandler(database));
			httpServer.createContext("/kill-process", new KillServerHandler(httpServer));
			httpServer.setExecutor(null); // creates default executor
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static void main(String[] args) {
		if (args.length == 1) {
			try {
				int port = Integer.parseInt(args[0]);
				System.out.println("Starting server on port " + args[0]);
				Server server = new Server(new DataAccess(DATABASE_LOCATION), port);
				server.httpServer.start();
			} catch (ServerException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("USAGE: java server.Server portNumber");
		}
	}
	
	
	
	static class LoginHandler implements HttpHandler {
		
		private DataAccess database;
		
		public LoginHandler(DataAccess database) {
			this.database = database;
		}
		
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
				System.out.println(result);
				return result;
			}
			return null;
		}
		
	}
	
	
	
	static class ProjectListHandler implements HttpHandler {
		
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
	
	
	
	static class KillServerHandler implements HttpHandler {

		private HttpServer server;
		
		public KillServerHandler(HttpServer server) {
			this.server = server;
		}
		
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			System.out.println(exchange.getRequestURI().toString());
			server.stop(0);
		}
		
	}
	
//	@Override
//	public void handle(HttpExchange exchange) throws IOException {
//		InputStream is = exchange.getRequestBody();
////		read(is); // read the request body
//		String response = "This is the response";
//		exchange.sendResponseHeaders(200, response.length());
//		OutputStream os = exchange.getResponseBody();
//		os.write(response.getBytes());
//		os.close();
//	}

}
