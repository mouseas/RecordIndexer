package client.views.mainFrame.dataArea;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import java.awt.BorderLayout;
import java.util.*;

import client.controller.MainController;
import client.model.*;
import shared.dataTransfer.*;

@SuppressWarnings("serial")
public class TableEntryTab extends JPanel {

	private MainController controller;
	private TableModel tableModel;
	private JTable table;
	
	private static final TableModel BLANK_TABLE_MODEL = new JTable().getModel();
	
	private JPanel scrollContents;
	private JScrollPane scroll;
	
	/**
	 * Constructor.
	 * @param controller MainController used for event handling.
	 */
	public TableEntryTab(MainController controller) {
		setLayout(new BorderLayout());

		this.controller = controller;
		tableModel = null;
		table = new JTable();
		
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
	public void buildTable(List<Field> fields, Project project, List<Record> records) {
		remove(table.getTableHeader()); // remove the old header.
		if (fields == null || project == null || records == null ||
				fields.size() < 1 || records.size() < 1) {
			tableModel = BLANK_TABLE_MODEL; // nothing to display
			table.setModel(tableModel);
		} else {
			tableModel = new EntryTableModel(fields, project, records);
			table.setModel(tableModel);
			
			// set table settings
			table.setCellSelectionEnabled(true);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.getTableHeader().setReorderingAllowed(false);
			add(table.getTableHeader(), BorderLayout.NORTH);
			tableModel.addTableModelListener(tableSelectionListener);
		}
		table.validate();
		table.repaint();
	}
	
	/**
	 * Builds the table model and tells the table to validate and redraw itself.
	 * @param dm Data Model object to build the table from.
	 */
	public void buildTable(DataModel dm) {
		Batch batch = dm.getCurrentBatch();
		if (batch != null && dm.getCurrentProject() != null) {
			buildTable(batch.getFields(),
					dm.getCurrentProject(),
					batch.getRecords());
		} else {
			buildTable(null, null, null); // build empty table.
		}
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
	
	private TableModelListener tableSelectionListener = new TableModelListener() {

		@Override
		public void tableChanged(TableModelEvent e) {
			// TODO Update the form view's selected record + field
			
		}
		
	};
	
}
