package client.gui.mainFrame;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import client.gui.mainFrame.buttonBar.ButtonPanel;
import client.gui.mainFrame.dataArea.DataAreaPanel;
import client.gui.mainFrame.viewingArea.ViewingAreaPanel;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

//	private Component component;
	private MainFrameMenubar menubar;
	
	public MainFrame() {
		
		menubar = new MainFrameMenubar();
		this.setJMenuBar(menubar);
		
		this.setTitle("Record Indexer");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocation(100,20);
		
		this.addWindowListener(windowAdaptor);
		this.addWindowFocusListener(windowAdaptor);
		this.addWindowStateListener(windowAdaptor);
		
//		this.add(new JLabel("Hi there"), BorderLayout.WEST);
//		this.add(new JTextArea(5,40), BorderLayout.CENTER);
//		this.add(new JButton("Poke Me"), BorderLayout.EAST);
//		component = new DrawingComponent();
//		this.add(component, BorderLayout.CENTER);
		this.add(new ButtonPanel(), BorderLayout.NORTH);
		this.add(new ViewingAreaPanel(), BorderLayout.CENTER);
		this.add(new DataAreaPanel(), BorderLayout.SOUTH);
		
		this.pack();
	}
	
	private WindowAdapter windowAdaptor = new WindowAdapter() {
		@Override
		public void windowGainedFocus(WindowEvent e) {
//			component.requestFocusInWindow();
		}
	};
}
