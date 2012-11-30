package client.views.mainFrame;

import javax.swing.*;

import java.awt.*;
//import java.awt.event.*;

//import client.model.*;
import client.controller.Controller;
import client.views.login.LoginDialog;
import client.views.mainFrame.buttonBar.ButtonPanel;
import client.views.mainFrame.dataArea.DataAreaPanel;
import client.views.mainFrame.viewingArea.ViewingAreaPanel;
import client.views.shared.*;

/**
 * Main indexing window.
 * @author Martin Carney
 *
 */
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
		setJMenuBar(menubar);
		
		setTitle("Record Indexer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(100,20);

		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		
		buttonBar = new ButtonPanel();
		dataArea = new DataAreaPanel();
		viewingArea = new ViewingAreaPanel();
		Image mario = DrawingComponent.loadImage("mario.jpg");
		viewingArea.setImage(mario);
		
		add(buttonBar);
		add(viewingArea);
		add(dataArea);
		
		pack();
	}
	
	/**
	 * Overrides the normal setVisible method to pop up
	 * a login dialog when the window is made visible.
	 */
	@Override
	public void setVisible(boolean v) {
		super.setVisible(v);
		if (!controller.loggedIn()) {
			createAndShowLoginDialog();
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
		// TODO move this to Controller
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
		menubar.setController(c);
	}

	private void createAndShowLoginDialog() {
		LoginDialog loginDialog = new LoginDialog(this, controller);
		controller.setLoginView(loginDialog);
		loginDialog.setVisible(true);
	}
}
