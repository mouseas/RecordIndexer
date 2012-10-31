package server;

import java.io.*;
import java.net.*;
import java.util.*;

import server.ServerException;
import server.dataAccess.*;

public class ServerTCPExample {

	// Need to add functions to send/receive with ServerCommunicator.
	
	ServerSocket listenSocket;
	byte[] buffer;

//	private DataAccess database;
	private int port;
	
	private static final String DATABASE_LOCATION = 
		"database" + File.separator + "indexer-app.sqlite";
	private static final String ASSETS_LOCATION = "serverAssets/";
	
	/**
	 * Constructor. Requires a live DataAccess object in order to access the database.
	 * @param database
	 */
	public ServerTCPExample(DataAccess database) {
		this.port = -1;
//		this.database = database;
		
		listenSocket = null;
		buffer = new byte[1024];
	}
	
	/**
	 * Starts the server running on a port defined by the first command-line argument.
	 * @param args args[0] should be a port number from 0 to 65535
	 */
	public static void main(String[] args) {
		if (args.length == 1) {
			int portNum = Integer.parseInt(args[0]);
			ServerTCPExample s = null;
			try {
				portNum = Integer.parseInt(args[0]);
				if (portNum < 0 || portNum > 65535) { throw new NumberFormatException(); }
				s = new ServerTCPExample(new DataAccess(DATABASE_LOCATION));
				s.run(portNum);
			} catch (NumberFormatException e) {
				System.out.println("portNumber must be an integer between 0 and 65535.");
				usage();
			} catch (ServerException e) {
				System.out.println("Unable to properly connect to database. Terminating.");
				System.out.println(e.getMessage());
			}
		} else {
			usage();
		}
	}
	
	private static void usage() {
		System.out.println("USAGE: java Server portNumber");
	}
	
	/**
	 * Runs the server, receiving and sending messages with clients.
	 */
	public void run(int portNum) {
		port = portNum;
		try {
			listenSocket = new ServerSocket(port);
			while(acceptAndProcessInput()); // loop until the server receives an "exit" command
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			safeClose(listenSocket);
		}
		
	}
	
	/**
	 * Gets the first line of a request, and sorts the request to the relevant functions.
	 * @return
	 * @throws IOException
	 */
	private boolean acceptAndProcessInput() throws IOException {
		Socket clientSocket = listenSocket.accept();
		try {
			Scanner input = new Scanner(clientSocket.getInputStream());
			
			String command = input.nextLine();
			System.out.println(command);
			
			if (command.startsWith("exit")) {
				return false; // Kill the server.
			} else if (command.startsWith("get")) {
				// Get a file, such as an image, help text, or recognized entries.
				getFile(clientSocket, command);
			} else {
				// pretty much everything else.
				
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			safeClose(clientSocket);
		}
		return true;
	}
	
	/**
	 * Sends the requested file to the client, such as an image, or help text.
	 * All files requested must exist in the /serverAssets directory.
	 * @param clientSocket Established socket with a client.
	 */
	private void getFile(Socket clientSocket, String firstLine) throws IOException {
//		Scanner input = new Scanner(clientSocket.getInputStream());
		PrintWriter output = new PrintWriter(clientSocket.getOutputStream());
		
		String filePath = ASSETS_LOCATION + firstLine.substring("get ".length());
		InputStream file = null;
		
		try {
			file = new BufferedInputStream(new FileInputStream(filePath));
		} catch (IOException e) {
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
			} catch (IOException e) {
				// Reading the file failed, so give up
			} finally {
				safeClose(file);
			}
		}
	}
	
	/**
	 * Closes a variety of objects, usually sockets.
	 * @param obj
	 */
	private void safeClose(Closeable obj) {
		try {
			obj.close();
		} catch (IOException e) {
			// do nothing; there is nothing we can do.
		}
	}
	
	
	

}
