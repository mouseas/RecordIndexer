package shared.dataTransfer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Batch object, holds information about one image in need of indexing.
 * @author Martin
 *
 */
public class BatchImage extends DataTransferObject {
	
	private int projectID;
	private String imageFilename;
	private ImageReference image;
	
	private boolean completed;
	private String username;
	
	public BatchImage(int id, int projectID, String imageFilename, String username) {
		setID(id);
		this.projectID = projectID;
		this.imageFilename = imageFilename;
		this.username = username;
		getImage(); // initializes the Image object.
		completed = false;
	}
	
	/**
	 * Additional constructor that allows the batch to be created as completed = true.
	 * @param id
	 * @param projectID
	 * @param imageFilename
	 * @param username
	 * @param completed
	 */
	public BatchImage (int id, int projectID, String imageFilename, String username, boolean completed) {
		this(id, projectID, imageFilename, username);
		this.completed = completed;
	}
	
	/**
	 * Gets the image associated with this batch.
	 * Deprecated: use getImageLoc() instead.
	 * @return
	 */
	@Deprecated
	public ImageReference getImage() {
		if (image == null) {
			image = new ImageReference(getProjectID(), imageFilename);
		}
		
		return image;
	}
	
	/**
	 * Gets the location of the image for this batch.
	 * @return
	 */
	public String getImageLoc() {
		return imageFilename;
	}
	
	/**
	 * Sets the location of the image for this batch. Avoid use.
	 * @param loc
	 */
	public void setImageLoc(String loc) {
		imageFilename = loc;
	}
	
	/**
	 * Gets the project this batch belongs to
	 * @return
	 */
	public int getProjectID() {
		return projectID;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	public static String serialize(BatchImage batch) {
		XStream xstream = new XStream(new DomDriver());
		return xstream.toXML(batch);
	}
	public static BatchImage deserialize(String xml) {
		XStream xstream = new XStream(new DomDriver());
		return (BatchImage)xstream.fromXML(xml);
	}
	
}