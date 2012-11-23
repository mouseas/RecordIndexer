package client;

import java.awt.EventQueue;
import client.gui.mainFrame.MainFrame;

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
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				MainFrame frame = new MainFrame();
				frame.setVisible(true);
			}
		});

	}

}
