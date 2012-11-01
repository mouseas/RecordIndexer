package server.httpHandler;

import java.io.*;
import java.net.*;

import server.dataAccess.DataAccess;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GetBatchHandler implements HttpHandler {

	private DataAccess database;
	
	public GetBatchHandler(DataAccess database) {
		this.database = database;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		InputStream is = exchange.getRequestBody();
		//read(is); // read the request body
		String response = "This is the response";
		exchange.sendResponseHeaders(200, response.length());
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

}
