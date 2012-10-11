package server;

public class Server {

	/**
	 * Starts the server running on a port defined by the first command-line argument.
	 * @param args args[0] should be a port number from 0 to 65535
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

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
	public void run() {
		
	}
	
	

}
