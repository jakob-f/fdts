package at.ac.tuwien.media.util;

import java.io.File;

public abstract class Util {
	/**
	 * Returns a unique path for a thumbnail folder in the Ethanol root folder based on the path of the image folder
	 * 
	 * @param sPath the path of the image folder
	 * @return a new {@link String} with a new path in the Ethanol root folder
	 * where the slashes of the image folder path have been replaced by dots
	 */
	public static File getPreviewFolderForPath(final String sPath) {
		return new File(Value.ETHANOL_ROOT_FOLDER + Value.RESIZED_IMAGE_FOLDER + _replaceFileSeperators(_removeSDCARDFromFilePath(sPath)));
	}
	
	// replaces all slashes with a dot to flatten the file structure
	private static String _replaceFileSeperators(final String sPath) {
		String sNewPath = sPath;
		if (sPath.startsWith("/")) {
			sNewPath = sPath.substring(1);
		}
		if (sPath.endsWith("/")) {
			sNewPath = sNewPath.substring(0, sNewPath.length() - 1);
		}
		
		return File.separator + sNewPath.replaceAll("/", ".") + File.separator;
	}
	
	// removes the path of the SCARD from the file path
	private static String _removeSDCARDFromFilePath(final String sPath) {
		return sPath.startsWith(Value.SDCARD) ?
				sPath.substring(Value.SDCARD.length() - 1)
				: sPath;
	}
}
