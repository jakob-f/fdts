package at.ac.tuwien.media.util;

import java.io.File;

public abstract class Util {
	public static File getPreviewFolderForPath(final String path) {
		return new File(Value.ETHANOL_ROOT_FOLDER + Value.RESIZED_IMAGE_FOLDER + removeSDCARDFromFilePath(path));
	}
	
	private static String removeSDCARDFromFilePath(final String path) {
		return path.startsWith(Value.SDCARD) ?
			path.substring(Value.SDCARD.length() - 1)
			 : path;
	}
}
