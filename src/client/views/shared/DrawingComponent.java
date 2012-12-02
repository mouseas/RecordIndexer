package client.views.shared;

import javax.swing.JComponent; // superclass
import javax.imageio.*;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class DrawingComponent extends JComponent {

	private static Image NULL_IMAGE = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
	
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
			System.out.println("Failed to load " + filename);
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
		
		determinePreferredSize();
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
		double oldScale = scale;
		scale = newScale;
		
//		double windowWidth = getSize().getWidth();
//		double windowHeight = getSize().getHeight();
//		
//		double dx = (getSize().getWidth() / 2) - (scale * getSize().getWidth()) / 2;
////		double dy = getSize().getHeight() - (scale / oldScale) * getSize().getHeight();
//		double dy = 0;
//		System.out.println(dx + " " + dy + " | " + getSize().getWidth() + " " + getSize().getHeight());
//		adjustShapePositions(dx, dy);
		
		determinePreferredSize();
		repaint();
	}
	
	/**
	 * Determine the whole size of the image, and sets the preferred size accordingly
	 */
	public void determinePreferredSize() {
		final int MIN = 50; // smallest allowed size.
		
		double minX = 0;
		double maxX = 0;
		double minY = 0;
		double maxY = 0;
		boolean firstImageLocated = false;
		
		for (int i = 0; i < shapes.size(); i++) {
			DrawingShape shape = shapes.get(i);
			if (shape instanceof DrawingImage) {
				DrawingImage image = (DrawingImage)shape;
				if (!firstImageLocated) { 
					// first DrawingImage; set values to it.
					firstImageLocated = true;
					minX = image.getX();
					minY = image.getY();
					maxX = minX + image.getWidth();
					maxY = minY + image.getHeight();
				} else { 
					// additional Drawing Image. Get min and max values from all of them.
					if (minX > image.getX()) { minX = image.getX(); }
					if (minY > image.getY()) { minY = image.getY(); }
					if (maxX < image.getX() + image.getWidth()) {
						maxX = image.getX() + image.getWidth();
					}
					if (maxY < image.getY() + image.getHeight()) {
						maxY = image.getY() + image.getHeight();
					}
				}
			}
		}
		if ((maxX - minX) * scale > MIN && (maxY - minY) * scale > MIN) {
			setPreferredSize(new Dimension((int)((maxX - minX) * scale),
										(int)((maxY - minY) * scale)));
		}
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
	
	private int deviceToWorldX(int d_X) {
		double w_X = d_X;
		w_X *= 1.0 / scale;
		return (int)w_X;
	}
	
	private int deviceToWorldY(int d_Y) {
		double w_Y = d_Y;
		w_Y *= 1.0 / scale;
		return (int)w_Y;
	}

//	private KeyAdapter keyAdapter = new KeyAdapter() {
//
//		@Override
//		public void keyPressed(KeyEvent e) {
//			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
//				adjustShapePositions(-5, 0, shapes);
//			}
//			else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
//				adjustShapePositions(5, 0, shapes);
//			}
//			else if (e.getKeyCode() == KeyEvent.VK_UP) {
//				adjustShapePositions(0, -5, shapes);
//			}
//			else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
//				adjustShapePositions(0, 5, shapes);
//			}
//		}
//	};
	
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

