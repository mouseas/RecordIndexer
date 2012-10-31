package shared.dataTransfer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class Batch extends DataTransferObject {
	
	private int projectID;
	private String imageFilename;
	private Image image;
	
	private boolean completed;
	
	public Batch(int id, int projectID, String imageFilename) {
		setID(id);
		this.projectID = projectID;
		this.imageFilename = imageFilename;
		getImage(); // initializes the Image object.
		completed = false;
	}
	
	public Batch (int id, int projectID, String imageFilename, boolean completed) {
		this(id, projectID, imageFilename);
		this.completed = completed;
	}
	
	/**
	 * Gets the image associated with this batch.
	 * @return
	 */
	public Image getImage() {
		if (image == null) {
			image = new Image(getProjectID(), imageFilename);
		}
		
		return image;
	}
	
	/**
	 * Gets the project this batch belongs to
	 * @return
	 */
	public int getProjectID() {
		return projectID;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	public static String serialize(Batch batch) {
		XStream xstream = new XStream(new DomDriver());
		return xstream.toXML(batch);
	}
	
	public static Batch deserialize(String xml) {
		XStream xstream = new XStream(new DomDriver());
		return (Batch)xstream.fromXML(xml);
	}
	
}
//DROP TABLE batches;
//CREATE TABLE batches
//(
//	id integer not null primary key autoincrement,
//	project_id integer not null,
//	filename blob,
//	foreign key(project_id) references projects(id)
//);