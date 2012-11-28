package client.view.mainFrame.buttonBar;

import javax.swing.JPanel; // superclass
import javax.swing.*;

import java.util.*;

@SuppressWarnings("serial")
public class ButtonPanel extends JPanel {
	private List<JButton> buttons;
	
	private JButton btnZoomIn;
	private JButton btnZoomOut;
	private JButton btnInvert;
	private JButton btnToggleHighlight;
	private JButton btnSave;
	private JButton btnSubmit;
	
	public ButtonPanel() {
		createButtons();
		for(JButton btn : buttons) {
			add(btn);
		}
	}
	
	private void createButtons() {
		buttons = new ArrayList<JButton>();
		btnZoomIn = new JButton("Zoom In");
		buttons.add(btnZoomIn);
		btnZoomOut = new JButton("Zoom Out");
		buttons.add(btnZoomOut);
		btnInvert = new JButton("Invert Image");
		buttons.add(btnInvert);
		btnToggleHighlight = new JButton("Toggle Highlight");
		buttons.add(btnToggleHighlight);
		btnSave = new JButton("Save");
		buttons.add(btnSave);
		btnSubmit = new JButton("Submit");
		buttons.add(btnSubmit);
	}
	
}
