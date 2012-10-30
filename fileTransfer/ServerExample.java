
import java.io.*;
import java.net.*;
import java.util.*;

public class ServerExample {
	
	private int port;
	
	public ServerExample(int port) {
		this.port = port;
	}
	
	public void run() {
		ServerSocket listenSocket = null;
		byte[] buffer = new byte[1024];
		
		try {
			listenSocket = new ServerSocket(port);
			
			while (true) {
				Socket clientSocket = listenSocket.accept();
				
				try {
					Scanner input = new Scanner(clientSocket.getInputStream());
					PrintWriter output = new PrintWriter(clientSocket.getOutputStream());
					
					String command = input.nextLine();
					
					if (command.startsWith("exit")) {
						return;
					}
					else if (command.startsWith("get")) {
						System.out.println(command);
						String filePath = command.substring("get ".length());
						InputStream file = null;
						
						try {
							file = new BufferedInputStream(new FileInputStream(filePath));
						}
						catch (IOException e) {
							output.print("error " + e.getMessage() + "\n");
							output.flush();
						}
						
						if (file != null) {
							try {
								output.print("ok\n");
								output.flush();
	
								int bytes;
								while ((bytes = file.read(buffer)) >= 0) {
									clientSocket.getOutputStream().write(buffer, 0, bytes);
								}
							}
							catch (IOException e) {
								// Reading the file failed, so give up
							}
							finally {
								IOUtils.safeClose(file);
							}
						}
					}
				}
				finally {
					IOUtils.safeClose(clientSocket);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			IOUtils.safeClose(listenSocket);
		}
	}

	public static void main(String[] args) {
		
		int port = -1;
		
		if (args.length == 1) {
			try {
				port = Integer.parseInt(args[0]);
			}
			catch (NumberFormatException e) {
				// Ignore
			}
		}
		
		if (port >= 0 && port <= 65535) {
			ServerExample server = new ServerExample(port);
			server.run();
		}
		else {
			System.out.println("USAGE: java Server <port>");
		}

	}

}
