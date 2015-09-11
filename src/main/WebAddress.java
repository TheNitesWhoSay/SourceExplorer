package main;

import java.net.URL;

/**
 * A simple object storing the address of an item on the web.
 * The class provide some methods for retrieving the address
 * in different formats.
 * 
 * @author Justin Forsberg
 * 
 */
public class WebAddress {

	private String address; // The web address as given by client code
	
	/**
	 * Constructs a WebAddress with the given string.
	 * @param address The address, as given by the user.
	 */
	public WebAddress(String address) {
		
		this.address = address;
	}
	
	/**
	 * Constructs a WebAddress with a blank string.
	 */
	public WebAddress() {
		
		this.address = "";
	}

	/**
	 * Returns the stored address.
	 * @return The address associated with this object.
	 */
	public String getAddress() {
		
		return this.address;
	}
	
	/**
	 * Attempts to return a URL based on the stored address.
	 * @return The URL if successfully constructed, null otherwise.
	 */
	public URL getUrl() {
		
		if ( address == null || address.equals("") )
			return null;
		
		URL url = attemptUrl(address);
		if ( url == null && !address.startsWith("http://") && !address.startsWith("https://") )
			url = attemptUrl("http://" + address);
		
		return url;
	}
	
	/**
	 * Attempts to set the stored address.
	 * @param address The new address to be stored.
	 * @return Whether the newly stored address is a valid url.
	 */
	public boolean setAddress(String address) {
		
		this.address = address;
		return getUrl() != null;
	}

	/**
	 * Tries to form a URL from a string, in a safe manner.
	 * @param addr The address to try to make into a URL.
	 * @return the URL if successful, null otherwise.
	 */
	private URL attemptUrl(String addr) {
		
		try {
			URL url = new URL(addr);
			return url;
		}
		catch ( Exception e ) { // Malformed URL, out of memory
			
			return null;
		}
	}
}
