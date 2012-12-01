package client.views.sample;

import javax.swing.*;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import client.controller.SampleController;
import client.views.shared.DrawingComponent;

/**
 * Large window able to display a sample image for a given
 * project downloaded from the server.
 * 
 * @author Martin Carney
 *
 */
@SuppressWarnings("serial")
public class SampleViewer extends JDialog {

	private Image image;
	
	private SampleController controller;
	
	private JPanel top;
	private JPanel bottom;
	
	private JButton btnClose;
	private DrawingComponent drawingComponent;

	private static final Dimension spacer = new Dimension(5,5);
	
	public SampleViewer(Image image, SampleController controller, String projectName) {
		super();
		
		this.image = image;
		this.controller = controller;
		
		setTitle("Sample Image from " + projectName);
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setModal(true);
		setResizable(false); // spec requires this to be set to false.
		
		add(Box.createRigidArea(spacer));
		buildTop();
		add(Box.createRigidArea(spacer));
		buildBottom();
		add(Box.createRigidArea(spacer));

		
		pack();
	}

	private void buildTop() {
		top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
		
		drawingComponent = new DrawingComponent();
		drawingComponent.setScale(determineScale(image));
		drawingComponent.addImage(image);
		top.add(drawingComponent);
		
		add(top);
	}

	private void buildBottom() {
		bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
		
		btnClose = new JButton("Close");
		btnClose.addActionListener(closeListener);
		bottom.add(btnClose);
		
		add(bottom);
	}
	
	/**
	 * Determines the appropriate scale factor based on the size of the image
	 * and of the user's screen.
	 * @param image Image to check against
	 * @return Double value to use for scaling the view image.
	 * Returns the smallest of 1.0, the 
	 */
	private double determineScale(Image image) {
		final int MARGIN_X_SIZE = 60;
		final int MARGIN_Y_SIZE = 120; // leaves enough room for button on the bottom
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		// reduce the dimension by the margins given above.
		screenSize.setSize(screenSize.getWidth() - MARGIN_X_SIZE,
							screenSize.getHeight() - MARGIN_Y_SIZE);
		double scaleX = 1.0;
		double scaleY = 1.0;
		
		// calculate X and Y scale factors
		if (screenSize.getWidth() < image.getWidth(null)) {
			scaleX = screenSize.getWidth() / image.getWidth(null);
		}
		if (screenSize.getHeight() < image.getHeight(null)) {
			scaleY = screenSize.getHeight() / image.getHeight(null);
		}
		
		// return the smaller scale factor, or 1.0 if both are > 1
		return Math.min(1.0, Math.min(scaleX, scaleY));
	}
	
	private ActionListener closeListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			controller.closeViewer();
		}
	};
	
}
