package shared.dataTransfer;

import java.util.List;

public class Field extends DataTransferObject {
	
	private int projectID;
	private String title;
	private int xCoord;
	private int width;
	private String helpHtml;
	private String knownDataLocation;
	
	public Field (int id, int projectID, String title, int xCoord,
			int width, String helpHtml, String knownData) {
		setID(id);
		this.projectID = projectID;
		this.title = title;
		this.xCoord = xCoord;
		this.width = width;
		this.helpHtml = helpHtml;
		this.knownDataLocation = knownData;
	}
	
	/**
	 * Gets the project this field belongs to
	 * @return
	 */
	public int getProjectID() {
		return projectID;
	}
	
	/**
	 * Gets the x position that this field (and its associated column) is at.
	 * @return
	 */
	public int getXCoord() {
		return xCoord;
	}
	
	/**
	 * Gets the actual name of the field.
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Gets the width, in pixels, of the field
	 * @return
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Gets all the possible entries accepted for this field.
	 * @return Non-editable list of acceptable entries for the field.
	 */
	public List<String> getKnownData() {
		System.out.println("Not implemented yet!");
		return null;
	}
	
	/**
	 * Gets the help text for this field.
	 * @return
	 */
	public String getHelpText() {
		return helpHtml;
	}
	
}
//DROP TABLE fields;
//CREATE TABLE fields
//(
//	id integer not null primary key autoincrement,
//	project_id integer not null,
//	title varchar(255) not null,
//	x_coord integer not null,
//	width integer not null,
//	help_html blob,
//	known_data blob,
//	foreign key(project_id) references projects(id)
//)