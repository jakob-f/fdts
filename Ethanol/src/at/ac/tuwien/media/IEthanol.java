package at.ac.tuwien.media;

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
	 * @param interval the interval of pictures to skip
	 */
	public void skipToThumbnail(EDirection direction, int interval);
	
	/**
	 * Skips to a thumbnail from a row.
	 * 
	 * @param row the row to skip from
	 * @param percent the position of the image to skip to measured in percent of the total screen width from right
	 */
	public void skipToThumbnailFromRow(int row, int percent);
	
	/**
	 * Fixes or releases the current thumbnail.
	 * If the current thumbnail is fixed it will release it and vice versa.
	 */
	public void fixOrReleaseCurrentThumbnail();
	
	/**
	 * Starts an external program from the main activity.
	 * 
	 * @param program the program to start
	 */
	public void startExternalProgram();
}
