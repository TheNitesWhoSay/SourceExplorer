package main;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

/**
 * A class for displaying objects on the web.
 * WebPages contain an address and the title of the page
 * and the code necessary for displaying the page in GUIs.
 * 
 * @author Justin Forsberg
 * 
 */
public class WebPage extends Observable {

	/**
	 * Messages sent to observers of this WebPage.
	 */
	public enum WpMessage { // Wp/WP for WebPage 
		
		/** The WebPage has changed its stored title. */
		WP_TITLE_CHANGE,
		
		/** The WebPage has requested it be closed. */
		WP_CLOSE;
	}
	
	private int id; // An identifier assigned to this page by an observer
	private WebAddress address; // The web address for this page
	private String title; // The title to be displayed for this WebPage
	
	private JTextField addressText; // Web address input area
	private JTextArea sourceText; // Source text output area
	
	/**
	 * Constructs a WebPage with the given title and address.
	 * @param title The title to be displayed for this WebPage.
	 * @param address The address to be associated with this WebPage.
	 */
	private WebPage(String title, String address) throws OutOfMemoryError {
		
		this.id = 0;
		this.addressText = null;
		this.sourceText = null;
		this.address = new WebAddress(address);
	}
	
	/**
	 * Constructs a WebPage with the given title.
	 * @param title The title to be displayed for this WebPage.
	 */
	private WebPage(String title) throws OutOfMemoryError {
		
		this.id = 0;
		this.title = title;
		this.addressText = null;
		this.sourceText = null;
		this.address = new WebAddress();
	}
	
	/**
	 * Creates and returns a WebPage with the given title and address,
	 * in a safe manner.
	 * @param title The title to be displayed for this WebPage.
	 * @param address The address to be associated with this WebPage.
	 * @return A WebPage with the given title and address on success,
	 * null otherwise.
	 */
	public static WebPage CreateWebPage(String title, String address) {
		
		try {
			
			WebPage page = new WebPage(title, address);
			return page;
		}
		catch ( OutOfMemoryError e ) {
			
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Creates and returns a WebPage with the given title and an
	 * initially blank address, in a safe manner.
	 * @param title The title to be displayed for this WebPage.
	 * @return A WebPage with the given title and address on success,
	 * null otherwise.
	 */
	public static WebPage CreateWebPage(String title) {
		
		try {
			
			WebPage page = new WebPage(title);
			return page;
		}
		catch ( OutOfMemoryError e ) {
			
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Sets by observers to help identify this specific WebPage
	 * @param id An id, probably an integer, send as the argument
	 * in notify messages.
	 */
	public void setId(int id) {
		
		this.id = id;
	}
	
	/**
	 * Returns the title that should be displayed for this WebPage.
	 * @return The title that should be displayed for this WebPage.
	 */
	public String getTitle() {
		
		return title;
	}
	
	/**
	 * Returns the address associated with this WebPage.
	 * @return The address associated with this WebPage.
	 */
	public String getAddress() {
		
		return this.address.getAddress();
	}
	
	/**
	 * Sets the title that should be displayed for this WebPage.
	 * @param title The title that should be displayed for this WebPage.
	 */
	public void setTitle(String title) {
		
		this.title = title;
		setChanged();
		notifyObservers(new Notification(this.id, WpMessage.WP_TITLE_CHANGE));
	}
	
	/**
	 * Fires when the go button is pressed.
	 */
	public void ButtonGo() {
		
		if ( address.setAddress(addressText.getText()) )
		{
			String source = Downloader.GetPageSource(address.getUrl());
			if ( source != null && !source.equals("") )
			{
				sourceText.setText(source);
				this.setTitle(address.getAddress());
			}
			else
				JOptionPane.showMessageDialog(null, "Failed to load item at address:\n" + address.getAddress());
		}
		else
			JOptionPane.showMessageDialog(null, "Invalid URL!");
	}
	
	/**
	 * Fires when the close button is pressed.
	 */
	public void ButtonClose() {
		
		setChanged();
		notifyObservers(new Notification(this.id, WpMessage.WP_CLOSE));
	}
	
	/**
	 * Gets the entire contents of a GUI for this WebPage and returns
	 * it as an object that can be added to various containers.
	 * @return A component containing a GUI for this WebPage.
	 */
	public Component getPageContents() {
		
		try {
			
			JPanel panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			
			c.gridx   = 0  ; c.gridy   = 0  ;
			c.weightx = 1.0; c.weighty = 0.0;
			panel.add(getHeaderContents(), c);
			
			sourceText = new JTextArea("");
			sourceText.setLineWrap(false);
			JScrollPane scroll = new JScrollPane(sourceText);
			scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			c.gridx   = 0  ; c.gridy   = 1  ;
			c.weightx = 1.0; c.weighty = 1.0;
			panel.add(scroll, c);
			
			return panel;
		}
		catch ( Exception e ) {
			
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * Helper for GetPageContents; retrieves the contents for the
	 * header portion of the Page including the address bar,
	 * go button, and close button.
	 * @return A component containing a GUI for this WebPage's header.
	 */
	private Component getHeaderContents() {
		
		try {
			
			JPanel headerBar = new JPanel();
			headerBar.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			
			this.addressText = new JTextField("");
			addressText.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent arg) {
					if ( arg.getKeyCode() == KeyEvent.VK_ENTER )
						ButtonGo();
				}
				public void keyReleased(KeyEvent arg) { }
				public void keyTyped(KeyEvent arg) { }
			});
			c.gridx   = 0  ; c.gridy   = 0  ;
			c.weightx = 1.0; c.weighty = 1.0;
			headerBar.add(addressText, c);
			
			JButton go = new JButton("Go");
			go.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ButtonGo();
				}
			});
			go.setSize(5, 5);
			c.gridx   = 1  ; c.gridy   = 0  ;
			c.weightx = 0.0; c.weighty = 0.0;
			c.anchor = GridBagConstraints.WEST;
			headerBar.add(go, c);
			
			JButton closePage = new JButton("x");
			closePage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ButtonClose();
				}
			});
			closePage.setSize(5, 5);
			c.gridx   = 2  ; c.gridy   = 0  ;
			c.weightx = 0.0; c.weighty = 0.0;
			c.anchor = GridBagConstraints.NORTHEAST;
			headerBar.add(closePage, c);
			
			return headerBar;
		}
		catch ( Exception e ) {
			
			e.printStackTrace();
			return null;
		}
	}

}
