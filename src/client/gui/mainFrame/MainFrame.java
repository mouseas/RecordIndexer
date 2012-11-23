package client.gui.mainFrame;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import client.gui.mainFrame.buttonBar.ButtonPanel;
import client.gui.mainFrame.dataArea.DataAreaPanel;
import client.gui.mainFrame.viewingArea.ViewingAreaPanel;
import client.gui.shared.*;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

//	private Component component;
	private MainFrameMenubar menubar;
	
	private ButtonPanel buttonBar;
	private DataAreaPanel dataArea;
	private ViewingAreaPanel viewingArea;
	
	public MainFrame() {
		
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
}
