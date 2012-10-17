package client.dataAccess;

import shared.dataTransfer.*;

public class ProjectOld extends ObjectWithID {
	
	public ProjectOld(Project p) {
		
	}
	
	/**
	 * Gets the y position of a row. Does not perform edge checking.
	 * Note that the first row is lineNumber = 0.
	 * @param lineNumber Which row to get the y position of
	 * @return (row height * line number) + y position of first row
	 */
	public int getY(int lineNumber) {
		return 0;
	}
	
	/**
	 * Gets the number of rows in a batch, aka the number of records
	 * per image.
	 * @return
	 */
	public int getRecordsPerImage() {
		return 0;
	}
	
	/**
	 * Gets the height, in pixels, of a row in this project.
	 * @return
	 */
	public int getRowHeight() {
		return 0;
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
		return "";
	}
	
	/**
	 * Assigns a batch to a project.
	 * @param b The batch to assign to this project.
	 * @return Whether the batch was successfully assigned to the project.
	 */
	public boolean assignBatch(BatchOld b) {
		return false;
	}
	
}
//id integer not null primary key autoincrement,
//title varchar(255) not null,
//records_per_image integer not null,
//first_y_coord integer not null,
//width integer not null,
//record_height integer not null