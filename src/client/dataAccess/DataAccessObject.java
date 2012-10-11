package client.dataAccess;

/**
 * All objects from the database have an ID.
 * @author Martin Carney
 *
 */
public abstract class DataAccessObject {
	private int ID;
	
	public int getID() {
		return ID;
	}
	
	protected void setID(int newID) {
		ID = newID;
	}
}
