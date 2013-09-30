package at.ac.tuwien.media.util;

import java.io.File;

import android.graphics.Point;

/**
 * The {@link Util} class provides some central methods.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public abstract class Util {
	private static Point displayMetrics;
	
	/**
	 * Sets the display metrics as a {@link Point}
	 * 
	 * @param displayMetrics the display metrics for this device
	 */
	public static void setDisplayMetrics(final Point displayMetrics) {
		Util.displayMetrics = displayMetrics;
	}
	
	/**
	 * Gets the display metrics as a {@link Point}
	 * 
	 * @return the display metrics for this device
	 */
	public static Point getDisplayMetrics() {
		if (Util.displayMetrics != null) {
			return Util.displayMetrics;
		}
		
		throw new RuntimeException("display metrics not set.");
	}
	
	/**
	 * Returns a unique path for a thumbnail folder in the root folder based on the path of the image folder
	 * 
	 * @param path the path of the image folder
	 * @return a new {@link String} with a new path in the root folder
	 * where the slashes of the image folder path have been replaced by dots
	 */
	public static File getPreviewFolderForPath(final String path) {
		return new File(Value.ROOT_FOLDER + Value.RESIZED_IMAGE_FOLDER + _replaceFileSeperators(_removeSDCARDFromFilePath(path)));
	}
	
	// replaces all slashes with a dot to flatten the file structure
	private static String _replaceFileSeperators(final String path) {
		String newPath = path;
		if (path.startsWith("/")) {
			newPath = path.substring(1);
		}
		if (path.endsWith("/")) {
			newPath = newPath.substring(0, newPath.length() - 1);
		}
		
		return File.separator + newPath.replaceAll("/", ".") + File.separator;
	}
	
	// removes the path of the SCARD from the file path
	private static String _removeSDCARDFromFilePath(final String path) {
		return path.startsWith(Value.SDCARD) ?
				path.substring(Value.SDCARD.length() - 1)
				: path;
	}
}
