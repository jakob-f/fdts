package at.ac.tuwien.media;

import at.ac.tuwien.media.io.gesture.ERectangleType;
import at.ac.tuwien.media.util.Value.EDirection;

/**
 * {@link IEthanol} declares the interface for the {@link Ethanol} class.
 *  
 * @author jakob.frohnwieser@gmx.at
 */
public interface IEthanol {
	/**
	 * Skips to the next thumbnail in a given direction with the a variable interval.
	 * 
	 * @param direction the direction to go
	 * @param interval the interval of thumbnails to skip
	 */
	public void skipToThumbnail(final EDirection direction, final int interval);
	
	/**
	 * Skips to the thumbnail with the given number.
	 * 
	 * @param thumbnailNumber the number of the thumbnail to skip to.
	 */
	public void skipToThumbnail(final int thumbnailNumber);
	
	/**
	 * Skips to a thumbnail from a row.
	 * 
	 * @param rectangleRow the row to skip from
	 * @param percent the position of the thumbnail to skip to measured in percent of the total screen width from right
	 */
	public void skipToThumbnailFromRow(final ERectangleType rectangleRow, final int percent);
	
	/**
	 * Shows the slider.
	 */
	public void showSlider();
	
	/**
	 * Fixes or releases the current thumbnail.
	 * If the current thumbnail is fixed it will release it and vice versa.
	 * 
	 * @param if set to <code>true</code> the previous changes will be reseted
	 */
	public void fixOrReleaseCurrentThumbnail(final boolean reset);
	
	/**
	 * Forces the app to restart
	 */
	public void restart();
	
	/**
	 * Shows the current thumbnail in a (big) single view.
	 * 
	 * @param program the program to start
	 */
	public void showCurrentThumbnail();
	
	/**
	 * Deletes all Ethanol files on the system and exits the app
	 */
	public void deleteAllFiles();
	
	/**
	 * Used to call the menu item selected callback only with a id
	 * 
	 * @param itemId the id of the menu to start
	 * @return <code>true</code> if the event was consumed, <code>false</code> otherwise
	 */
	public boolean onOptionsItemSelected(final int itemId);
}
