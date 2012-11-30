package client.views.mainFrame;

import javax.swing.*;

import controller.Controller;

import java.awt.*;
//import java.awt.event.*;

//import client.model.*;
import client.views.mainFrame.buttonBar.ButtonPanel;
import client.views.mainFrame.dataArea.DataAreaPanel;
import client.views.mainFrame.viewingArea.ViewingAreaPanel;
import client.views.shared.*;


@SuppressWarnings("serial")
public class MainFrame extends JFrame {

//	private Component component;
	private MainFrameMenubar menubar;
	
	private ButtonPanel buttonBar;
	private DataAreaPanel dataArea;
	private ViewingAreaPanel viewingArea;
	
	private Controller controller;
//	private MainFrameDimensions savedDimensions;
	
	public MainFrame() {
		super(); // do whatever JFrame does. right?
		
		menubar = new MainFrameMenubar();
		this.setJMenuBar(menubar);
		
		this.setTitle("Record Indexer");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setLocation(100,20);
		
		buttonBar = new ButtonPanel();
		dataArea = new DataAreaPanel();
		viewingArea = new ViewingAreaPanel();
		Image mario = DrawingComponent.loadImage("mario.jpg");
		viewingArea.setImage(mario);
		
		add(buttonBar);
		add(viewingArea);
		add(dataArea);
		
		this.pack();
	}

	public void login(String username, String password) {
		boolean success = controller.login(username, password);
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
		controller.logout();
	}

	public void setController(Controller c) {
		controller = c;
		buttonBar.setController(c);
		dataArea.setController(c);
		viewingArea.setController(c);
	}
}
