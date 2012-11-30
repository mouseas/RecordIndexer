package client.views.mainFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import client.controller.Controller;


@SuppressWarnings("serial")
public class MainFrameMenubar extends JMenuBar {

	private JMenu file;
	
	private JMenuItem download;
	private JMenuItem logout;
	private JMenuItem exit;
	
	private boolean menuEnabled;
	private boolean downloadEnabled;
	
	private Controller controller;
	
	public MainFrameMenubar() {
		super();
		
		menuEnabled = true;
		downloadEnabled = true;
		
		file = new JMenu("File");
		
		download = new JMenuItem("Download Batch");
		download.addActionListener(downloadListener);
		logout = new JMenuItem("Logout");
		logout.addActionListener(logoutListener);
		exit = new JMenuItem("Exit");
		exit.addActionListener(exitListener);
		
		file.add(download);
		file.add(logout);
		file.add(exit);
		this.add(file);
	}
	
	ActionListener downloadListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			controller.openDownloadWindow();
		}
	};
	
	ActionListener logoutListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			controller.logout();
		}
	};
	
	ActionListener exitListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			controller.exit();
		}
	};

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}
	
	/**
	 * Enable or disable all the menu items. setDownloadEnabled(false) can
	 * override this setting
	 * @param enabled
	 */
	public void setMenuEnabled(boolean enabled) {
		menuEnabled = enabled;
		if (downloadEnabled) { // only change its state if it is enabled.
			download.setEnabled(enabled);
		}
		logout.setEnabled(enabled);
		exit.setEnabled(enabled);
	}
	
	/**
	 * Turns on or off just the "Download Batch" menu item.
	 * @param enabled
	 */
	public void setDownloadEnabled(boolean enabled) {
		downloadEnabled = enabled;
		if (menuEnabled) { // only change its state if the whole menu is enabled.
			download.setEnabled(enabled);
		}
	}
}
