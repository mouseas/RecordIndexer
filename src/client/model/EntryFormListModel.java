package client.model;

import javax.swing.DefaultListModel;

@SuppressWarnings("serial")
public class EntryFormListModel extends DefaultListModel<String> {

	private DataModel dm;
	
	int size;
	
	public EntryFormListModel(DataModel dataModel) {
		dm = dataModel;
		size = 0;
		if (dm != null && dm.getCurrentProject() != null) {
			size = dm.getCurrentProject().getRecordsPerImage();
		}
	}

	@Override
	public String getElementAt(int index) {
		return "" + (index + 1);
	}

	@Override
	public int getSize() {
		// determine the size
		int oldSize = size;
		if (dm == null || dm.getCurrentProject() == null) { 
			size = 0;
		} else {
			size = dm.getCurrentProject().getRecordsPerImage();
		}
		// trigger an update if it has changed.
		if (size != oldSize) {
			fireContentsChanged(this, 0, 0);
		}

		return size;
	}

	public DataModel getDataModel() {
		return dm;
	}

	public void setDataModel(DataModel dataModel) {
		dm = dataModel;
		fireContentsChanged(this, 0, 0); // trigger an update.
	}

}
