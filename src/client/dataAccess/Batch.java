package client.dataAccess;

import java.util.*;
import shared.dataTransfer.*;

public class Batch{
	
	public Batch(XferBatch b) {
		
	}
	
	/**
	 * Gets the image associated with this batch.
	 * @return
	 */
	public Image getImage() {
		return null;
	}
	
	/**
	 * Gets the list of fields from the project this batch belongs to
	 * @return
	 */
	public List<Field> getFields() {
		return null;
	}
	
	/**
	 * Gets the records attached to this batch
	 * @return
	 */
	public List<Record> getRecords() {
		return null;
	}
	
	/**
	 * Gets the project this batch belongs to
	 * @return
	 */
	public Project getProject() {
		return null;
	}
	
	/**
	 * Gets the total number of rows (records) for this batch. In the LDS
	 * indexing program, this often varies by +/- 1 from the project standard.
	 * @return
	 */
	public int getNumRecords() {
		return 0;
	}
	
	/**
	 * Gets the x position of a given field
	 * @param f Field to get the x position of
	 * @return
	 */
	public int getFieldX(Field f) {
		return 0;
	}
	
	/**
	 * Gets the y position of a specific row
	 * @param lineNumber Which row to get the y position of
	 * @return
	 */
	public int getFieldY(int lineNumber) {
		return 0;
	}
	
	/**
	 * Gets the height of a row for this batch's project.
	 * @return
	 */
	public int getRowHeight() {
		return 0;
	}
	
	
	
//	private int numRecords;
//	private List<Field> fields;
}
