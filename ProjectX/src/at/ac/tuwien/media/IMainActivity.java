package at.ac.tuwien.media;

/**
 * {@link IMainActivity} declares the interface for the {@link MainActivity} class.
 *  
 * @author jakob.frohnwieser@gmx.at
 */
public interface IMainActivity {
	
	/**
	 * Deletes all files on the system and exits the app
	 */
	public void deleteAllFiles();
	
	/**
	 * Used to call the menu item selected callback only with a id
	 * 
	 * @param itemId the id of the menu to start
	 * @return <code>true</code> if the event was consumed, <code>false</code> otherwise
	 */
	public boolean onOptionsItemSelected(final int itemId);
	
	/**
	 * Forces the app to restart
	 */
	public void restart();
}
