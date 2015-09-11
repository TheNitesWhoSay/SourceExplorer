package main;

import java.awt.Component;
import java.awt.Image;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A program for displaying the source code of
 * requested web pages, in a tabbed browser.
 * 
 * @author Justin Forsberg
 *
 */
public class SourceExplorer extends JFrame implements Observer {

	private static final long serialVersionUID = 7106353970340455261L; // Prevents compiler warnings
	
	private List<WebPage> openTabs; // A list of tab windows currently open in the browser
	private JTabbedPane tabs; // The JTabbedPane that holds the GUI portion of open tabs
	
	/**
	 * Attempts to start the SourceExplorer program.
	 */
	public SourceExplorer() {
		
		super("Source Explorer");
		
		try {
			
			setBounds(100,100,300,200);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream input = classLoader.getResourceAsStream("Resources/SourceExplorerIcon.png");
			Image logo = ImageIO.read(input);
			this.setIconImage(new ImageIcon(logo).getImage());
			
			openTabs = new ArrayList<WebPage>();
			tabs = new JTabbedPane(JTabbedPane.TOP);
			if ( openTab("New Tab") && openTab("") )
			{
				tabs.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						tabSelChanged();
					}
				});
				
				getContentPane().add(tabs);
				setVisible(true); // Program is entirely event-driven at this point
			}
			else
				alertFatalInit(); // Program exits shortly
		}
		catch ( Exception e ) {
			
			e.printStackTrace();
			alertFatalInit(); // Program exits shortly
		}
	}
	
	/**
	 * Alerts to the user that an error occured during
	 * initialization so the program couldn't be opened.
	 */
	public static void alertFatalInit() {
		
		JOptionPane.showMessageDialog(null,
				"Fatal initialization error, " +
				"cannot start Source Explorer. " +
				"The JVM is most likely out of memory."
		);
	}
	
	/**
	 * Attempts to open a tab within SourceExplorer
	 * displaying the given title and using the given
	 * web address.
	 * @param title The title to be displayed for this tab.
	 * @param address The address to be associated with this tab.
	 * @return Whether the tab was opened successfully.
	 */
	public boolean openTab(String title, String address) {
		
		WebPage tab = WebPage.CreateWebPage(title, address);
		if ( tab != null && openTabs.add(tab) )
		{
			Component tabContents = tab.getPageContents();
			if ( tabContents != null && tabs.add(title, tabContents) != null )
			{
				tab.setId(tabs.getTabCount()-1);
				tab.addObserver(this);
				return true;
			}
			else
				openTabs.remove(openTabs.size()-1);
		}
		return false;
	}
	
	/**
	 * Attempts to open a tab within SourceExplorer
	 * displaying the given title and with an initially
	 * blank web address.
	 * @param title The title to be dispalyed for this tab.
	 * @return Whether the tab was opened successfully.
	 */
	public boolean openTab(String title) {
		
		return openTab(title, "");
	}
	
	/**
	 * Attempts to change the text on the label of the tab.
	 * @param index The index of the tab whose label is to be changed.
	 * @param title The new value to be displayed on the tab label.
	 * @return Whether the label was successfully changed.
	 */
	public boolean modifyTabLabel(int index, String title) {
		
		if ( index >= 0 && index < tabs.getTabCount() )
		{	
			tabs.setTitleAt(index,  title);
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Attempts to set the title of WebPage at the given tab index.
	 * @param index The index of the tab whose title is to be set.
	 * @param title The title to be dispalyed for the tab at the
	 * given index.
	 * @return Whether the tab was renamed successfully.
	 */
	public boolean setTabTitle(int index, String title) {
		
		WebPage tab = openTabs.get(index);
		if ( tab != null )
		{
			tab.setTitle(title);
			return true;
		}
		return false;
	}
	
	/**
	 * Attempts to give the title "New Tab" to the current, blank
	 * trailing tab and make a new blank trailing tab after it.
	 * @return Whether both operations completed successfully.
	 */
	public boolean createNewTab() {
		
		return openTab("") &&
			   setTabTitle(tabs.getTabCount()-2, "New Tab");
	}
	
	/**
	 * Fires when the ChangeListener for tabs senses a change.
	 */
	public void tabSelChanged() {
		
		int lastIndex = tabs.getTabCount()-1;
		if ( lastIndex >= 0 && tabs.getSelectedIndex() == lastIndex )
		{
			// The blank, trailing tab was selected
			createNewTab();
		}
	}
	
	/**
	 * Attempts to close and cleanup a the tab/WebPage at the given index.
	 * @param tabId The index of a tab that should be closed.
	 */
	public void closeTab(int tabId) {
		
		assert( tabs.getTabCount() == openTabs.size() );
		
		int numCurrTabs = openTabs.size();
		if ( tabId >= 0 && tabId < openTabs.size() )
		{
			// Decrement the ids of all tabs that come after
			for ( int i=tabId+1; i<numCurrTabs; i++ )
			{
				WebPage page = openTabs.get(i);
				if ( page != null )
					page.setId(i-1);
			}
			
			openTabs.remove(tabId);
			if ( tabId == tabs.getTabCount()-2 )
				tabs.setSelectedIndex(tabId-1);
			
			tabs.remove(tabId);
		}
	}
	
	/**
	 * Program entry point, attempts to construct SourceExplorer.
	 * @param args No argument are currently in use.
	 */
	public static void main(String[] args) {
		
		try {
			
			new SourceExplorer();
		}
		catch ( OutOfMemoryError e ) {
			
			e.printStackTrace();
			SourceExplorer.alertFatalInit();
		}
	}

	
	/**
	 * Fires when WebPages notify a there has been a change to
	 * the title change or the closing of the page.
	 */
	public void update(Observable obs, Object argument) {
		
		if ( obs.getClass().getName().equals(WebPage.class.getName()) ) // Notification from WebPage
		{
			WebPage page = (WebPage)obs;
			Notification arg = (Notification)argument;
			WebPage.WpMessage msg = (WebPage.WpMessage)arg.getMessage();
			switch ( msg ) {
			
				case WP_TITLE_CHANGE:
					modifyTabLabel(arg.getId(), page.getTitle());
					break;
				case WP_CLOSE:
					closeTab(arg.getId());
					break;
				default:
					System.out.println("Unrecognized WebPage message.");
					break;
			}
		}
		else
			System.out.println("Unrecognized notification class.");
	}
}
