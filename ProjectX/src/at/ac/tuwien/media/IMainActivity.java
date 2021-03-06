package at.ac.tuwien.media;

import at.ac.tuwien.media.util.Value;

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
	 * Deletes a thumbnail at the given position
	 * 
	 * @param listIndex the index of the list to delete from
	 * @param thumbnailPosition the position of the thumbnail to delete
	 */
	public void delete(final int listIndex, final Value.EThumbnailPostion thumbnailPosition);
	
	/**
	 * Inserts a thumbnail at the current position to the list at the given {@link Value.EInsertListPosition}
	 * 
	 * @param fromListIndex the index of the list to insert from
	 * @param fromListThumbnailPosition the position of the thumbnail to insert
	 * @param insertListPosition the position of the list to insert to
	 */
	public void insert(final int fromListIndex, final Value.EThumbnailPostion fromListThumbnailPosition, final Value.EInsertListPosition insertListPosition);
	
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
