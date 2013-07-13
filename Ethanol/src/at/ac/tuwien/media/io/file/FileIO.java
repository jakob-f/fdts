package at.ac.tuwien.media.io.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import at.ac.tuwien.media.io.file.image.BitmapManipulator;
import at.ac.tuwien.media.io.file.model.Dimension;
import at.ac.tuwien.media.util.EthanolLogger;
import at.ac.tuwien.media.util.Util;
import at.ac.tuwien.media.util.Value;
import at.ac.tuwien.media.util.exception.EthanolException;

/**
 * The {@link File} class handles the whole reading and writing of images and thumbnails.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class FileIO {
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
			readAndResizeImages();
			
			// prevent recreation on next startup
			Configuration.set(Value.CONFIG_RESET, false);
		}
		
		// at this point all needed thumbnails do exist
		// therefore get only one list of names to work with from thumbnail folder A
		return getThumbnailFilesFromDirectory(Value.THUMBNAIL_FOLDER_A);
	}
	
	private List<File> getThumbnailFilesFromDirectory(final String folder) {
		final List<File> thumbnails = new ArrayList<File>();

		// get all images from a thumbnail folder with the given name
		final File[] thumbnailFiles = getAllImageFilesFromDirectory(previewImageFolder + folder);
		
		if (thumbnailFiles != null) {
			// sort files by pathnames
			Arrays.sort(thumbnailFiles);
			
			// create a List containing all files
			for (File thumbnailFile : thumbnailFiles) {
				thumbnails.add(thumbnailFile);
			}
		}
		
		return thumbnails;
	}
	
	private void readAndResizeImages() throws EthanolException {
		// get all image files in image root directory
		final File[] imageFiles = getAllImageFilesFromDirectory(imageFolder);
		if (imageFiles != null) {
			for (File imageFile : imageFiles) {
				// resize the image and save it with the name of the clip
				resizeAndPersistThumbnail(imageFile, imageFile.getName());
			}
		}
	}
	
	private Bitmap getBitmapFromImageFile(final File imageFile) {
		// returns the given file as an Bitmap
		return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
	}
	
	private List<Bitmap> getBitmapsFromDirectory(final String folder) {
		final List<Bitmap> images = new ArrayList<Bitmap>();
		
		// get a List with all image bitmaps
		for (File imageFile : getThumbnailFilesFromDirectory(folder)) {
			images.add(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
		}
		
		return images;
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
	
	private void resizeAndPersistThumbnail(final File imageFile, final String name) throws EthanolException {
		// save a thumbnail with size 1
		saveThumbnail(manipulateImage(imageFile, EThumbnailType.A.getDimension()),
				previewImageFolder + Value.THUMBNAIL_FOLDER_A, name);
		// save a thumbnail with size 2
		saveThumbnail(manipulateImage(imageFile, EThumbnailType.B.getDimension()),
				previewImageFolder + Value.THUMBNAIL_FOLDER_B, name);
		// save a thumbnail with size 3
		saveThumbnail(manipulateImage(imageFile, EThumbnailType.C.getDimension()),
				previewImageFolder + Value.THUMBNAIL_FOLDER_C, name);
		// save a thumbnail with size 4
		saveThumbnail(manipulateImage(imageFile, EThumbnailType.D.getDimension()),
				previewImageFolder + Value.THUMBNAIL_FOLDER_D, name);
		// save a thumbnail with size 5
		saveThumbnail(manipulateImage(imageFile, EThumbnailType.E.getDimension()),
				previewImageFolder + Value.THUMBNAIL_FOLDER_E, name);
		// save a thumbnail with size 6
		saveThumbnail(manipulateImage(imageFile, EThumbnailType.F.getDimension()),
				previewImageFolder + Value.THUMBNAIL_FOLDER_F, name);
		// save a thumbnail with size 7
		saveThumbnail(manipulateImage(imageFile, EThumbnailType.G.getDimension()),
				previewImageFolder + Value.THUMBNAIL_FOLDER_G, name);
		// save a thumbnail with size 8
		saveThumbnail(manipulateImage(imageFile, EThumbnailType.H.getDimension()),
				previewImageFolder + Value.THUMBNAIL_FOLDER_H, name);
		// save a thumbnail with size 9
		saveThumbnail(manipulateImage(imageFile, EThumbnailType.I.getDimension()),
				previewImageFolder + Value.THUMBNAIL_FOLDER_I, name);
	}
	
	private Bitmap manipulateImage(final File imageFile, final Dimension dimension) throws EthanolException {
		try {
			if (Configuration.getAsBoolean(Value.CONFIG_ROTATE_IMAGES) && Configuration.getAsBoolean(Value.CONFIG_WARP_IMAGES)) {
				return BitmapManipulator.resizeRotateWarp(imageFile, dimension);
			} else if (Configuration.getAsBoolean(Value.CONFIG_ROTATE_IMAGES)) {
				return BitmapManipulator.resizeRotate(imageFile, dimension);
			} else if (Configuration.getAsBoolean(Value.CONFIG_WARP_IMAGES)) {
				return BitmapManipulator.resizeWarp(imageFile, dimension);
			}

			return BitmapManipulator.resize(imageFile, dimension);
		} catch (Exception ex) {
			throw new EthanolException("Cannot resize an manipulate image", ex);
		}
	}
	
	private void saveThumbnail(final Bitmap thumbnail, final String directory, final String name) throws EthanolException {
		try {
			// get byte[] from bitmap
			final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			thumbnail.compress(Bitmap.CompressFormat.JPEG, Value.THUMBNAIL_COMPRESS_QUALITY, bytes);
	
			// create directory if not exists
			new File(directory).mkdirs();
			
			// save the thumbnail
			saveFileOnSystem(new File(directory, name), bytes.toByteArray());
			
			// close output stream
			if (bytes != null) {
				bytes.close();
			}
		} catch (Exception ex) {
			throw new EthanolException("Cannot close output stream" , ex);
		}
		
	}
	
	private void saveFileOnSystem(final File file, final byte[] data) throws EthanolException {
		// try to save a file the file system
		FileOutputStream fos = null;
		try {
			file.setWritable(true);
			file.createNewFile();
			
			fos = new FileOutputStream(file);
			fos.write(data);
		} catch (IOException ioe) {
			// something went wrong
			throw new EthanolException("cannot write file to filesystem", ioe);
		} finally {
			try {
				// close output stream
				if (fos != null) {
					fos.flush();
					fos.close();
				}
			} catch (IOException ioe) {
				throw new EthanolException("Cannot close output stream" , ioe);
			}
		}
	}
	
	public List<Bitmap> getThumbnailList(final EThumbnailType thumbnailType) {
		// return a list with all thumbnails of a given size
		switch (thumbnailType) {
			case A:
				return getBitmapsFromDirectory(Value.THUMBNAIL_FOLDER_A);
			case B:
				return getBitmapsFromDirectory(Value.THUMBNAIL_FOLDER_B);
			case C:
				return getBitmapsFromDirectory(Value.THUMBNAIL_FOLDER_C);
			case D:
				return getBitmapsFromDirectory(Value.THUMBNAIL_FOLDER_D);
			case E:
				return getBitmapsFromDirectory(Value.THUMBNAIL_FOLDER_E);
			case F:
				return getBitmapsFromDirectory(Value.THUMBNAIL_FOLDER_F);
			case G:
				return getBitmapsFromDirectory(Value.THUMBNAIL_FOLDER_G);
			case H:
				return getBitmapsFromDirectory(Value.THUMBNAIL_FOLDER_H);
			case I:
				return getBitmapsFromDirectory(Value.THUMBNAIL_FOLDER_I);
			default:
				return null;
		}
	}
	
	public Bitmap getThumbnail(final String name, final EThumbnailType thumbnailType) {
		// return a thumbnail with the given name and size
		switch (thumbnailType) {
			case A:
				return getBitmapFromDirectory(new File(previewImageFolder + Value.THUMBNAIL_FOLDER_A), name);
			case B:
				return getBitmapFromDirectory(new File(previewImageFolder + Value.THUMBNAIL_FOLDER_B), name);
			case C:
				return getBitmapFromDirectory(new File(previewImageFolder + Value.THUMBNAIL_FOLDER_C), name);
			case D:
				return getBitmapFromDirectory(new File(previewImageFolder + Value.THUMBNAIL_FOLDER_D), name);
			case E:
				return getBitmapFromDirectory(new File(previewImageFolder + Value.THUMBNAIL_FOLDER_E), name);
			case F:
				return getBitmapFromDirectory(new File(previewImageFolder + Value.THUMBNAIL_FOLDER_F), name);
			case G:
				return getBitmapFromDirectory(new File(previewImageFolder + Value.THUMBNAIL_FOLDER_G), name);
			case H:
				return getBitmapFromDirectory(new File(previewImageFolder + Value.THUMBNAIL_FOLDER_H), name);
			case I:
				return getBitmapFromDirectory(new File(previewImageFolder + Value.THUMBNAIL_FOLDER_I), name);
			default:
				return null;
		}
	}
	
	private Bitmap getBitmapFromDirectory(final File directory, final String name) {
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
				getBitmapFromImageFile(images[0])
				: null;
	}
}