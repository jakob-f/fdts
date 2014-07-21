package at.ac.tuwien.media.methanol.util;

import java.io.File;
import java.util.Locale;

/**
 * The {@link Util} class provides some central methods for the Methanol App.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public abstract class Util {
	/**
	 * Returns a unique path for a thumbnail folder in the Methanol root folder based on the path of the image folder
	 * 
	 * @param path the path of the image folder
	 * @return a new {@link String} with a new path in the Methanol root folder
	 * where the slashes of the image folder path have been replaced by dots
	 */
	public static File getPreviewFolderForPath(final String path) {
		return new File(Value.METHANOL_ROOT_FOLDER + Value.RESIZED_IMAGE_FOLDER + _replaceFileSeperators(_removeSDCARDFromFilePath(path)));
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
	
	public static String toThumbnailName(final String name) {
		return Util.replaceLast(name, Value.JPEG, Value.THUMBNAIL);
	}
	
	/**
	 * Replaces the last string sequence in the given string
	 * 
	 * @param input the original {@link String}
	 * @param oldString the {@link String} to replace
	 * @param newString the replacement {@link String}
	 * @return the string with the replaced string
	 */
	public static String replaceLast(final String input, final String oldString, final String newString) {
		final String inputLC = input.toLowerCase(Locale.ENGLISH);
		
		if (!inputLC.contains(oldString)) {
			return input;
		}

		final StringBuilder sb = new StringBuilder(input);
		final int index = inputLC.lastIndexOf(oldString);
		sb.replace(index, index + oldString.length(), newString);
		
		return sb.toString();
	}
}
