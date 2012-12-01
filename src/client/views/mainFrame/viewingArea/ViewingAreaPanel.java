package client.views.mainFrame.viewingArea;

import javax.swing.JPanel; // parent class
import javax.swing.*;

import java.awt.*;
import java.awt.geom.*;

import client.controller.Controller;
import client.views.shared.DrawingComponent;

@SuppressWarnings("serial")
public class ViewingAreaPanel extends JPanel {
	
	private DrawingComponent drawingComponent;
	private Image currentImage;
	private Point2D offset;

	@SuppressWarnings("unused")
	private Controller controller;
	
	public ViewingAreaPanel() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		drawingComponent = new DrawingComponent();
		add(drawingComponent);
		currentImage = null;
		offset = new Point2D.Double(0,0);
		
	}
	
	/**
	 * Replaces the current image with one from the input.
	 * @param newImage
	 */
	public void setImage(Image newImage) {
		if (currentImage != null) {
			drawingComponent.removeImage(currentImage);
		}
		currentImage = newImage;
		if (newImage != null) {
			drawingComponent.addImage(currentImage, offset);
		}
		drawingComponent.repaint();
		drawingComponent.validate();
		
	}

	public void setController(Controller c) {
		controller = c;
	}
}
