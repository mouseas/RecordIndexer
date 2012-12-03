package client.model;

import javax.swing.table.AbstractTableModel;

import java.util.*;

import shared.dataTransfer.*;

@SuppressWarnings("serial")
public class EntryTableModel extends AbstractTableModel {

	protected List<Field> fields;
	protected Project project;
	
	protected Record[][] records;
	
	/**
	 * Constructor. Give it the project and the field list.
	 * @param fieldsList
	 * @param proj
	 */
	public EntryTableModel(List<Field> fieldsList, Project proj, Record[][] recordGrid) {
		fields = fieldsList;
		project = proj;
		records = recordGrid;
	}
	
	@Override
	public int getColumnCount() {
		if (fields == null) { return 0; }
		return fields.size() + 1;
	}

	@Override
	public int getRowCount() {
		if (project == null) { return 0; }
		return project.getRecordsPerImage();
	}

	@Override
	public Object getValueAt(int row, int column) {
		if (records == null) { return null; } // nada.
		if (row < 0 || column < 0 || 
				column > records.length || 
				row >= records[0].length) {
			throw new IndexOutOfBoundsException();
		}
		if (column > 0) {
			return records[column - 1][row].getValue();
		} else {
			return row + 1;
		}
	}
	
	@Override
	public void setValueAt(Object value, int row, int column) {
		if (value instanceof String && column > 0) {
			String str = (String) value;
			records[column - 1][row].setValue(str);
		} else {
			System.out.println("Attempting to put non-string into " + row + "," + column);
		}
	}
	
	@Override
	public String getColumnName(int column) {
		if (column > fields.size() || column < 0) { 
			throw new IndexOutOfBoundsException();
		}
		if (column > 0) {
			return fields.get(column - 1).getTitle();
		} else {
			return "Record Number";
		}
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if (column > 0) {
			return true; // column 0 is "record number"
		} else {
			return false;
		}
	}

}
