package client.views.mainFrame.dataArea;

import javax.swing.*;

import controller.Controller;

import java.awt.*;

@SuppressWarnings("serial")
public class DataAreaPanel extends JPanel {

	private JTabbedPane leftPane;
	private JTabbedPane rightPane;
	private JSplitPane split;
	
	private Controller controller;
	
	public DataAreaPanel() {
		leftPane = new JTabbedPane();
//		leftPane.setPreferredSize(new Dimension(400,150));
//		leftPane.setMinimumSize(new Dimension(0,75));
		leftPane.addTab("Table Entry", new TableEntryTab());
		leftPane.addTab("Form Entry", new FormEntryTab());
		rightPane = new JTabbedPane();
//		rightPane.setPreferredSize(new Dimension(400,150));
//		rightPane.setMinimumSize(new Dimension(0,75));
		rightPane.addTab("Field Help", new FieldHelpTab());
		rightPane.addTab("Image Navigation", new ImageNavTab());
		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
		add(split);
	}

	public void setController(Controller c) {
		controller = c;
	}
}
