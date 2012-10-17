package shared.dataTransfer;

import java.util.*;

public class Project extends ObjectWithID {
	
	private String title;
	private int recordsPerImage;
	private int firstYCoord;
	private int fieldHeight;
	private int numRows;
	private List<Field> fields;
	
	public Project(int id, int recordsPerImage, int firstYCoord, int fieldHeight,
			int numRows, String title) {
		setID(id);
		this.title = title;
		this.recordsPerImage = recordsPerImage;
		this.firstYCoord = firstYCoord;
		this.fieldHeight = fieldHeight;
		this.numRows = numRows;
	}
	
	/**
	 * Gets the y position of a row. Does not perform edge checking.
	 * Note that the first row is lineNumber = 0.
	 * @param lineNumber Which row to get the y position of
	 * @return (row height * line number) + y position of first row
	 */
	public int getY(int lineNumber) {
		return (fieldHeight * lineNumber) + firstYCoord;
	}
	
	/**
	 * Gets the number of rows in a batch, aka the number of records
	 * per image.
	 * @return
	 */
	public int getRecordsPerImage() {
		return numRows;
	}
	
	/**
	 * Gets the height, in pixels, of a row in this project.
	 * @return
	 */
	public int getRowHeight() {
		return fieldHeight;
	}
	
	/**
	 * Gets the width, in pixels, of a whole row, which should be the sum
	 * of the widths of all of the fields in this project.
	 * @return
	 */
	public int getWidth() {
		return 0;
	}
	
	/**
	 * Gets the name of the project
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Assigns a batch to a project.
	 * @param b The batch to assign to this project.
	 * @return Whether the batch was successfully assigned to the project.
	 */
	public boolean assignBatch(Batch b) {
		return false;
	}
}