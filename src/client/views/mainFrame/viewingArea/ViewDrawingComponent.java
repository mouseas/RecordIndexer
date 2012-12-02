package client.views.mainFrame.viewingArea;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import client.views.shared.DrawingComponent;
import client.views.shared.DrawingShape;

/**
 * Child of DrawingComponent, this version is used in the MainView, and can be
 * zoomed, translated, inverted, etc.
 * 
 * @author Martin Carney
 *
 */
public class ViewDrawingComponent extends DrawingComponent {

	private Point2D lastPoint;
	
	public ViewDrawingComponent() {
		this.addMouseListener(mouseAdapter);
		this.addMouseMotionListener(mouseAdapter);
	}
	
	MouseAdapter mouseAdapter = new MouseAdapter() {
		
		@Override
		public void mousePressed(MouseEvent e) {
			lastPoint = new Point2D.Double(e.getX(), e.getY());
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			
			int dx = e.getX() - (int)lastPoint.getX();
			int dy = e.getY() - (int)lastPoint.getY();
			
			for (DrawingShape shape : shapes) {
				shape.adjustPosition(dx / getScale(), dy / getScale());
			}

			lastPoint = new Point2D.Double(e.getX(), e.getY());
			ViewDrawingComponent.this.repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			lastPoint = null;
		}
	};
}
