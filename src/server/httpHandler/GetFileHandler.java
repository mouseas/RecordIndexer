package server.httpHandler;

import java.io.*;

import server.ServerHelper;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GetFileHandler implements HttpHandler {

	private static final String FILE_ROOT = "demo/indexer_data/Records";
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		InputStream fileStream = null;
		OutputStream responseBody = null;
		long length = -1;
		byte[] buffer = new byte[1024];
		int responseCode = 200;
		String response = null;
		try {
			String filePath =  FILE_ROOT + 
							   exchange.getRequestURI().getPath().substring("get/".length());
			File f = new File(filePath);
			length = f.length();
			// file found, send it!
			fileStream = new BufferedInputStream(new FileInputStream(filePath));
			exchange.sendResponseHeaders(responseCode, length);
			responseBody = exchange.getResponseBody();
			int bytes;
			while ((bytes = fileStream.read(buffer)) >= 0) {
				responseBody.write(buffer, 0, bytes);
			}
		} catch (IOException e) { // file not found; send a 404 response.
			responseCode = 404;
			response = "404, file not found.";
			exchange.sendResponseHeaders(responseCode, response.length());
			responseBody = exchange.getResponseBody();
			responseBody.write(response.getBytes());
		} finally {
			ServerHelper.safeClose(fileStream);
			ServerHelper.safeClose(responseBody);
		}
		
	}

}
