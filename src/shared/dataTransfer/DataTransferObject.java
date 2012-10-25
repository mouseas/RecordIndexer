package shared.dataTransfer;

/**
 * All objects from the database have an ID.
 * @author Martin Carney
 *
 */
public abstract class DataTransferObject {
	private int ID;
	
	public int getID() {
		return ID;
	}
	
	protected void setID(int newID) {
		ID = newID;
	}
	
	/**
	 * Converts a DataTransferObject into a string representation, for easy
	 * transmission between server and client. The superclass has no implementation
	 * of this function; each child must implement it themselves.
	 * @return
	 */
	public String serialize() {
		return null;
	}
	
}
