package client.model;

import shared.dataTransfer.*;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
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
	
	private Dimension selectedCell;
	private List<DMListener> listeners;
	
	/**
	 * Default constructor. Everything is null.
	 */
	public DataModel() {
		selectedCell = new Dimension(0, 0);
		listeners = new ArrayList<DMListener>();
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
	
	/**
	 * Sets the currently selected column and row.
	 * @param col
	 * @param row
	 */
	public void select(int col, int row) {
		selectedCell.setSize(col, row);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).selectionChanged(new ActionEvent(this, i, "n/a"));
		}
	}
	
	public void selectRow(int row) {
		select(selectedCell.width, row);
	}
	
	public void selectColumn(int col) {
		select(col, selectedCell.height);
	}
	
	public void addSelectionChangeListener(DMListener listener) {
		listeners.add(listener);
	}
	
	public void removeSelectionChangeListener(DMListener listener) {
		listeners.remove(listener);
	}

	public int getRowSelected() {
		return selectedCell.height;
	}
	
	public int getColSelected() {
		return selectedCell.width;
	}
	
	
}
