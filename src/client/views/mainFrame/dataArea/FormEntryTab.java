package client.views.mainFrame.dataArea;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import client.model.DMListener;
import client.model.DataModel;
import client.model.EntryFormListModel;

@SuppressWarnings("serial")
public class FormEntryTab extends JPanel implements DMListener {
	
	@SuppressWarnings("unused")
	private List<FormElement> rowElements;
	private JPanel elementsHolder;
	private JList<String> rowList;
	private JScrollPane listScrollPane;
	private EntryFormListModel rowNumbersModel;
	
	private DataModel dm;
	
	public FormEntryTab() {
		rowNumbersModel = new EntryFormListModel(dm);
		rowList = new JList<String>(rowNumbersModel);
		rowList.setSelectedIndex(0);
		rowList.addListSelectionListener(rowSelectListener);
		
		listScrollPane = new JScrollPane(rowList);
		listScrollPane.setPreferredSize(new Dimension(50, 300));
		
		rowElements = new ArrayList<FormElement>();
		
		elementsHolder = new JPanel();
		elementsHolder.setLayout(new BoxLayout(elementsHolder, BoxLayout.Y_AXIS));
		JScrollPane elementScroll = new JScrollPane(elementsHolder);
		setLayout(new BorderLayout());
		
		add(elementScroll, BorderLayout.EAST);
		add(listScrollPane, BorderLayout.WEST);
	}
	
	/**
	 * Changes the DataModel the form uses for rendering and editing.
	 * @param dataModel
	 */
	public void setDataModel(DataModel dataModel) {
		if (dm != null) {
			dm.removeSelectionChangeListener(this); 
			// move listening from old DataModel to new one.
		}
		dm = dataModel;
		if (dm != null) {
			dm.addSelectionChangeListener(this);
		}
		rowNumbersModel.setDataModel(dm);
		rebuildFormElements();
	}
	
	/**
	 * Sets a value by column, using the current row selection from the JList on the left.
	 * @param col
	 * @param value
	 */
	public void setValue(int col, String value) {
		int row = rowList.getSelectedIndex();
		dm.setValue(col, row, value);
	}

	/**
	 * Whenever the selection is changed by another part of the program,
	 * this updates the selection for the table.
	 */
	@Override
	public void selectionChanged(ActionEvent e) {
		if (dm != null && dm.getRowSelected() >= 0) {
			rowList.removeListSelectionListener(rowSelectListener); 
			// disable listener temporarily to prevent looping.
			
			int row = dm.getRowSelected();
			int column = dm.getColSelected();
			rowElements.get(column).requestFocus();
			for (int i = 0; i < rowElements.size(); i++) {
				String value = dm.getValue(i, row);
				rowElements.get(i).setText(value);
			}
			rowList.setSelectedIndex(row);
			rowList.addListSelectionListener(rowSelectListener);
		}
	}

	/**
	 * Select a specific column.
	 * @param column
	 */
	public void selectColumn(int column) {
		dm.removeSelectionChangeListener(this);
		dm.selectColumn(column);
		dm.addSelectionChangeListener(this);
	}
	
	/**
	 * Sets the text cursor into the correct JTextField in the form elements.
	 */
	public void setFormFocus() {
		int column = dm.getColSelected();
		if (rowElements.size() > column) {
			System.out.println("FormEntryTab.setFormFocus()");
			rowElements.get(column).requestFocusInWindow();
		}
	}
	
	/**
	 * Discards the current list of FormElements and builds a new one. Use when
	 * changing the number of items in the left-side list.
	 */
	public void rebuildFormElements() {
		elementsHolder.removeAll();
		for (int i = 0; i < rowElements.size(); i++) {
			rowElements.get(i).preDispose();
		}
		rowElements.clear();
		if (dm != null && dm.getCurrentBatch() != null && 
				dm.getCurrentBatch().getFields() != null) {
			int count = dm.getCurrentBatch().getFields().size();
			for (int i = 0; i < count; i++) {
				String fieldName = dm.getCurrentBatch().getFields().get(i).getTitle();
				FormElement elem = new FormElement(fieldName, "", this, i);
				elementsHolder.add(elem);
				rowElements.add(elem);
			}
		}
	}

	/**
	 * Handles when an item in the list on the left is selected. Specifically,
	 * it updates the DataModel about a change in selection.
	 */
	private ListSelectionListener rowSelectListener = new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(!e.getValueIsAdjusting()) { // is the user done making a selection?
				int rowSelected = rowList.getSelectedIndex();
				if (rowSelected >= 0) {
					dm.selectRow(rowSelected);
				}
			}
		}
	};
	
	
}
