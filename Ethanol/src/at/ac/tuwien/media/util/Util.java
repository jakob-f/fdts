package at.ac.tuwien.media.util;

import java.io.File;

public abstract class Util {
	/**
	 * Returns a unique path for a thumbnail folder in the Ethanol root folder based on the path of the image folder
	 * 
	 * @param path the path of the image folder
	 * @return a new {@link String} with a new path in the Ethanol root folder
	 * where the slashes of the image folder path have been replaced by dots
	 */
	public static File getPreviewFolderForPath(final String path) {
		return new File(Value.ETHANOL_ROOT_FOLDER + Value.RESIZED_IMAGE_FOLDER + _replaceFileSeperators(_removeSDCARDFromFilePath(path)));
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
