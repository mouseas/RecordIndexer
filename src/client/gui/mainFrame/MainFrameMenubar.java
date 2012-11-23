package client.gui.mainFrame;

import javax.swing.JMenuBar; // superclass
import javax.swing.*;

public class MainFrameMenubar extends JMenuBar {

	private JMenu file;
	
	private JMenuItem download;
	private JMenuItem logout;
	private JMenuItem exit;
	
	public MainFrameMenubar() {
		super();
		file = new JMenu("File");
		
		download = new JMenuItem("Download Batch");
		logout = new JMenuItem("Logout");
		exit = new JMenuItem("Exit");
		
		file.add(download);
		file.add(logout);
		file.add(exit);
		this.add(file);
	}
}
