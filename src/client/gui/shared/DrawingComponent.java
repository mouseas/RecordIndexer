package client.gui.shared;

import javax.swing.JComponent; // superclass
//import javax.swing.*;
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
	
	private ArrayList<DrawingShape> shapes;
//	private ArrayList<DrawingShape> dragshapes;
	private Font font;
	private Point2D lastPoint;
	
	/**
	 * constructor
	 */
	public DrawingComponent() {
		shapes = new ArrayList<DrawingShape>();
		font = new Font("SansSerif", Font.PLAIN, 72);
		
		this.setBackground(new Color(178, 223, 210));
		this.setPreferredSize(new Dimension(700, 300));
		this.setMinimumSize(new Dimension(100, 100));
		this.setMaximumSize(new Dimension(1000, 800));
		
		this.addMouseListener(mouseAdapter);
		this.addMouseMotionListener(mouseAdapter);
		this.addKeyListener(keyAdapter);
		
		shapes.add(new DrawingRect(new Rectangle2D.Double(20, 20, 150, 200), new Color(210, 180, 140, 197)));
		shapes.add(new DrawingLine(new Line2D.Double(400, 400, 600, 600), new Color(255, 0, 0, 64)));
		
		Image mario = loadImage("mario.jpg");
		shapes.add(new DrawingImage(mario, new Rectangle2D.Double(250, 50, mario.getWidth(null), mario.getHeight(null))));
		
		String text1 = "Hello World";
		shapes.add(new DrawingText(text1, font, Color.BLACK, new Point2D.Float(200,400)));
		
	}
	
	private Image loadImage(String filename) {
		try {
			return ImageIO.read(new File(filename));
		} catch (IOException e) {
			return NULL_IMAGE;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		drawBackground(g2);
		drawShapes(g2);
	}
	
	private void drawBackground(Graphics2D g2) {
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
	}
	
	private void drawShapes(Graphics2D g2) {
		for (DrawingShape shape : shapes) {
			shape.draw(g2);
		}
	}
	
	private void adjustShapePositions(double dx, double dy) {
		for (DrawingShape shape : shapes) {
			shape.adjustPosition(dx, dy);
		}
		this.repaint();
	}
	
	private MouseAdapter mouseAdapter = new MouseAdapter() {
		
		@Override
		public void mousePressed(MouseEvent e) {
			lastPoint = new Point2D.Double(e.getX(), e.getY());
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			
			int dx = e.getX() - (int)lastPoint.getX();
			int dy = e.getY() - (int)lastPoint.getY();
			
			for (DrawingShape shape : shapes) {
				shape.adjustPosition(dx, dy);
			}

			lastPoint = new Point2D.Double(e.getX(), e.getY());
			DrawingComponent.this.repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			lastPoint = null;
		}
	};
	

	private KeyAdapter keyAdapter = new KeyAdapter() {

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				adjustShapePositions(-5, 0);
			}
			else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				adjustShapePositions(5, 0);
			}
			else if (e.getKeyCode() == KeyEvent.VK_UP) {
				adjustShapePositions(0, -5);
			}
			else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				adjustShapePositions(0, 5);
			}
		}
	};
}

interface DrawingShape {
	boolean contains(Graphics2D g2, double x, double y);
	void draw(Graphics2D g2);
	void adjustPosition(double dx, double dy);
}

class DrawingRect implements DrawingShape {

	private Rectangle2D rect;
	private Color color;
	
	public DrawingRect(Rectangle2D rect, Color color) {
		this.rect = rect;
		this.color = color;
	}

	@Override
	public void draw(Graphics2D g2) {
		g2.setColor(color);
		g2.fill(rect);
		// OR g2.fillRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
		
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
	
}

class DrawingLine implements DrawingShape {

	private Line2D line;
	private Color color;
	
	public DrawingLine(Line2D line, Color color) {
		this.line = line;
		this.color = color;
	}
	
	@Override
	public void draw(Graphics2D g2) {
		g2.setColor(color);
		g2.setStroke(new BasicStroke(5));
		g2.draw(line);
		// OR g2.drawLine((int)line.getX1(), (int)line.getY1(), (int)line.getX2(), (int)line.getY2());
	}

	@Override
	public void adjustPosition(double dx, double dy) {
		line.setLine(line.getX1() + dx, line.getY1() + dy, 
					line.getX2() + dx, line.getY2() + dy);
	}

	@Override
	public boolean contains(Graphics2D g2, double x, double y) {
		// TODO Auto-generated method stub
		return false;
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
		Rectangle2D bounds = rect.getBounds2D();
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
}

class DrawingText implements DrawingShape {
	
	private String text;
	private Font font;
	private Color color;
	private Point2D location;
	
	public DrawingText(String text, Font font, Color color, Point2D location) {
		this.text = text;
		this.font = font;
		this.color = color;
		this.location = location;
	}
	
	@Override
	public void draw(Graphics2D g2) {
		g2.setColor(color);
		g2.setFont(font);
		g2.drawString(text, (int)location.getX(), (int)location.getY());
	}

	@Override
	public void adjustPosition(double dx, double dy) {
		double newX = location.getX() + dx;
		double newY = location.getY() + dy;
		location.setLocation(newX, newY);
	}

	@Override
	public boolean contains(Graphics2D g2, double x, double y) {
		// TODO Auto-generated method stub
		return false;
	}
}