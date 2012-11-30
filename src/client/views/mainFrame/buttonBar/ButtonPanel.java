package client.views.mainFrame.buttonBar;

import javax.swing.*;

import client.controller.Controller;


import java.awt.*;
import java.awt.event.*;
import java.util.*;

@SuppressWarnings("serial")
public class ButtonPanel extends JPanel {
	private ArrayList<JButton> buttons;
	
	private JButton btnZoomIn;
	private JButton btnZoomOut;
	private JButton btnInvert;
	private JButton btnToggleHighlight;
	private JButton btnSave;
	private JButton btnSubmit;

	private Controller controller;
	
	public ButtonPanel() {
		createButtons();
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		for(JButton btn : buttons) {
			add(btn);
			add(Box.createRigidArea(new Dimension(4,3)));
		}
		add(Box.createHorizontalGlue());
	}
	
	private void createButtons() {
		buttons = new ArrayList<JButton>();
		
		btnZoomIn = new JButton("Zoom In");
		btnZoomIn.addActionListener(zoomInListener);
		buttons.add(btnZoomIn);
		
		btnZoomOut = new JButton("Zoom Out");
		btnZoomOut.addActionListener(zoomOutListener);
		buttons.add(btnZoomOut);
		
		btnInvert = new JButton("Invert Image");
		btnInvert.addActionListener(invertListener);
		buttons.add(btnInvert);
		
		btnToggleHighlight = new JButton("Toggle Highlight");
		btnToggleHighlight.addActionListener(hilightListener);
		buttons.add(btnToggleHighlight);
		
		btnSave = new JButton("Save");
		btnSave.addActionListener(saveListener);
		buttons.add(btnSave);
		
		btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(submitListener);
		buttons.add(btnSubmit);
	}

	public void setController(Controller c) {
		controller = c;
	}
	
	private ActionListener zoomInListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			controller.zoomIn();
		}
	};
	
	private ActionListener zoomOutListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			controller.zoomOut();
		}
	};
	
	private ActionListener invertListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			controller.invertImage();
		}
	};
	
	private ActionListener hilightListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			controller.toggleHilight();
		}
	};
	
	private ActionListener saveListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			controller.save();
		}
	};
	
	private ActionListener submitListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			controller.submitBatch();
		}
	};
	
}
