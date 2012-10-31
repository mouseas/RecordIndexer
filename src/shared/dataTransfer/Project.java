package shared.dataTransfer;

import java.io.*;
import com.thoughtworks.xstream.*;
import com.thoughtworks.xstream.io.xml.DomDriver;

@SuppressWarnings("serial")
public class Project extends DataTransferObject implements Serializable {
	
	private String title;
	private int firstYCoord;
	private int fieldHeight;
	private int numRows;
	
	public Project(int id, int firstYCoord, int fieldHeight, int numRows, String title) {
		setID(id);
		this.title = title;
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
	
//	/**
//	 * Gets the width, in pixels, of a whole row, which should be the sum
//	 * of the widths of all of the fields in this project.
//	 * @return
//	 */
//	public int getWidth() {
//		if (fields != null) {
//			int result = 0;
//			for (int i = 0; i < fields.size(); i++) {
//				result += fields.get(i).getWidth();
//			}
//			return result;
//		} else {
//			return -1;
//		}
//	}
	
	/**
	 * Gets the name of the project
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	
//	/**
//	 * Assigns a list of Fields to an existing project.
//	 * @param fields
//	 */
//	public void setFields (List<Field> fields) {
//		this.fields = fields;
//	}
	
	public static String serialize(Project project) {
		XStream xstream = new XStream(new DomDriver());
		return xstream.toXML(project);
	}
	
	public static Project deserialize(String xml) {
		XStream xstream = new XStream(new DomDriver());
		return (Project)xstream.fromXML(xml);
	}
}