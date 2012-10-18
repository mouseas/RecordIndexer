package server;

import server.dataAccess.*;

public class Server {

	// Need to add functions to send/receive with ServerCommunicator.
	
	private DataAccess database;
	
	/**
	 * Starts the server running on a port defined by the first command-line argument.
	 * @param args args[0] should be a port number from 0 to 65535
	 */
	public static void main(String[] args) {
		if (args.length == 1) {
			int portNum = -1;
			Server s = null;
			try {
				portNum = Integer.parseInt(args[0]);
				if (portNum < 0 || portNum > 65535) { throw new NumberFormatException(); }
				s = new Server();
				s.run(portNum);
			} catch (NumberFormatException e) {
				System.out.println("portNumber must be an integer between 0 and 65535.");
				usage();
			}
		} else {
			usage();
		}
	}
	
	private static void usage() {
		System.out.println("USAGE: java Server portNumber");
	}
	
	/**
	 * Drops all the current database data and imports data from a saved 
	 * xml database file using a DataImporter object.
	 * @param location Location of the database to load
	 * @return Whether the import was successful
	 */
	public boolean importData (String location) {
		return false;
	}
	
	/**
	 * Saves the current database data to file using a DataExporter object.
	 * @param location Location to save the database to. Overwrites any
	 * existing database data at this location.
	 * @return Whether the export was successful
	 */
	public boolean exportData (String location) {
		return false;
	}
	
	/**
	 * Runs the server, receiving and sending messages with clients.
	 */
	public void run(int portNum) {
		
	}
	
	
	

}
