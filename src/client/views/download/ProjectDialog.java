package client.views.download;

import java.awt.Dimension;
import java.util.List;

import javax.swing.*;

import shared.dataTransfer.Project;
import client.controller.Controller;

@SuppressWarnings("serial")
public class ProjectDialog extends JDialog {

	private Controller controller;
	
	private JComboBox projectDropdown;
	private JButton btnViewSample;
	private JButton btnCancel;
	private JButton btnDownload;
	
	private JPanel top;
	private JPanel bottom;
	
	private List<Project> projects;
	
	private static final Dimension spacer = new Dimension(5,5);
	private static final Dimension fieldSize = new Dimension(250, 20);
	private static final Dimension labelSize = new Dimension(75, 20);
	
	public ProjectDialog(JFrame frame, Controller controller, List<Project> projects) {
		super(frame);
		
		this.projects = projects;
		this.controller = controller;
		
		setTitle("Download Batch");
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setModal(true);
		setResizable(false);
		
		add(Box.createRigidArea(spacer));
		buildTop();
		add(Box.createRigidArea(spacer));
		buildBottom();
		add(Box.createRigidArea(spacer));
		
		pack();
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	private void buildTop() {
		top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));

		top.add(Box.createRigidArea(spacer));
		top.add(new JLabel("Project"));
		top.add(Box.createRigidArea(spacer));
		top.add(buildDropdown());
		top.add(Box.createRigidArea(spacer));
		btnViewSample = new JButton("View Sample");
		top.add(btnViewSample);
		top.add(Box.createRigidArea(spacer));
		
		add(top);
	}

	private void buildBottom() {
		bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));

		btnCancel = new JButton("Cancel");
		btnDownload = new JButton("Download");
		
		bottom.add(btnCancel);
		bottom.add(Box.createRigidArea(spacer));
		bottom.add(btnDownload);
		
		add(bottom);
	}
	
	private JComponent buildDropdown() {
		String[] projectNames = new String[projects.size()];
		for (int i = 0; i < projects.size(); i++) {
			projectNames[i] = projects.get(i).getTitle();
		}
		projectDropdown = new JComboBox(projectNames);
		return projectDropdown;
	}

}
