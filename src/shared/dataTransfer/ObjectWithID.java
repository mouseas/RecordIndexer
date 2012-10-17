package shared.dataTransfer;

/**
 * All objects from the database have an ID.
 * @author Martin Carney
 *
 */
public abstract class ObjectWithID {
	private int ID;
	
	public int getID() {
		return ID;
	}
	
	protected void setID(int newID) {
		ID = newID;
	}
}
