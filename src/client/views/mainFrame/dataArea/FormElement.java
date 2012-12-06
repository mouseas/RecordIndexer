package client.views.mainFrame.dataArea;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.*;

@SuppressWarnings("serial")
public class FormElement extends JPanel {
	
	private JLabel label;
	private JTextField textField;
	private int column;
	
	private FormEntryTab parent;
	
	/**
	 * Primary constructor, takes the label text and a value for the text box.
	 * @param labelStr
	 * @param valueStr
	 */
	public FormElement(String labelStr, String valueStr, FormEntryTab parent, int column) {
		this.parent = parent;
		this.column = column;
		
		label = new JLabel(labelStr);
		label.setPreferredSize(new Dimension(150, 15));
		textField = new JTextField(valueStr);
		textField.addActionListener(changeListener);
		textField.addFocusListener(focusListener);
		textField.setPreferredSize(new Dimension(150, 15));
		
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
	 * When the textbox has something typed in it, this propogates the
	 * value over to the DataModel.
	 */
	private ActionListener changeListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String value = textField.getText();
			parent.setValue(column, value);
		}
		
	};
	
	/**
	 * When the textbox gets focus, changes the selection (synched with table view)
	 */
	private FocusListener focusListener = new FocusListener() {
		@Override
		public void focusGained(FocusEvent e) {
			textField.removeFocusListener(focusListener);
			parent.selectColumn(column);
			textField.addFocusListener(focusListener);
		}

		@Override
		public void focusLost(FocusEvent e) {
			// trigger an ActionEvent. Apparently changing fields doesn't trigger it naturally.
			changeListener.actionPerformed(new ActionEvent(this, 0, ""));
		}
		
	};

	/**
	 * Prepare the FormElement for disposal.
	 */
	public void preDispose() {
		textField.removeActionListener(changeListener);
	}
}
