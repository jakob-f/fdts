package at.ac.tuwien.media.ethanol.io.file;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.media.ExifInterface;
import at.ac.tuwien.media.ethanol.io.file.bitmap.BitmapIO;
import at.ac.tuwien.media.ethanol.io.file.bitmap.BitmapManipulator;
import at.ac.tuwien.media.ethanol.io.file.model.Dimension;
import at.ac.tuwien.media.ethanol.io.file.model.EThumbnailType;
import at.ac.tuwien.media.ethanol.util.Configuration;
import at.ac.tuwien.media.ethanol.util.EthanolLogger;
import at.ac.tuwien.media.ethanol.util.Util;
import at.ac.tuwien.media.ethanol.util.Value;
import at.ac.tuwien.media.ethanol.util.exception.EthanolException;

/**
 * The {@link ImageIO} class handles the whole reading and writing of images and thumbnails.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class ImageIO {
	private String imageFolder;
	private String previewImageFolder;
	
	public List<File> loadThumbnails() throws EthanolException {
		// video root directory
		imageFolder = Configuration.getAsString(Value.CONFIG_IMAGE_FOLDER) + "/";
		EthanolLogger.addDebugMessage("Loading images from " + imageFolder);
		
		// preview image root directory
		previewImageFolder = Util.getPreviewFolderForPath(imageFolder).getAbsolutePath() + File.separator;
		
		// check if resized thumbnails have been already created (i.e. reset is true)
		// if not create them!
		if (Configuration.getAsBoolean(Value.CONFIG_RESET)) {
			// first delete old preview folders (if there are any)
			FileIO.deleteSubdirectories(new File(previewImageFolder));
			
			// the read all images from the image folder and
			// write all thumbnail files
			readAndResizeImages();
			
			// prevent recreation on next startup
			Configuration.set(Value.CONFIG_RESET, false);
		}
		
		// at this point all (old) thumbnails do exist
		// get the list in the correct order and also check for newer images
		return checkForNewImages();
	}
	
	private List<File> checkForNewImages() throws EthanolException {
		// first get all old images in the right order
		final List<File> imageFiles = ImageOrderListIO.read();
		// get all image files in image root directory
		final File[] imageFilesArray = getAllImageFilesFromDirectory(imageFolder);
		
		// new images do exist
		if (imageFilesArray != null && imageFiles.size() < imageFilesArray.length) {
			// add new images to the end of the list
			for (File imageFile : imageFilesArray) {
				if (!imageFiles.contains(imageFile)) {
					// resize the image and save it
					resizeAndPersistThumbnail(imageFile);
					
					// and add it to the list
					imageFiles.add(imageFile);
				}
			}
			
			// save the new list
			ImageOrderListIO.write(imageFiles);
		}
		
		// return a list with all image files in the correct order
		return imageFiles;
	}
	
	private void readAndResizeImages() throws EthanolException {
		// get all image files in image root directory
		final File[] imageFilesArray = getAllImageFilesFromDirectory(imageFolder);
		final List<File> imageFiles = new ArrayList<File>();
		
		if (imageFilesArray != null) {
			for (int i = 0; i < imageFilesArray.length; i++) {
				// resize the image and save it
				resizeAndPersistThumbnail(imageFilesArray[i]);
				
				// add the save file to image file list
				imageFiles.add(imageFilesArray[i]);
			}
		}
		
		// save the image order list
		ImageOrderListIO.writeSave(imageFiles);
	}
	
	private File[] getAllImageFilesFromDirectory(final String parentDirectory) {
		// search only for images
		final File[] images = new File(parentDirectory).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String filename) {
				// return only directories with a specified name
//FIXME				return file.isFile() && filename.matches(Values.REGEX_IMAGE);
				return filename.matches(Value.REGEX_IMAGE);
			}
		});
		
		return images;
	}
		
	private void resizeAndPersistThumbnail(final File imageFile) throws EthanolException {
		try {
			// read the image
			Bitmap baseBitmap = BitmapIO.read(imageFile);
			
			// rotate the image (if needed)
			if (Configuration.getAsBoolean(Value.CONFIG_ROTATE_IMAGES)) {
				final int imageRotation = new ExifInterface(imageFile.getAbsolutePath()).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
				baseBitmap = BitmapManipulator.rotate(baseBitmap, imageRotation);
			}

			// save all thumbnails - begin with the biggest size A
			EThumbnailType thumbnailType = EThumbnailType.A;
			while (thumbnailType != null) {
				// save a thumbnail with the curent size
				BitmapIO.saveBitmap(manipulateImage(baseBitmap, thumbnailType.getDimension(), false),
						previewImageFolder + thumbnailType.getThumbnailFolder(), imageFile.getName());
				
				// continue with the next smaller thumbnail size
				thumbnailType = thumbnailType.getNextSmallerThumbnailType();
			}
			
			// save all FIAR thumbnails - begin with the biggest size A
			thumbnailType = EThumbnailType.A;
			while (thumbnailType != EThumbnailType.E) {
				// save a thumbnail with the curent size
				BitmapIO.saveBitmap(manipulateImage(baseBitmap, thumbnailType.getDimension(), true),
						previewImageFolder + thumbnailType.getThumbnailFolder(), imageFile.getName());
				
				// continue with the next smaller thumbnail size
				thumbnailType = thumbnailType.getNextSmallerThumbnailType();
			}
		} catch (IOException ioe) {
			throw new EthanolException("Cannot resize and manipulate image", ioe);
		}
	}
	
	private Bitmap manipulateImage(final Bitmap image, final Dimension dimension, final boolean isFIAR) {
		// resize or crop the image to generate the preview thumbnails
		if (Configuration.getAsBoolean(Value.CONFIG_CROP_IMAGES)) {
			return BitmapManipulator.resizeCrop(image, dimension);
		}

		return BitmapManipulator.resize(image, dimension, isFIAR);
	}

	public List<Bitmap> getThumbnailList(final List<File> orderList, final EThumbnailType thumbnailType) {
		final List<Bitmap> images = new ArrayList<Bitmap>();
		
		// get a List with all image bitmaps in the correct order
		for (File imageFile : orderList) {
			images.add(BitmapIO.read(new File(previewImageFolder + thumbnailType.getThumbnailFolder() + File.separator + imageFile.getName())));
		}
		
		return images;
	}
	
	public Bitmap getThumbnail(final String name, final EThumbnailType thumbnailType, final boolean isFIAR) {
		if (isFIAR) {
			// return a FIAR thumbnail with the given name and size
			return getBitmapWithNameFromDirectory(new File(previewImageFolder + thumbnailType.getFiarFolder()), thumbnailType, name);
		}
		
		// return a thumbnail with the given name and size
		return getBitmapWithNameFromDirectory(new File(previewImageFolder + thumbnailType.getThumbnailFolder()), thumbnailType, name);
	}
	
	private Bitmap getBitmapWithNameFromDirectory(final File directory, final EThumbnailType thumbnailType, final String name) {
		// search for the preview image with the given name
		final File[] images = directory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(final File file, final String filename) {
//FIXME				return file.isFile() && filename.equals(name);
				return filename.equals(name);
			}
		});
		
		// if the image was found return it as a bitmap; if not return null
		return (images != null && images.length == 1) ?
				BitmapIO.read(images[0])
				: null;
	}
}