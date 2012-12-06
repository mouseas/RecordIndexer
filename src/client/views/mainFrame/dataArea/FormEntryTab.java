package client.views.mainFrame.dataArea;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ListDataListener;

import client.model.DataModel;
import client.model.EntryFormListModel;

@SuppressWarnings("serial")
public class FormEntryTab extends JPanel {
	
	private List<FormElement> rowElements;
	private JPanel elementsHolder;
	private JList<String> rowList;
	private JScrollPane listScrollPane;
	private EntryFormListModel rowNumbersModel;
	
	private DataModel dm;
	
	public FormEntryTab() {
		rowNumbersModel = new EntryFormListModel(dm);
		rowList = new JList<String>(rowNumbersModel);
		rowList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rowList.setLayoutOrientation(JList.VERTICAL);
		
		listScrollPane = new JScrollPane(rowList);
		listScrollPane.setPreferredSize(new Dimension(50, 300));
		
		rowElements = new ArrayList<FormElement>();
		elementsHolder = new JPanel();
		elementsHolder.setLayout(new BoxLayout(elementsHolder, BoxLayout.Y_AXIS));
		setLayout(new BorderLayout());
		
		add(elementsHolder, BorderLayout.EAST);
		add(listScrollPane, BorderLayout.WEST);
	}
	
	public void setDataModel(DataModel dataModel) {
		dm = dataModel;
		rowNumbersModel.setDataModel(dm);
	}
	
	
}
