package client.views.mainFrame;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

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
	
	private static final Dimension minWindowSize = new Dimension(233, 253);

//	private MainFrameDimensions savedDimensions;
	
	public MainFrame() {
		super(); // do whatever JFrame does. right?
		
		menubar = new MainFrameMenubar();
		setJMenuBar(menubar);
		
		setTitle("Record Indexer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(100,20);
		setMinimumSize(minWindowSize);
		
		getContentPane().addComponentListener(resizeListener);
		addWindowListener(windowListener);

		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		
		buttonBar = new ButtonPanel();
		dataArea = new DataAreaPanel();
		viewingArea = new ViewingAreaPanel();
		Image mario = DrawingComponent.loadImage("mario.jpg");
		viewingArea.setImage(mario);

		add(Box.createRigidArea(new Dimension(5, 5)));
		add(buttonBar);
		add(viewingArea);
		add(dataArea);
		
		pack();
	}
	
//	/**
//	 * Overrides the normal setVisible method to pop up
//	 * a login dialog when the window is made visible.
//	 */
//	@Override
//	public void setVisible(boolean visible) {
//		super.setVisible(visible);
//		if (visible && !controller.loggedIn()) {
//			createAndShowLoginDialog();
//		}
//	}

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
	
	public void setMenuEnabled(boolean enable) {
		menubar.setMenuEnabled(enable);
	}
	
	public void setDownloadEnabled(boolean enable) {
		menubar.setDownloadEnabled(enable);
	}

	public void createAndShowLoginDialog() {
		LoginDialog loginDialog = new LoginDialog(this, controller);
		controller.setLoginView(loginDialog);
		loginDialog.setVisible(true);
	}

	/**
	 * Adapter to make sure controller.exit() is called on closing the window,
	 * in order to make sure the current state is saved.
	 */
	private WindowAdapter windowListener = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent arg0) {
			controller.exit();
		}
	};
	
	private ComponentAdapter resizeListener = new ComponentAdapter() {
		@Override
		public void componentResized(ComponentEvent e) {
			// TODO handle window resizing here.
		}
	};
	
//	/**
//	 * Loads any saved window state associated with the current user.
//	 * If this is the user's first time logging in, loads the defaults.
//	 */
//	private void loadState(String username) {
//		// TODO load dimensions from file, then adjust window to match.
//	}
}
