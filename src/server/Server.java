package server;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import com.sun.net.httpserver.*;

import server.dataAccess.*;
import server.httpHandler.*;

public class Server {

	
	private DataAccess database;
	private HttpServer httpServer;
	
	private static final String DATABASE_LOCATION = 
		"database" + File.separator + "indexer-app.sqlite";
	private static final String USAGE = "USAGE: java server.Server portNumber";
	
	public Server(DataAccess database, int port) {
		httpServer = null;
		this.database = database;
		database.markBatchesNotInUse(); // mark all batches as not in use.
		// No batches should be in use when the server starts.
		createHttpServerAndContexts(port);
	}
	
	/**
	 * Runs the server on the port specified in the first command line argument.
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 1) {
			try {
				int port = Integer.parseInt(args[0]);
				if (port < 0 || port > 65535) { throw new NumberFormatException(); }
				System.out.println("Starting server on port " + args[0]);
				Server server = new Server(new DataAccess(DATABASE_LOCATION), port);
				server.httpServer.start();
			} catch (ServerException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				System.out.println("Port number must be an integer between 0 and 65535");
				System.out.println(USAGE);
				return;
			}
		} else {
			System.out.println(USAGE);
		}
	}
	
	/**
	 * Creates the HTTP Server, and adds the contexts used by the server.
	 * @param port Which port the server should listen for connections on.
	 */
	private void createHttpServerAndContexts(int port) {
		try {
			httpServer = HttpServer.create(new InetSocketAddress(port), 10);
			httpServer.createContext("/login", new LoginHandler(database));
			httpServer.createContext("/project-list", new ProjectListHandler(database));
			httpServer.createContext("/kill-process", new KillServerHandler(httpServer));
			httpServer.createContext("/sample-image", new SampleImageHandler(database));
			httpServer.createContext("/get-next-batch", new GetBatchHandler(database));
			httpServer.createContext("/get-specific-batch", new GetSpecificBatchHandler(database));
			httpServer.createContext("/field-list", new GetFieldsHandler(database));
			httpServer.createContext("/search", new SearchHandler(database));
			httpServer.createContext("/get", new GetFileHandler());
			httpServer.createContext("/submit-batch", new SubmitBatchHandler(database));
			httpServer.setExecutor(null); // creates default executor
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
	/**
	 * Simple static class, used to shut down the server when it receives a request
	 * at /kill-process.
	 * @author Martin
	 *
	 */
	static class KillServerHandler implements HttpHandler {

		private HttpServer server;
		
		public KillServerHandler(HttpServer server) {
			this.server = server;
		}
		
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			String response = "Shutting down server...";
			exchange.sendResponseHeaders(200, response.length());
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
			server.stop(0);
		}
		
	}
}
