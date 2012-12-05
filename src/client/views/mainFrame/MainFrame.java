package client.views.mainFrame;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import client.controller.MainController;
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
	
	private JSplitPane verticalSplit;
	
	private MainController controller;
	
	private static final Dimension MIN_WINDOW_SIZE = new Dimension(233, 253);
	private static final Dimension SPACER = new Dimension(5, 5);
	private static final int DEFAULT_WINDOW_X = 100;
	private static final int DEFAULT_WINDOW_Y = 20;

//	private MainFrameDimensions savedDimensions;
	
	public MainFrame() {
		super(); // do whatever JFrame does. right?
		
		menubar = new MainFrameMenubar();
		setJMenuBar(menubar);
		
		setTitle("Record Indexer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(DEFAULT_WINDOW_X, DEFAULT_WINDOW_Y);
		setMinimumSize(MIN_WINDOW_SIZE);
		
		addWindowListener(windowListener);
		addMouseWheelListener(wheelListener);

		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		
		buttonBar = new ButtonPanel();
		dataArea = new DataAreaPanel();
		viewingArea = new ViewingAreaPanel();
		
		JPanel top = new JPanel();
		verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, dataArea);
		top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
		
		top.add(Box.createRigidArea(SPACER));
		top.add(buttonBar);
		top.add(Box.createRigidArea(SPACER));
		top.add(viewingArea);

		add(verticalSplit);
		
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

	public void setController(MainController c) {
		controller = c;
		buttonBar.setController(c);
		dataArea.setController(c);
		viewingArea.setController(c);
		menubar.setController(c);
	}
	
	public void setMenuEnabled(boolean enable) {
		menubar.setMenuEnabled(enable);
	}

	public MainFrameMenubar getMenubar() {
		return menubar;
	}
	
	public void setDownloadEnabled(boolean enable) {
		menubar.setDownloadEnabled(enable);
	}

	public ButtonPanel getButtonBar() {
		return buttonBar;
	}

	public DataAreaPanel getDataArea() {
		return dataArea;
	}

	public ViewingAreaPanel getViewingArea() {
		return viewingArea;
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
	
	/**
	 * Handles scroll wheel activity: zooms in or out with each wheel notch
	 */
	private MouseAdapter wheelListener = new MouseAdapter() {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int notches = e.getWheelRotation();
//			System.out.println(notches + " " + e.hashCode());
			while (notches > 0) {
				controller.zoomOut();
				notches--;
			}
			while (notches < 0) {
				controller.zoomIn();
				notches++;
			}
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
