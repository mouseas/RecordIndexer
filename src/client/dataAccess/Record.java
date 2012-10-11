package client.dataAccess;

import java.util.*;
import shared.dataTransfer.*;

public class Record extends DataAccessObject{
	
	public Record (List<XferRecordValue> values) {
		
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
