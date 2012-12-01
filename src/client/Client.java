package client;

import java.awt.EventQueue;


import client.controller.Controller;
import client.views.mainFrame.MainFrame;

/**
 * Starts the Client-side program
 * @author Martin
 *
 */
public class Client {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 2) {
			try {
				final String domain = args[0]; // for some reason these must be final.
				final int port = Integer.parseInt(args[1]);
				if (port < 0 || port > 65535) {
					throw new Exception("Port number out of bounds: " + port);
				}
//				System.out.println("Domain: [" + domain + "]");
//				System.out.println("Port: [" + port + "]");
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						Controller c = new Controller(domain, port);
						// make the main frame
						MainFrame mainFrame = new MainFrame();
						mainFrame.setController(c);
						c.setMainView(mainFrame);
						c.createAndShowLoginDialog(); // open login screen
					}
				});
			} catch (Exception e) {
				System.out.println(e.getMessage());
				usage();
			}
		} else {
			usage();
		}
	}
	
	/**
	 * Prints a usage statement. May later give a pop-up error message.
	 */
	private static void usage() {
		System.out.println("USAGE: java Client domain port");
	}

}
