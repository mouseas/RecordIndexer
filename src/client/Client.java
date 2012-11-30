package client;

import java.awt.EventQueue;

import controller.Controller;

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
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						MainFrame frame = new MainFrame();
						Controller c = new Controller(domain, port);
						frame.setVisible(true);
						frame.setController(c);
						c.setView(frame);
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
