package shared.dataTransfer;

import java.awt.image.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class Image {
	
	private String filename;
	private int projectID;
	
	public Image(int projectID, String filename) {
		this.projectID = projectID;
		this.filename = filename;
	}
	
	public int getProjectID() {
		return projectID;
	}
	
	public RenderedImage getImage() {
		return null;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public static String serialize(Image image) {
		XStream xstream = new XStream(new DomDriver());
		return xstream.toXML(image);
	}
	
	
}
