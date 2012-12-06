package client.views.mainFrame.viewingArea;

import javax.swing.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

import client.controller.MainController;

@SuppressWarnings("serial")
public class ViewingAreaPanel extends JPanel {
	
	private ViewDrawingComponent drawingComponent;
	private Image currentImage;
	
	private boolean imageInverted;

	@SuppressWarnings("unused")
	private MainController controller;
	
	public ViewingAreaPanel() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		drawingComponent = new ViewDrawingComponent();
		add(drawingComponent);
		currentImage = null;
		
		setImageInverted(false);
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
			drawingComponent.addImage(currentImage);
			if(isInverted()) {
				invertImage();
				imageInverted = true; // only allowed override
			}
		}
//		drawingComponent.setScale(scaleFitToView());
		drawingComponent.validate();
		
	}
	
	public double scaleFitToView() {
		if (currentImage == null) { return 1.0; } // set to default scale.
		double scaleX = 1.0;
		double scaleY = 1.0;
		Dimension panel = drawingComponent.getSize();
		Dimension image = new Dimension(currentImage.getWidth(null),
										currentImage.getHeight(null));
		scaleX = panel.getWidth() / image.getWidth();
		scaleY = panel.getHeight() / image.getHeight();
		
		return Math.min(Math.min(scaleX, scaleY), 1.0);
	}
	
	/**
	 * Inverts the image and background, which may aid readability.
	 */
	public void invertImage() {
		setImageInverted(!isInverted());
		if (currentImage == null) { return; }
		BufferedImage img = (BufferedImage)currentImage;
		for(int x = 0; x < img.getWidth(null); x++) {
			for(int y = 0; y < img.getHeight(null); y++) {
				try {
				int RGBA = img.getRGB(x, y);
				Color col = new Color(RGBA, true);
				col = new Color(255 - col.getRed(),
								255 - col.getGreen(),
								255 - col.getBlue(),
								col.getAlpha());
				img.setRGB(x, y, col.getRGB());
				} catch (Exception e) {
					System.out.println("ViewingAreaPanel.invertImage(): Error at " + x + " " + y);
					e.printStackTrace();
				}
			}
		}
		drawingComponent.invertBackground();
		drawingComponent.repaint();
	}
	
	/**
	 * Sets the scale for the drawing component, and translates the image's
	 * position so that the center stays in the center.
	 * @param scale The absolute scale for the image, where 1.0 is 1:1.
	 * Must be > 0
	 */
	public void setZoom(double scale) {
		if (scale <= 0) { return; } // reject negative numbers
		drawingComponent.setScale(scale);
		
	}
	
	public void setZoomToIdeal() {
		double scale = scaleFitToView();
		drawingComponent.setScale(scale);
	}
	
	public double getCurrentZoom() {
		return drawingComponent.getScale();
	}

	public void setController(MainController c) {
		controller = c;
	}
	
	/**
	 * Sets the absolute offset for the viewing area panel. Used when restoring a
	 * user's state.
	 * @param x X offset
	 * @param y Y offset
	 */
	public void setOffset(double x, double y) {
		drawingComponent.setOffset(x, y);
	}
	
	public Point2D getOffset() {
		return drawingComponent.getOffset();
	}

	public boolean isInverted() {
		return imageInverted;
	}
	
	public void setInverted(boolean inverted) {
		if (inverted != isInverted()) {
			invertImage();
		}
	}

	/**
	 * Used internally to track when imageInverted is changed.
	 * @param imageInverted What value to set imageInverted to
	 */
	private void setImageInverted(boolean imageInverted) {
		this.imageInverted = imageInverted;
	}
}
