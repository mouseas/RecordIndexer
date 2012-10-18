package shared.dataTransfer;

import java.awt.image.*;

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
	
	
}
