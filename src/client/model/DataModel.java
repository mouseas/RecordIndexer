package client.model;

import shared.dataTransfer.*;

import java.awt.Image;
import java.util.*;

/**
 * Core client data model class; holds data but does no operations itself.
 * @author Martin
 *
 */
public class DataModel {

	private List<Project> projects;
	private Project currentProject;
	private Batch currentBatch;
	@Deprecated // use currentBatch.getRecords()
	private Record[][] currentRecordGrid;
	@Deprecated // Use ServerCommunicator.getCurrentUser().
	private User currentUser;
	private Image currentBatchImage;
	
	/**
	 * Default constructor. Everything is null.
	 */
	public DataModel() {
		
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public Batch getCurrentBatch() {
		return currentBatch;
	}

	public void setCurrentBatch(Batch currentBatch) {
		this.currentBatch = currentBatch;
	}

	@Deprecated
	public Record[][] getCurrentRecordGrid() {
		return currentRecordGrid;
	}

	@Deprecated
	public void setCurrentRecordGrid(Record[][] currentRecordGrid) {
		this.currentRecordGrid = currentRecordGrid;
	}

	@Deprecated
	public User getCurrentUser() {
		return currentUser;
	}

	@Deprecated
	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public Image getCurrentBatchImage() {
		return currentBatchImage;
	}

	public void setCurrentBatchImage(Image currentBatchImage) {
		this.currentBatchImage = currentBatchImage;
	}

	public Project getCurrentProject() {
		return currentProject;
	}

	public void setCurrentProject(Project currentProject) {
		this.currentProject = currentProject;
	}
	
}
