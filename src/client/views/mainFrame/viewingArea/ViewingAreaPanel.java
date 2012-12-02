package client.views.mainFrame.viewingArea;

import javax.swing.JPanel; // parent class
import javax.swing.*;

import java.awt.*;
import java.awt.geom.*;

import client.controller.MainController;
import client.views.shared.DrawingComponent;

@SuppressWarnings("serial")
public class ViewingAreaPanel extends JPanel {
	
	private ViewDrawingComponent drawingComponent;
	private Image currentImage;
	private Point2D offset;

	@SuppressWarnings("unused")
	private MainController controller;
	
	public ViewingAreaPanel() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		drawingComponent = new ViewDrawingComponent();
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
	
	/**
	 * Sets the scale for the drawing component, and translates the image's
	 * position so that the center stays in the center.
	 * @param scale The absolute scale for the image, where 1.0 is 1:1.
	 * Must be > 0
	 */
	public void setZoom(double scale) {
		if (scale <= 0) { return; }
		
		drawingComponent.setScale(scale);
		
	}
	
	public double getCurrentZoom() {
		return drawingComponent.getScale();
	}

	public void setController(MainController c) {
		controller = c;
	}
}
