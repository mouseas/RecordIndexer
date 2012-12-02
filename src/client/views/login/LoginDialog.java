package client.views.login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import client.controller.MainController;

@SuppressWarnings("serial")
public class LoginDialog extends JDialog {
	
	private MainController controller;
	
	private JPanel top;
	private JPanel bottom;
	
	private JButton btnLogin;
	private JButton btnQuit;
	
	private JTextField fldUsername;
	private JTextField fldPassword;
	
	private static final Dimension spacer = new Dimension(5,5);
	private static final Dimension fieldSize = new Dimension(250, 20);
	private static final Dimension labelSize = new Dimension(75, 20);
	
	public LoginDialog(JFrame frame, MainController controller) {
		super(frame);
		
		this.controller = controller;
		
		setTitle("Log In");
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
		centerOnScreen();
	}

	public MainController getController() {
		return controller;
	}

	public void setController(MainController controller) {
		this.controller = controller;
	}

	private ActionListener loginListener = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String username = fldUsername.getText();
			String password = fldPassword.getText();
			if (controller != null) {
				controller.login(username, password);
			}
		}
	};
	
	private ActionListener quitListener = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			dispose();
			if (controller != null) {
				controller.exit();
			}
		}
	};
	
	/**
	 * Build the top half of the panel, namely the username and password fields
	 * and labels.
	 */
	private void buildTop() {
		top = new JPanel();
		JPanel labels = new JPanel();
		JPanel boxes = new JPanel();
		
		top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
		labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
		boxes.setLayout(new BoxLayout(boxes, BoxLayout.Y_AXIS));
		
		JLabel temp = new JLabel("Username:");
		temp.setPreferredSize(labelSize);
		labels.add(temp);
		labels.add(Box.createRigidArea(spacer));
		temp = new JLabel("Password:");
		temp.setPreferredSize(labelSize);
		labels.add(temp);
		
		// TODO remove username and password from fields once done testing
		fldUsername = new JTextField("test1"); // debug - put in test1 user info to save me typing it each time.
		fldUsername.setPreferredSize(fieldSize);
		fldPassword = new JPasswordField("test1"); // debug
		fldPassword.setPreferredSize(fieldSize);
		boxes.add(fldUsername);
		boxes.add(Box.createRigidArea(spacer));
		boxes.add(fldPassword);

		top.add(Box.createRigidArea(spacer));
		top.add(labels);
		top.add(Box.createRigidArea(spacer));
		top.add(boxes);
		top.add(Box.createRigidArea(spacer));
		
		add(top);
	}
	
	private void buildBottom() {
		bottom = new JPanel(/*new BoxLayout(this, BoxLayout.X_AXIS)*/);
		
		btnLogin = new JButton("Log In");
		btnLogin.addActionListener(loginListener);
		bottom.add(btnLogin);
		
		btnQuit = new JButton("Quit");
		btnQuit.addActionListener(quitListener);
		bottom.add(btnQuit);
		
		add(bottom);
	}
	
	private void centerOnScreen() {
		Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension loginSize = this.getSize();
		setLocation((screenDimensions.width - loginSize.width) / 2,
					(screenDimensions.height - loginSize.height) / 2);
	}
	
	
}
