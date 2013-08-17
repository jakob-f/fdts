package at.ac.tuwien.media.io.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.media.util.Configuration;
import at.ac.tuwien.media.util.Util;
import at.ac.tuwien.media.util.Value;
import at.ac.tuwien.media.util.exception.EthanolException;

/**
 * The {@link ImageOrderListIO} class handles the whole reading and writing of the image order list
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class ImageOrderListIO {
	
	/**
	 * Reads the image order list from the current preview folder.
	 * 
	 * @return the content of the list as a {@link List} of {@link File} or null if no list was found
	 * @throws EthanolException thrown if the order list cannot be read
	 */
	public static List<File> read() throws EthanolException {
		// get the preview image root folder
		final String previewImageFolder = Util.getPreviewFolderForPath(Configuration.getAsString(Value.CONFIG_IMAGE_FOLDER)).getAbsolutePath() + File.separator;
		// get the image order list file
		final File imageOrderList = new File(previewImageFolder + Value.IMAGE_ORDER_LIST_FILENAME);
		
		if (imageOrderList.exists()) {
			// read the order list
			final String filePaths = FileIO.read(imageOrderList);
			final List<File> imageFiles = new ArrayList<File>();

			// get all file paths
			for (String filePath : filePaths.split("\n")) {
				// get the image file and check if it exists
				final File imageFile = new File(filePath);
				if (imageFile.exists()) {
					// add it to the file list
					imageFiles.add(imageFile);
				}
			}
			
			return imageFiles;
		}
		
		return null;
	}
	
	/**
	 * Writes the image order list in the current preview folder. This will always override the old list without asking!
	 * 
	 * @param imageFiles the {@link List} with the images {@link File} in the right order
	 * @throws EthanolException thrown if the order list cannot be written
	 */
	public static void write(final List<File> imageFiles) throws EthanolException {
		// get the preview image root folder
		final String previewImageFolder = Util.getPreviewFolderForPath(Configuration.getAsString(Value.CONFIG_IMAGE_FOLDER)).getAbsolutePath() + File.separator;
				
		String orderList = "";
		
		// get all file paths as Strings
		for (File thumbnailFile : imageFiles) {
			orderList += thumbnailFile.getAbsolutePath() + "\n";
		}
		
		// write the file to the system
		FileIO.write(new File(previewImageFolder + Value.IMAGE_ORDER_LIST_FILENAME), orderList.getBytes());
	}
	
	/**
	 * Writes the image order list in the current preview folder. This will not override the old list.
	 * 
	 * @param imageFiles the {@link List} with the images {@link File} in the right order
	 * @throws EthanolException thrown if the order list cannot be written
	 */
	public static void writeSave(final List<File> imageFiles) throws EthanolException {
		// get the preview image root folder
		final String previewImageFolder = Util.getPreviewFolderForPath(Configuration.getAsString(Value.CONFIG_IMAGE_FOLDER)).getAbsolutePath() + File.separator;
		
		// check if an old list already exists
		if (!new File(previewImageFolder + Value.IMAGE_ORDER_LIST_FILENAME).exists()) {
			write(imageFiles);
		}
		
		// else do nothing
	}
}
