package main;

/**
 * An argument sent in notifications to observers.
 * 
 * @author Justin Forsberg
 *
 */
public class Notification {
	
	private int objectId; // A value used by observers to help identify the observed objects
	private Object message; // A value (should be enum) used by observers to identify what happened to observables
	
	/**
	 * Constructs a notification using the given objectId and message.
	 * @param objectId A value used by observers to help identify observed objects.
	 * @param message A message (typically enum) used to say what changes are being notified.
	 */
	public Notification(int objectId, Object message) {
		
		this.objectId = objectId;
		this.message = message;
	}
	
	/**
	 * Retrieves the stored objectId.
	 * @return The stored objectId.
	 */
	public int getId() {
		
		return this.objectId;
	}
	
	/**
	 * Sets the stored objectId.
	 * @param id The id to be given in this notification.
	 */
	public void setId(int id) {
		
		this.objectId = id;
	}
	
	/**
	 * Retrives the stored message.
	 * @return The stored message.
	 */
	public Object getMessage() {
		
		return this.message;
	}
	
	/**
	 * Sets the stored message.
	 * @param message The stored message.
	 */
	public void setMessage(Object message) {
		
		this.message = message;
	}
}
