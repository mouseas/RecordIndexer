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
		setLayout(new BorderLayout());
		
		add(elementsHolder, BorderLayout.EAST);
		add(listScrollPane, BorderLayout.WEST);
	}
	
	public void setDataModel(DataModel dataModel) {
		if (dm != null) {
			dm.removeSelectionChangeListener(this);
		}
		dm = dataModel;
		if (dm != null) {
			dm.addSelectionChangeListener(this);
		}
		
		rowNumbersModel.setDataModel(dm);
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

	@Override
	public void selectionChanged(ActionEvent e) {
		rowList.removeListSelectionListener(rowSelectListener);
		if (dm != null && dm.getRowSelected() >= 0) {
			rowList.setSelectedIndex(dm.getRowSelected());
		}
		rowList.addListSelectionListener(rowSelectListener);
	}
	
	
}
