package server;

import java.io.*;
import java.net.*;

import com.sun.net.httpserver.*;

public class Server {

	
	public static void main(String[] args) {
		try {
			int port = 8080;
			HttpServer server = HttpServer.create(new InetSocketAddress(port), 10);
			server.createContext("/serverAssets", new myHandler());
			server.setExecutor(null); // creates default executor
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static class myHandler implements HttpHandler {
		
		public void handle(HttpExchange t) throws IOException {
			InputStream is = t.getRequestBody();
			read(is); // read the request body
			String response = "This is the response";
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
		
		private void read(InputStream is) {
			
		}
	}

}
