package client.views.mainFrame.dataArea;

import java.awt.BorderLayout;

import javax.swing.*;

@SuppressWarnings("serial")
public class FormElement extends JPanel {
	
	private JLabel label;
	private JTextField textField;
	
	/**
	 * Primary constructor, takes the label text and a value for the text box.
	 * @param labelStr
	 * @param valueStr
	 */
	public FormElement(String labelStr, String valueStr) {
		label = new JLabel(labelStr);
		textField = new JTextField(valueStr);
		
		setLayout(new BorderLayout());
		add(label, BorderLayout.WEST);
		add(textField, BorderLayout.EAST);
	}
	
	/**
	 * Sets the form element's text.
	 * @param text
	 */
	public void setText(String text) {
		textField.setText(text);
	}
	
	/**
	 * Simpler constructor with the label and no value string.
	 * @param labelStr
	 */
	public FormElement(String labelStr) {
		this(labelStr, "");
	}
}
