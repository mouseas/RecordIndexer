package client.controller;

import java.awt.Image;
import java.net.URL;

import javax.imageio.ImageIO;

import shared.dataTransfer.BatchImage;
import shared.dataTransfer.ImageReference;
import shared.dataTransfer.Project;

import client.serverCommunicator.ServerCommunicator;
import client.views.sample.SampleViewer;

/**
 * Builds, displays, and controls an image sample view.
 * @author Martin Carney
 *
 */
public class SampleController {
	
	SampleViewer view;
	Image image;
	String projectName;

	/**
	 * Constructor which accepts an already-loaded image.
	 * @param image
	 */
	public SampleController(Image image, String projectName) {
		this.image = image;
		this.projectName = projectName;
	}
	
	/**
	 * Constructor which is able to download a sample image given
	 * a server communicator and a project.
	 * @param sc ServerCommunicator
	 * @param p Project
	 */
	public SampleController(ServerCommunicator sc, Project p) {
		ImageReference reference = sc.requestSampleImage(p);
		if (reference == null) { return; }
		String location = reference.getFilename();
		Image image = null;
		if (location != null && location.length() > 0) {
			try {
				URL url = new URL(location);
				image = ImageIO.read(url.openStream());
			} catch (Exception e) {
				System.out.println("SampleController: Error while getting an inputstream from the server.");
				System.out.println(e.getMessage());
			}
		}
		if (image != null) {
			this.image = image;
			projectName = p.getTitle();
		}
	}
	
	/**
	 * Builds and opens the viewer
	 */
	public void buildAndOpenViewer() {
		view = new SampleViewer(image, this, projectName);
		view.setVisible(true);
	}
	
	public void closeViewer() {
		if (view != null) {
			view.dispose();
			view = null;
		}
	}
	
}
