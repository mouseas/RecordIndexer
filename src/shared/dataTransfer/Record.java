package shared.dataTransfer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * A Record object. Holds a single value, as well as where it comes from;
 * which batch, which field (column), and which row the value corresponds to.
 * @author Martin
 *
 */
public class Record extends DataTransferObject {

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
		setValue(value);
	}
	
	/**
	 * Gets the batch that this record belongs to. Kind of like y position ballpark.
	 * @return
	 */
	public int getBatchID() {
		return batchID;
	}

	/**
	 * Gets the row number of this record within its batch. Kind of like y position.
	 * @return
	 */
	public int getRowNumber() {
		return rowNumber;
	}
	
	/**
	 * Returns the value of the individual record
	 * @return String value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Assigns a value to the record
	 * @param newVal The string to be set as the value for this record entry
	 */
	public void setValue(String newVal) {
		if (newVal == null) {
			value = "";
		} else {
			value = newVal;
		}
		
	}

	/**
	 * Get the field ID for this record entry. Kind of like x position in the table.
	 * @return
	 */
	public int getFieldID() {
		return fieldID;
	}
	
	public static String serialize(Record record) {
		XStream xstream = new XStream(new DomDriver());
		return xstream.toXML(record);
	}
	
}