package client.views.shared;

import javax.swing.JComponent; // superclass
import javax.imageio.*;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

@SuppressWarnings("serial")
public class DrawingComponent extends JComponent {

	public static final Image NULL_IMAGE = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
	
	protected double scale;
	protected double offsetX;
	protected double offsetY;
	
	protected Color backgroundColor;
	
	protected ArrayList<DrawingShape> shapes;
	
	/**
	 * Default constructor
	 */
	public DrawingComponent() {
		scale = 1.0;
		offsetX = 0;
		offsetY = 0;
		
		shapes = new ArrayList<DrawingShape>();
		
		backgroundColor = new Color(255, 255, 255);
		setBackground(backgroundColor);
		setPreferredSize(new Dimension(700, 300));
		setMinimumSize(new Dimension(100, 100));
	}
	
	/**
	 * Loads an image file into an Image object.
	 * @param filename
	 * @return
	 */
	public static Image loadImage(String filename) {
		try {
			return ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.out.println("DrawingComponent.loadImage(): Failed to load " + filename);
			return NULL_IMAGE;
		}
	}
	
	public void addImage(Image image) {
		addImage(image, new Point2D.Double(0, 0));
	}
	
	public void addImage(Image image, Point2D offset) {
		shapes.add(new DrawingImage(image, new Rectangle2D.Double(
							offset.getX(), offset.getY(), 
							image.getWidth(null), image.getHeight(null))));
	}
	
	public void removeImage(Image image) {
		for (int i = 0; i < shapes.size(); i++) {
			if (shapes.get(i).getClass() == DrawingImage.class) {
				DrawingImage item = (DrawingImage)shapes.get(i);
				if (item.getImage().equals(image)) {
					shapes.remove(i);
					return;
				}
			}
		}
	}
	
	/**
	 * Moves all of the shapes in the DrawingComponent
	 * @param dx
	 * @param dy
	 */
	public void adjustShapePositions(double dx, double dy) {
		offsetX += dx;
		offsetY += dy;
		this.repaint();
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double newScale) {
		if (newScale <= 0) { return; } // reject negative scales
		scale = newScale;
		repaint();
//		System.out.println("DrawingComponent.setScale(): " + scale);
	}
	
	/**
	 * Determines and sets the preferred size for the component based on the
	 * height & width of all of the images, and the scale.
	 */
	public void determineSizeFromScale() {
		double maxWidth = 0;
		double maxHeight = 0;
		for (DrawingShape shape : shapes) {
			if (shape instanceof DrawingImage) {
				DrawingImage image = (DrawingImage)shape;
				maxWidth = Math.max(maxWidth, image.getWidth());
				maxHeight = Math.max(maxHeight, image.getHeight());
			}
		}
		Dimension result = new Dimension((int)(maxWidth * scale),
										(int)(maxHeight * scale));
		setPreferredSize(result);
	}
	
	public void setOffset(double offX, double offY) {
		offsetX = offX;
		offsetY = offY;
	}
	
	public Point2D getOffset() {
		return new Point2D.Double(offsetX, offsetY);
	}

	/**
	 * Draws the DrawingComponent to a Graphics2D object.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
    	Graphics2D g2 = (Graphics2D)g;

		drawBackground(g2);
		
    	AffineTransform old= g2.getTransform();
		for (DrawingShape shape : shapes) {
			AffineTransform tr2= new AffineTransform(old);
			double dx = offsetX + (this.getWidth() / 2) - (shape.getWidth() * (scale / 2));
			double dy = offsetY + (this.getHeight() / 2) - (shape.getHeight() * (scale / 2));
			
			tr2.translate(dx, dy);
			tr2.scale(scale, scale);
			g2.setTransform(tr2);
			shape.draw(g2);
		}
		
		g2.setTransform(old);
    	
	}
	
	/**
	 * Fills the component area with the background color.
	 * @param g2
	 */
	private void drawBackground(Graphics2D g2) {
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
	}
	
	private int worldToDeviceX(int w_X) {
		double d_X = w_X;
		d_X *= scale;
		return (int)d_X;
	}
	
	private int worldToDeviceY(int w_Y) {
		double d_Y = w_Y;
		d_Y *= scale;
		return (int)d_Y;
	}
	
//	private int deviceToWorldX(int d_X) {
//		double w_X = d_X;
//		w_X *= 1.0 / scale;
//		return (int)w_X;
//	}
//	
//	private int deviceToWorldY(int d_Y) {
//		double w_Y = d_Y;
//		w_Y *= 1.0 / scale;
//		return (int)w_Y;
//	}
	
	class DrawingRect implements DrawingShape {

		private Rectangle2D rect;
		private Color color;
		
		public DrawingRect(Rectangle2D rect, Color color) {
			this.rect = rect;
			this.color = color;
		}

		@Override
		public void draw(Graphics2D g2) {
			
			Rectangle2D scaledRect = new Rectangle2D.Double(worldToDeviceX((int)rect.getX()),
									worldToDeviceY((int)rect.getY()),
									(int)(rect.getWidth() * scale),
									(int)(rect.getHeight() * scale));
			g2.setColor(color);
			g2.fill(scaledRect);
			// OR g2.fillRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
			
		}

		@Override
		public void adjustPosition(double dx, double dy) {
			rect.setRect(rect.getX() + dx, rect.getY() + dy,
						rect.getWidth(), rect.getHeight());
		}

		@Override
		public boolean contains(Graphics2D g2, double x, double y) {
			// TODO Not implemented yet.
			return false;
		}

		@Override
		public double getHeight() {
			return rect.getHeight();
		}

		@Override
		public double getWidth() {
			return rect.getWidth();
		}
		
	}

	class DrawingImage implements DrawingShape {
		
		private Image image;
		private Rectangle2D rect;
		
		public DrawingImage(Image image, Rectangle2D rect) {
			this.image = image;
			this.rect = rect;
		}
		
		@Override
		public void draw(Graphics2D g2) {
//			Rectangle2D scaledRect = new Rectangle2D.Double(worldToDeviceX((int)rect.getX()),
//						worldToDeviceY((int)rect.getY()),
//						(int)(rect.getWidth() * scale),
//						(int)(rect.getHeight() * scale));
//			
//			g2.drawImage(image, (int)scaledRect.getMinX(), (int)scaledRect.getMinY(),
//								(int)scaledRect.getMaxX(), (int)scaledRect.getMaxY(), 
//								0, 0, image.getWidth(null), image.getHeight(null), null);
			Rectangle bounds = rect.getBounds();
			g2.drawImage(image, (int)bounds.getMinX(), (int)bounds.getMinY(),
					(int)bounds.getMaxX(), (int)bounds.getMaxY(),
					0, 0, image.getWidth(null), image.getHeight(null), null);
		}

		@Override
		public void adjustPosition(double dx, double dy) {
			rect.setRect(rect.getX() + dx, rect.getY() + dy,
					rect.getWidth(), rect.getHeight());
		}

		@Override
		public boolean contains(Graphics2D g2, double x, double y) {
			// TODO Auto-generated method stub
			return false;
		}
		
		public Image getImage() {
			return image;
		}
		
		public double getX() {
			return rect.getX();
		}
		
		public double getY() {
			return rect.getY();
		}
		
		public double getWidth() {
			return rect.getWidth();
		}
		
		public double getHeight() {
			return rect.getHeight();
		}
	}
	
}

