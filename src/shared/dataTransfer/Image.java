package shared.dataTransfer;

import java.awt.image.*;

public class Image {
	
	private String filename;
	private Project project;
	
	public Image(Project p, String filename) {
		this.project = p;
		this.filename = filename;
	}
	
	public Project getProject() {
		return null;
	}
	
	public RenderedImage getImage() {
		return null;
	}
	
	public String getFilename() {
		return filename;
	}
	
	
}
