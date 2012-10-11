package client.dataAccess;

import java.util.*;
import shared.dataTransfer.*;

public class Field {
	
	public Field(XferField f) {
		
	}
	
	/**
	 * Gets the project this field belongs to
	 * @return
	 */
	public Project getProject() {
		return null;
	}
	
	/**
	 * Gets the x position that this field (and its associated column) is at.
	 * @return
	 */
	public int getXCoord() {
		return 0;
	}
	
	/**
	 * Gets the actual name of the field.
	 * @return
	 */
	public String getTitle() {
		return "";
	}
	
	/**
	 * Gets the width, in pixels, of the field
	 * @return
	 */
	public int getWidth() {
		return 0;
	}
	
	/**
	 * Gets all the possible entries accepted for this field.
	 * @return Non-editable list of acceptable entries for the field.
	 */
	public List<String> getKnownData() {
		return null;
	}
	
	/**
	 * Gets the help text for this field.
	 * @return
	 */
	public String getHelpText() {
		return  "";
	}
	
}