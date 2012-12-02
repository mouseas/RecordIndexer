package client.views.shared;

import java.awt.Graphics2D;

/**
 * Public interface for objects which may be added to a DrawingComponent.
 * @author Martin Carney
 *
 */
public interface DrawingShape {
	/**
	 * Checks whether the object contains a point defined by x and y.
	 * @param g2 Graphics object...why is this here?
	 * @param x X position to check
	 * @param y Y position to check
	 * @return Whether the object contains the defined point.
	 */
	boolean contains(Graphics2D g2, double x, double y);
	
	/**
	 * Draws the object onto a Graphics2D object
	 * @param g2
	 */
	void draw(Graphics2D g2);
	
	/**
	 * Moves the object in terms of device coordinates.
	 * @param dx Amount to move along the x axis
	 * @param dy Amount to move along the y axis
	 */
	void adjustPosition(double dx, double dy);
	
	double getWidth();
	
	double getHeight();
}