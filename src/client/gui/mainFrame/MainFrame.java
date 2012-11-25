package client.gui.mainFrame;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import client.gui.mainFrame.buttonBar.ButtonPanel;
import client.gui.mainFrame.dataArea.DataAreaPanel;
import client.gui.mainFrame.viewingArea.ViewingAreaPanel;
import client.gui.shared.*;

import client.dataModel.*;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

//	private Component component;
	private MainFrameMenubar menubar;
	
	private ButtonPanel buttonBar;
	private DataAreaPanel dataArea;
	private ViewingAreaPanel viewingArea;
	
	private DataModel dataModel;
	private MainFrameDimensions savedDimensions;
	
	public MainFrame(String domain, int port) {
		dataModel = new DataModel(domain, port);
		
		menubar = new MainFrameMenubar();
		this.setJMenuBar(menubar);
		
		this.setTitle("Record Indexer");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocation(100,20);
		
		this.addWindowListener(windowAdaptor);
		this.addWindowFocusListener(windowAdaptor);
		this.addWindowStateListener(windowAdaptor);
		
		buttonBar = new ButtonPanel();
		dataArea = new DataAreaPanel();
		viewingArea = new ViewingAreaPanel();
		Image mario = DrawingComponent.loadImage("mario.jpg");
		viewingArea.setImage(mario);
		
		this.add(buttonBar, BorderLayout.NORTH);
		this.add(viewingArea, BorderLayout.CENTER);
		this.add(dataArea, BorderLayout.SOUTH);
		
		this.pack();
	}
	
	private WindowAdapter windowAdaptor = new WindowAdapter() {
		@Override
		public void windowGainedFocus(WindowEvent e) {
//			component.requestFocusInWindow();
		}
	};

	public void login(String username, String password) {
		boolean success = dataModel.login(username, password);
		if (success) {
			loadState(username);
			// TODO anything else on success?
		} else {
			// TODO login problem.
		}
	}
	
	/**
	 * Loads any saved window state associated with the current user.
	 * If this is the user's first time logging in, loads the defaults.
	 */
	private void loadState(String username) {
		// TODO load dimensions from file, then adjust window to match.
	}
	
	/**
	 * Logs out of the current user after saving their window state.
	 */
	public void logout() {
		// TODO save window state.
		// savedDimensions -> get current window dimensions
		// savedDimensions.serialize()
		// saveFile(savedDimensionsSerialized, username + ".config")
		dataModel.logout();
	}
}
