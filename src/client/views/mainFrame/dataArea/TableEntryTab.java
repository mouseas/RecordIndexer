package client.views.mainFrame.dataArea;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import java.awt.BorderLayout;
import java.util.*;

import client.controller.MainController;
import client.model.*;
import shared.dataTransfer.*;

@SuppressWarnings("serial")
public class TableEntryTab extends JPanel {

	private MainController controller;
	private EntryTableModel tableModel;
	private JTable table;
	
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
	public void buildTable(List<Field> fields, Project project, Record[][] records) {
		remove(table.getTableHeader()); // remove the old header.
		
		tableModel = new EntryTableModel(fields, project, records);
		table.setModel(tableModel);
		
		// set table settings
		table.setCellSelectionEnabled(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		add(table.getTableHeader(), BorderLayout.NORTH);
		tableModel.addTableModelListener(tableSelectionListener);
		
		table.validate();
		table.repaint();
	}
	
	/**
	 * Builds the table model and tells the table to validate and redraw itself.
	 * @param dm Data Model object to build the table from.
	 */
	public void buildTable(DataModel dm) {
		buildTable(dm.getCurrentBatch().getFields(),
				dm.getCurrentProject(),
				dm.getCurrentRecordGrid());
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
