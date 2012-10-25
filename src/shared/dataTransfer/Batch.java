package shared.dataTransfer;

public class Batch extends DataTransferObject {
	
	private int projectID;
	private String imageFilename;
	private Image image;
	
	public Batch(int id, int projectID, String imageFilename) {
		setID(id);
		this.projectID = projectID;
		this.imageFilename = imageFilename;
		getImage(); // initializes the Image object.
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
	
	
}
//DROP TABLE batches;
//CREATE TABLE batches
//(
//	id integer not null primary key autoincrement,
//	project_id integer not null,
//	filename blob,
//	foreign key(project_id) references projects(id)
//);