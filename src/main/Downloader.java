package main;

import java.net.URL;
import java.util.Scanner;

/**
 * A class that downloads or otherwise fetches
 * information about the given addresses.
 * 
 * @author Justin Forsberg
 * 
 */
public class Downloader {

	/**
	 * Attempts to return the source code for the given address.
	 * @param address The web address to get the source from.
	 * @return The source for the web address on success,
	 * null on faliure.
	 */
	public static String GetPageSource(URL address)
	{
		StringBuilder source = null;
		Scanner scanner = null;
		try {
			
			scanner = new Scanner(address.openStream(), "utf-8");
			source = new StringBuilder();
			
			while ( scanner.hasNextLine() )
			{
				source.append(scanner.nextLine());
				source.append('\n');
			}
			
			scanner.close();
			return source.toString();
		}
		catch ( Exception e ) {
			
			e.printStackTrace();
			if ( scanner != null )
				scanner.close();
			
			return null;
		}
	}
}
