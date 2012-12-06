package client.views.mainFrame.dataArea;

import javax.swing.*;

import client.controller.MainController;

import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class DataAreaPanel extends JPanel {

	private JTabbedPane leftPane;
	private TableEntryTab tableTab;
	private FormEntryTab formTab;
	
	private JTabbedPane rightPane;
	private JSplitPane split;
	
	@SuppressWarnings("unused")
	private double barPosition;
	
	@SuppressWarnings("unused")
	private MainController controller;
	
	public DataAreaPanel() {
		buildLeftPane();
		buildRightPane();
		
		setMinimumSize(new Dimension(150,50));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		addComponentListener(resizeListener);
		
		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
		// TODO add a listener that calculates the split's position whenever it is changed.
		barPosition = 0.5;
		add(split);
	}

	public void setController(MainController c) {
		controller = c;
		tableTab.setController(c);
		
	}

	public TableEntryTab getTableTab() {
		return tableTab;
	}

	public void setTableTab(TableEntryTab tableTab) {
		this.tableTab = tableTab;
	}

	public FormEntryTab getFormTab() {
		return formTab;
	}

	public void setFormTab(FormEntryTab formTab) {
		this.formTab = formTab;
	}

	public int getSplitterPos() {
		return split.getDividerLocation();
	}
	
	public void setSplitterPos(int pos) {
		split.setDividerLocation(pos);
		barPosition = pos / split.getWidth();
	}
	
	private void buildLeftPane() {
		leftPane = new JTabbedPane();
		leftPane.setPreferredSize(new Dimension(400,150));
		leftPane.setMinimumSize(new Dimension(75,50));
		tableTab = new TableEntryTab(controller);
		formTab = new FormEntryTab();
		leftPane.addTab("Table Entry", tableTab);
		leftPane.addTab("Form Entry", formTab);
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
			
			// TODO Fix. disabled for now.
//			int absLoc = (int)(barPosition * split.getWidth());
//			split.setDividerLocation(absLoc); 
		}
	};

}
