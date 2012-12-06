package client.views.mainFrame.dataArea;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import client.controller.MainController;
import client.model.*;

@SuppressWarnings("serial")
public class TableEntryTab extends JPanel implements DMListener {

	private MainController controller;
	private EntryTableModel tableModel;
	private JTable table;
	
	private JPanel scrollContents;
	private JScrollPane scroll;
	
	private DataModel dm;
	
	/**
	 * Constructor.
	 * @param controller MainController used for event handling.
	 */
	public TableEntryTab(MainController controller) {
		setLayout(new BorderLayout());

		this.controller = controller;
		table = new JTable();
		tableModel = new EntryTableModel(null);
		
		scrollContents = new JPanel(new BorderLayout());
		scroll = new JScrollPane(scrollContents);
		
		scrollContents.add(table.getTableHeader(), BorderLayout.NORTH); // might not be worthwhile.
		scrollContents.add(table, BorderLayout.CENTER);
		
		add(scroll);
	}
	
	/**
	 * Builds the table model and tells the table to validate and redraw itself.
	 * @param fields List of the fields belonging to the project.
	 * @param project Project the batch belongs to.
	 */
	public void buildTable(DataModel dataModel) {
		remove(table.getTableHeader()); // remove the old header.
		
		tableModel = new EntryTableModel(dataModel);
		table.setModel(tableModel);
		
		// set table settings
		table.setCellSelectionEnabled(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		table.getSelectionModel().addListSelectionListener(tableSelectionListener);
		add(table.getTableHeader(), BorderLayout.NORTH);
		tableModel.addTableModelListener(tableChangeListener);
		
		table.validate();
		table.repaint();
	}
	
	/**
	 * Set the MainController object for event handling.
	 * @param newController MainController to replace the current one, if any.
	 */
	public void setController(MainController newController) {
		controller = newController;
	}
	
	public MainController getController() {
		return controller;
	}
	
	/**
	 * Handles when the user types a change into one of the cells.
	 */
	private TableModelListener tableChangeListener = new TableModelListener() {

		@Override
		public void tableChanged(TableModelEvent e) {
			// TODO handle when changes are made through the table.
		}
	};
	
	/**
	 * Handles when the table has a change in selection.
	 */
	private ListSelectionListener tableSelectionListener = new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			int col = table.getSelectedColumn();
			int row = table.getSelectedRow();
			dm.select(col, row);
		}
		
	};

	public void setDataModel(DataModel dataModel) {
		if (dm != null) {
			dm.removeSelectionChangeListener(this);
		}
		dm = dataModel;
		if (dm != null) {
			dm.addSelectionChangeListener(this);
		}
		tableModel.setDataModel(dataModel);
	}

	@Override
	public void selectionChanged(ActionEvent e) {
		tableModel.removeTableModelListener(tableChangeListener);
		int col = dm.getColSelected();
		int row = dm.getRowSelected();
		if (col >= 0 && row >= 0) {
			table.setRowSelectionInterval(row, row);
			table.setColumnSelectionInterval(col, col);
		}
		tableModel.addTableModelListener(tableChangeListener);
	}
	
}
