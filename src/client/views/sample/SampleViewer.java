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
//		setResizable(false); // spec requires this to be true.
		// TODO Need to determine ideal scale for drawingComponent to prevent it
		// being too big or too small.
		
		add(Box.createRigidArea(spacer));
		buildTop();
		add(Box.createRigidArea(spacer));
		buildBottom();
		add(Box.createRigidArea(spacer));

		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		screenSize.setSize(screenSize.getWidth() - 250, screenSize.getHeight() - 250);
		setMaximumSize(screenSize); // prevents window being too big for screen.
		
		pack();
	}

	private void buildTop() {
		top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
		
		drawingComponent = new DrawingComponent();
		drawingComponent.setScale(0.5);
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
	
	private ActionListener closeListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			controller.closeViewer();
		}
	};
	
}
