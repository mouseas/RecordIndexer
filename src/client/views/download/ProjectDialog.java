package client.views.download;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;

import shared.dataTransfer.Project;
import client.controller.MainController;

/**
 * Dialog Box to select which project from which to download
 * a batch. Contains a drop-down with a list of current projects,
 * a button to download and view a sample image, and cancel and
 * download buttons.
 * @author Martin Carney
 *
 */
@SuppressWarnings("serial")
public class ProjectDialog extends JDialog {

	private MainController controller;
	
	private JComboBox<String> projectDropdown;
	private JButton btnViewSample;
	private JButton btnCancel;
	private JButton btnDownload;
	
	private JPanel top;
	private JPanel bottom;
		
	private List<Project> projects;
	
	private static final Dimension spacer = new Dimension(5,5);
	
	/**
	 * Constructor. Requires the window it is a child of, the controller object,
	 * and a list of projects to populate the dropdown.
	 */
	public ProjectDialog(JFrame frame, MainController controller, List<Project> projects) {
		super(frame);
		
		this.projects = projects;
		this.controller = controller;
		
		setTitle("Download Batch");
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setModal(true);
		setResizable(false);
		
		Point loc = new Point(frame.getLocation().x + 50, frame.getLocation().y + 50);
		setLocation(loc);
		
		add(Box.createRigidArea(spacer));
		buildTop();
		add(Box.createRigidArea(spacer));
		buildBottom();
		add(Box.createRigidArea(spacer));
		
		pack();
	}

	/**
	 * Gets the dialog's controller
	 * @return
	 */
	public MainController getController() {
		return controller;
	}

	/**
	 * Sets the dialog's controller.
	 * @param controller
	 */
	public void setController(MainController controller) {
		this.controller = controller;
	}

	/**
	 * Build the top half of the dialog, including label, drop-down selector,
	 * and view sample button.
	 */
	private void buildTop() {
		top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));

		top.add(Box.createRigidArea(spacer));
		top.add(new JLabel("Project"));
		top.add(Box.createRigidArea(spacer));
		
		top.add(buildDropdown());
		
		top.add(Box.createRigidArea(spacer));
		btnViewSample = new JButton("View Sample");
		btnViewSample.addActionListener(sampleListener);
		
		top.add(btnViewSample);
		top.add(Box.createRigidArea(spacer));
		
		add(top);
	}

	/**
	 * Build the bottom half of the
	 */
	private void buildBottom() {
		bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(cancelListener);
		btnDownload = new JButton("Download");
		btnDownload.addActionListener(downloadListener);
		
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
		projectDropdown = new JComboBox<String>(projectNames);
		return projectDropdown;
	}
	
	private Project getProjectByName(String projName) {
		for (int i = 0; i < projects.size(); i++) {
			if (projects.get(i).getTitle().equals(projName)) {
				return projects.get(i);
			}
		}
		new Exception("Project name not found in project list!").printStackTrace();
		return null;
	}
	
	private ActionListener cancelListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			controller.closeDownloadDialog();
		}
	};
	
	private ActionListener downloadListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			Project p = getProjectByName((String)projectDropdown.getSelectedItem());
			controller.downloadNextBatch(p);
		}
	};
	
	private ActionListener sampleListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			Project p = getProjectByName((String)projectDropdown.getSelectedItem());
			controller.viewSample(p);
		}
	};
}
