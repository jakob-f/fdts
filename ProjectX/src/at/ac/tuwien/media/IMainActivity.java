package at.ac.tuwien.media;

import at.ac.tuwien.media.util.Value;
import at.ac.tuwien.media.util.Value.EThumbnailPostion;

/**
 * {@link IMainActivity} declares the interface for the {@link MainActivity} class.
 *  
 * @author jakob.frohnwieser@gmx.at
 */
public interface IMainActivity {
	
	/**
	 * Aborts the insertion of a thumbnail
	 * 
	 * @param thumbnailPosition the position of the thumbnail to insert
	 */
	public void abortInsert(EThumbnailPostion thumbnailPosition);
	
	/**
	 * Deletes all files on the system and exits the app
	 */
	public void deleteAllFiles();
	
	/**
	 * Deletes a thumbnail at the given position
	 * 
	 * @param thumbnailPosition the position of the thumbnail to delete
	 */
	public void delete(final Value.EThumbnailPostion thumbnailPosition);
	
	/**
	 * Inserts a thumbnail at the current position to the list beneath
	 * 
	 * @param thumbnailPosition the position of the thumbnail to insert
	 */
	public void insert(final Value.EThumbnailPostion thumbnailPosition);
	
	/**
	 * Used to call the menu item selected callback only with a id
	 * 
	 * @param itemId the id of the menu to start
	 * @return <code>true</code> if the event was consumed, <code>false</code> otherwise
	 */
	public boolean onOptionsItemSelected(final int itemId);
	
	/**
	 * Prepares a thumbnail to be inserted at the current position to the list beneath
	 * 
	 * @param thumbnailPosition the position of the thumbnail to insert
	 */
	public void prepareInsert(final Value.EThumbnailPostion thumbnailPosition);
	
	/**
	 * Forces the app to restart
	 */
	public void restart();
}
