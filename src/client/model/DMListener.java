package client.model;

import java.awt.event.ActionEvent;
import java.util.EventListener;

public interface DMListener extends EventListener{
	public void selectionChanged(ActionEvent e);
}
