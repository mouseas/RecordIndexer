package shared.dataTransfer;

import java.util.List;


public class Record extends ObjectWithID {

	private int batchID;
	private int fieldID;
	private int rowNumber;
	private String value;
	
	public Record(int id, int batchID, int fieldID, int rowNumber, 
			String value) {
		setID(id);
		this.batchID = batchID;
		this.fieldID = fieldID;
		this.rowNumber = rowNumber;
		this.value = value;
	}
	
	/**
	 * Gets the batch that this record belongs to
	 * @return
	 */
	public Batch getBatch() {
		return null;
	}
	
	/**
	 * Gets the list of fields from the project this record belongs to
	 * @return
	 */
	public List<Field> getFields() {
		return null;
	}

	/**
	 * Gets the row number of this record within its batch
	 * @return
	 */
	public int getRowNumber() {
		return 0;
	}
	
	/**
	 * Returns an editable list of values, used when modifying a record.
	 * @return List of String values corresponding to the project's fields.
	 */
	public List<String> getValues() {
		return null;
	}
	
	/**
	 * Returns an un-editable list of values, used when reading a record.
	 * @return Unalterable list of String values corresponding to the 
	 * project's fields.
	 */
	public List<String> getLockedValues() {
		return null;
	}
	
	/**
	 * Gets a single value based on the field. Returns null if the field is
	 * not part of the record.
	 * @param f Field to get the value of.
	 * @return String value of the record in that field's column, or null if
	 * the field is not in this record.
	 */
	public String getValueByField(Field f) {
		return null;
	}
	
	/**
	 * Checks a record for a matching search term within a given search field.
	 * Always checks the first instance of the search field (which should be
	 * the only instance).
	 * @param searchTerm The term to check for
	 * @param searchField The field to check in
	 * @return The value found, or null if nothing found.
	 */
	public String search(String searchTerm, Field searchField) {
		return null;
	}
}
//DROP TABLE record_values;
//CREATE TABLE record_values
//(
//	id integer not null primary key autoincrement,
//	batch_id integer not null,
//	field_id integer not null,
//	row_number integer not null,
//	value varchar(255),
//	foreign key(batch_id) references batches(id),
//	foreign key(field_id) references fields(id)
//);