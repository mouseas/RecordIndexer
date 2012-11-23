package client.gui.mainFrame.viewingArea;

import javax.swing.JPanel; // parent class

import java.awt.BorderLayout;

import client.gui.shared.DrawingComponent;

@SuppressWarnings("serial")
public class ViewingAreaPanel extends JPanel {
	
	private DrawingComponent component;
	
	public ViewingAreaPanel() {
		component = new DrawingComponent();
		this.add(component, BorderLayout.CENTER);
	}
}
