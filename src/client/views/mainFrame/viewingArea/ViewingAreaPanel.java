package client.views.mainFrame.viewingArea;

import javax.swing.JPanel; // parent class


import java.awt.*;
import java.awt.geom.*;

import client.controller.Controller;
import client.views.shared.DrawingComponent;

@SuppressWarnings("serial")
public class ViewingAreaPanel extends JPanel {
	
	private DrawingComponent component;
	private Image currentImage;
	private Point2D offset;

	private Controller controller;
	
	public ViewingAreaPanel() {
		component = new DrawingComponent();
		this.add(component, BorderLayout.CENTER);
		currentImage = null;
		offset = new Point2D.Double(0,0);
		
	}
	
	/**
	 * Replaces the current image with one from the input.
	 * @param newImage
	 */
	public void setImage(Image newImage) {
		if (currentImage != null) {
			component.removeImage(currentImage);
		}
		currentImage = newImage;
		if (newImage != null) {
			component.addImage(currentImage, offset);
		}
		
	}

	public void setController(Controller c) {
		controller = c;
	}
}
