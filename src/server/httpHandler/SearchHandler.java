package server.httpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import server.dataAccess.DataAccess;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class SearchHandler implements HttpHandler {

	private DataAccess database;
	
	public SearchHandler(DataAccess database) {
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
