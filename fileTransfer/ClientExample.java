
import java.io.*;
import java.net.*;

public class ClientExample {

	private String serverHost;
	private int serverPort;
	private String serverFilePath;
	
	public ClientExample(String serverHost, int serverPort, String serverFilePath) {
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.serverFilePath = serverFilePath;
	}
	
	public void run() {
		Socket socket = null;
		byte[] buffer = new byte[1024];
		
		try {
			socket = new Socket(serverHost, serverPort);
			
			PrintWriter output = new PrintWriter(socket.getOutputStream());
			
			if (serverFilePath.equals("exit")) {
				output.print("exit\n");
				output.flush();
			}
			else {
				output.print("get " + serverFilePath + "\n");
				output.flush();
				
				InputStream input = socket.getInputStream();
				String status = IOUtils.readLine(input);
				if (status.startsWith("ok")) {					
					BufferedOutputStream file = null;
					try {
						String fileName = IOUtils.getFileName("test-" + serverFilePath);
						file = new BufferedOutputStream(new FileOutputStream(fileName));
						
						int bytes;
						while ((bytes = input.read(buffer)) >= 0) {
							file.write(buffer, 0, bytes);
						}
					}
					finally {
						IOUtils.safeClose(file);
					}
				}
				else {
					System.out.println(status);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			IOUtils.safeClose(socket);
		}
	}
	
	public static void main(String[] args) {
		
		String serverHost = "";
		int serverPort = -1;
		String serverFilePath = "";
		
		if (args.length == 3) {
			serverHost = args[0];
			
			try {
				serverPort = Integer.parseInt(args[1]);
			}
			catch (NumberFormatException e) {
				// Ignore
			}
			
			serverFilePath = args[2];
		}
		
		if (serverHost.length() > 0 && 
			(serverPort >= 0 && serverPort <= 65535) &&
			IOUtils.getFileName(serverFilePath).length() > 0) {
			
			ClientExample client = new ClientExample(serverHost, serverPort, serverFilePath);
			client.run();
		}
		else {
			System.out.println("USAGE: java Client <server-host> <server-port> <server-file-path>");
		}
		
	}

}
