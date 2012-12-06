package client.model;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class EntryTableModel extends AbstractTableModel {

	protected DataModel dm;
	
//	protected List<Field> fields;
//	protected Project project;
//	protected List<Record> records;
	
	int columns; // actual number of columns in the Records
	int rows;
	
	/**
	 * Constructor. Give it the project and the field list.
	 * @param fieldsList
	 * @param proj
	 */
	public EntryTableModel(DataModel dataModel) {
		setDataModel(dataModel);
		
		
	}
	
	@Override
	public int getColumnCount() {
		if (dm == null || dm.getCurrentBatch() == null || 
				dm.getCurrentBatch().getFields() == null) {	
			return 0; // nothing to return
		}
		return dm.getCurrentBatch().getFields().size() + 1;
	}

	@Override
	public int getRowCount() {
		if (dm == null || dm.getCurrentProject() == null) { return 0; }
		return dm.getCurrentProject().getRecordsPerImage();
	}

	@Override
	public Object getValueAt(int row, int column) {
		if (dm == null || dm.getCurrentBatch() == null 
				|| dm.getCurrentBatch().getRecords() == null) { 
			return null; 
		} // nada.
		if (row < 0 || column < 0 || 
				column > columns || 
				row >= rows) {
			throw new IndexOutOfBoundsException();
		}
		if (column > 0) {
			return dm.getCurrentBatch().getRecords()
					.get(calculateIndexFromCoords(column - 1, row)).getValue();
		} else {
			return row + 1;
		}
	}
	
	@Override
	public void setValueAt(Object value, int row, int column) {
		if (value instanceof String) {
			if (dm == null || dm.getCurrentBatch() == null 
					|| dm.getCurrentBatch().getRecords() == null) { 
				return; // no way to set it
			}
			if (column > 0 && column <= columns){
				String str = (String) value;
				dm.getCurrentBatch().getRecords()
						.get(calculateIndexFromCoords(column - 1, row))
						.setValue(str);
			} else {
				System.out.println("Attempting to put string into " + row + "," + column);
			}
		} else {
			System.out.println("Attempting to put non-string into " + row + "," + column);
		}
	}
	
	@Override
	public String getColumnName(int column) {
		if (dm == null || dm.getCurrentBatch() == null || 
				dm.getCurrentBatch().getFields() == null) {	
			return null; // nothing to return
		}
		if (column > dm.getCurrentBatch().getFields().size() || column < 0) { 
			throw new IndexOutOfBoundsException();
		}
		if (column > 0) {
			return dm.getCurrentBatch().getFields().get(column - 1).getTitle();
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

	public DataModel getDataModel() {
		return dm;
	}

	public void setDataModel(DataModel dm) {
		this.dm = dm;
		columns = getColumnCount() - 1;
		rows = getRowCount();
	}
	
	/**
	 * Calculates the array index based on the desired column and row.
	 */
	private int calculateIndexFromCoords(int column, int row) {
		// order is column 0 rows 1 through n, column 1 rows 1 though n, etc.
		return (rows * column) + row;
	}

}
