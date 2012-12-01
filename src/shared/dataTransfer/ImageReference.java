package shared.dataTransfer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * The image from a batch. Currently only holds the file name, but in the finished
 * app this will be able to download and return the actual image data. Maybe.
 * @author Martin
 *
 */
public class ImageReference {
	
	private String filename;
	private int projectID;
	
	public ImageReference(int projectID, String filename) {
		this.projectID = projectID;
		this.filename = filename;
	}
	
	public int getProjectID() {
		return projectID;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public static String serialize(ImageReference image) {
		XStream xstream = new XStream(new DomDriver());
		return xstream.toXML(image);
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	
}
