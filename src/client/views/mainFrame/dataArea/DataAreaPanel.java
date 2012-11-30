package client.views.mainFrame.dataArea;

import javax.swing.*;

import client.controller.Controller;

import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class DataAreaPanel extends JPanel {

	private JTabbedPane leftPane;
	private JTabbedPane rightPane;
	private JSplitPane split;
	
	private double barPosition;
	
	@SuppressWarnings("unused")
	private Controller controller;
	
	public DataAreaPanel() {
		buildLeftPane();
		buildRightPane();
		
		setMinimumSize(new Dimension(150,50));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		addComponentListener(resizeListener);
		
		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
		barPosition = 0.5;
		add(split);
	}

	public void setController(Controller c) {
		controller = c;
	}
	
	private void buildLeftPane() {
		leftPane = new JTabbedPane();
		leftPane.setPreferredSize(new Dimension(400,150));
		leftPane.setMinimumSize(new Dimension(75,50));
		leftPane.addTab("Table Entry", new TableEntryTab());
		leftPane.addTab("Form Entry", new FormEntryTab());
		
	}
	
	private void buildRightPane() {

		rightPane = new JTabbedPane();
		rightPane.setPreferredSize(new Dimension(400,150));
		rightPane.setMinimumSize(new Dimension(75,50));
		rightPane.addTab("Field Help", new FieldHelpTab());
		rightPane.addTab("Image Navigation", new ImageNavTab());
	}
	
	private ComponentAdapter resizeListener = new ComponentAdapter() {
		@Override
		public void componentResized(ComponentEvent e) {
			// Set the divider position based on the proportion rather than
			// absolute position.
			int absLoc = (int)(barPosition * split.getWidth());
			split.setDividerLocation(absLoc);
		}
	};

}
