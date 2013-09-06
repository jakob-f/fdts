package at.ac.tuwien.media.io.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import at.ac.tuwien.media.io.file.bitmap.BitmapManipulator;
import at.ac.tuwien.media.util.Configuration;
import at.ac.tuwien.media.util.Util;
import at.ac.tuwien.media.util.Value;
import at.ac.tuwien.media.util.exception.XException;

/**
 * The {@link ImageIO} class handles the whole reading and writing of images and thumbnails.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class ImageIO {
	private String imageFolder;
	private String previewImageFolder;
	
	public List<Bitmap> loadThumbnails() throws XException {
		// video root directory
		imageFolder = Configuration.getAsString(Value.CONFIG_IMAGE_FOLDER) + "/";
		
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
		return getAllBitmapsFromImageFolder();
	}
	
	private void readAndResizeImages() throws XException {
		// get all image files in image root directory
		final File[] imageFilesArray = getAllImageFilesFromDirectory(imageFolder);
		
		if (imageFilesArray != null) {
			for (int i = 0; i < imageFilesArray.length; i++) {
				// resize the image and save it
				resizeAndPersistThumbnail(imageFilesArray[i]);
			}
		}
	}
	
	private List<Bitmap> getAllBitmapsFromImageFolder() {
		final List<Bitmap> bitmaps = new ArrayList<Bitmap>();
		
		// search for the preview images
		final File[] imageFiles = getAllImageFilesFromDirectory(previewImageFolder);
		
		// if the images were found add them as a bitmaps to the list
		if (imageFiles != null) {
			for (File imageFile : imageFiles) {
				// decode the image file as a Bitmap
				bitmaps.add(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
			}
		}
		
		// return the bitmap list
		return bitmaps;
	}
	
	private File[] getAllImageFilesFromDirectory(final String parentDirectory) {
		// search only for images
		final File[] imageFiles = new File(parentDirectory).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String filename) {
				// return only directories with a specified name
//FIXME				return file.isFile() && filename.matches(Values.REGEX_IMAGE);
				return filename.matches(Value.REGEX_IMAGE);
			}
		});
		
		return imageFiles;
	}
	
	private void resizeAndPersistThumbnail(final File imageFile) throws XException {
		try {
			// rotate the image if needed
			final Bitmap baseBitmap = Configuration.getAsBoolean(Value.CONFIG_ROTATE_IMAGES) ?
					BitmapManipulator.rotate(imageFile)
					: BitmapFactory.decodeFile(imageFile.getAbsolutePath());

			// save a thumbnail with size A
			saveThumbnail(manipulateImage(baseBitmap), previewImageFolder, imageFile.getName());
		} catch (Exception ex) {
			throw new XException("Cannot resize and manipulate image", ex);
		}
	}
	
	private Bitmap manipulateImage(final Bitmap image) {
		// resize or crop the image to generate the preview thumbnails
		if (Configuration.getAsBoolean(Value.CONFIG_CROP_IMAGES)) {
			return BitmapManipulator.resizeCrop(image);
		}

		return BitmapManipulator.resize(image);
	}
	
	private void saveThumbnail(final Bitmap thumbnail, final String directory, final String name) throws XException {
		//TODO
		System.out.println(thumbnail.getWidth() + " : " + thumbnail.getHeight());
		try {
			// get byte[] from bitmap
			final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			thumbnail.compress(Bitmap.CompressFormat.JPEG, Value.THUMBNAIL_COMPRESS_QUALITY, bytes);
	
			// create directory if not exists
			new File(directory).mkdirs();
			
			// save the thumbnail
			FileIO.write(new File(directory, name), bytes.toByteArray());
			
			// close output stream
			if (bytes != null) {
				bytes.close();
			}
		} catch (Exception ex) {
			throw new XException("Cannot close output stream" , ex);
		}
	}
}