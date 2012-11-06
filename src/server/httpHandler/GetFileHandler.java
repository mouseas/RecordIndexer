package server.httpHandler;

import java.io.*;
import java.util.Scanner;

import shared.HelperFunction;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Gets a file from a specified folder. This folder is set by DataImporter
 * to be the same directory the Records XML file was imported from.
 * This can be overriden by modifying the filesLocation.txt file, but the
 * file is overwritten each time DataImporter is run.
 * @author Martin
 *
 */
public class GetFileHandler implements HttpHandler {

	private String fileRoot;
	
	public GetFileHandler() {
		fileRoot = "demo/indexer_data/Records"; // default
		setFileLocation(); // load file root from file if available.
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		InputStream fileStream = null;
		OutputStream responseBody = null;
		long length = -1;
		byte[] buffer = new byte[1024];
		int responseCode = 200;
		String response = null;
		try {
			String filePath =  fileRoot + 
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
			HelperFunction.safeClose(fileStream);
			HelperFunction.safeClose(responseBody);
		}
		
	}
	
	public void setFileLocation() {
		File locFile = new File("filesLocation.txt");
		Scanner in = null;
		if (locFile.exists()) {
			try {
				in = new Scanner(locFile);
				String result = in.nextLine();
				if (result != null && result.length() > 0) {
					fileRoot = result;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				HelperFunction.safeClose(in);
			}
		} // otherwise use the default still.
	}

}
