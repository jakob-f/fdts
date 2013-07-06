package at.ac.tuwien.media.io.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import at.ac.tuwien.media.io.file.model.Dimension;
import at.ac.tuwien.media.util.EthanolLogger;
import at.ac.tuwien.media.util.Value;
import at.ac.tuwien.media.util.exception.EthanolException;

/**
 * The {@link File} class handles the whole reading and writing of images and thumbnails.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class FileIO {
	private String rootDir;
	
	public List<File> loadThumbnails() throws EthanolException {
		// video root directory
		rootDir = Value.SDCARD + Configuration.get(Value.CONFIG_IMAGE_FOLDER) + "/";
		
		EthanolLogger.addDebugMessage("Loading images from " + rootDir);
		
		// check if resized thumbnails have been already created (i.e. reset is true)
		// if not create them!
		if (Configuration.getAsBoolean(Value.CONFIG_RESET)) {
			readAndResizeImages();
			
			// if everything succeeded set reset to false
			// to prevent recreation on next startup
			Configuration.set(Value.CONFIG_RESET, "false");
		}
		
		// at this point all needed thumbnails do exist
		// therefore get only one list of names to work with from thumbnail folder A
		return getThumbnailFilesFromDirectory(Value.THUMBNAIL_FOLDER_A);
	}
	
	private List<File> getThumbnailFilesFromDirectory(final String folder) {
		final LinkedList<File> thumbnails = new LinkedList<File>();
		
		// get all images from a thumbnail folder with the given name
		final File[] thumbnailFiles = getAllImageFilesFromDirectory(rootDir + folder);
		
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
		for (File imageFile : getAllImageFilesFromDirectory(rootDir)) {
			// get preview image
			final Bitmap bitmap = getBitmapFromImageFile(imageFile);
			// resize the image and save it with the name of the clip
			resizeAndPersistThumbnail(bitmap, imageFile.getName());
		}
	}
	
	private Bitmap getBitmapFromImageFile(final File imageFile) {
		// returns the given file as an Bitmap
		return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
	}
	
	private List<Bitmap> getBitmapsFromDirectory(final String folder) {
		final List<Bitmap> images = new LinkedList<Bitmap>();
		
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
	
	private void resizeAndPersistThumbnail(final Bitmap image, final String name) throws EthanolException {
		// save a thumbnail with size 1
		saveThumbnail(resizeImage(image, EThumbnailType.A.getDimension()),
				rootDir + Value.THUMBNAIL_FOLDER_A, name);
		// save a thumbnail with size 2
		saveThumbnail(resizeImage(image, EThumbnailType.B.getDimension()),
				rootDir + Value.THUMBNAIL_FOLDER_B, name);
		// save a thumbnail with size 3
		saveThumbnail(resizeImage(image, EThumbnailType.C.getDimension()),
				rootDir + Value.THUMBNAIL_FOLDER_C, name);
		// save a thumbnail with size 4
		saveThumbnail(resizeImage(image, EThumbnailType.D.getDimension()),
				rootDir + Value.THUMBNAIL_FOLDER_D, name);
		// save a thumbnail with size 5
		saveThumbnail(resizeImage(image, EThumbnailType.E.getDimension()),
				rootDir + Value.THUMBNAIL_FOLDER_E, name);
		// save a thumbnail with size 6
		saveThumbnail(resizeImage(image, EThumbnailType.F.getDimension()),
				rootDir + Value.THUMBNAIL_FOLDER_F, name);
		// save a thumbnail with size 7
		saveThumbnail(resizeImage(image, EThumbnailType.G.getDimension()),
				rootDir + Value.THUMBNAIL_FOLDER_G, name);
		// save a thumbnail with size 8
		saveThumbnail(resizeImage(image, EThumbnailType.H.getDimension()),
				rootDir + Value.THUMBNAIL_FOLDER_H, name);
		// save a thumbnail with size 9
		saveThumbnail(resizeImage(image, EThumbnailType.I.getDimension()),
				rootDir + Value.THUMBNAIL_FOLDER_I, name);
	}
	
	private Bitmap resizeImage(final Bitmap image, final Dimension dimension) {
		// return a thumbnail with the given dimension
		return Bitmap.createScaledBitmap(image, dimension.getWidth(), dimension.getHeight(), true);
	}
	
	private void saveThumbnail(final Bitmap thumbnail, final String directory, final String name) throws EthanolException {
		try {
			// get byte[] from bitmap
			final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			thumbnail.compress(Bitmap.CompressFormat.JPEG, Value.THUMBNAIL_COMPRESS_QUALITY, bytes);
	
			// create directory if not exists
			new File(directory).mkdir();
			
			// save the thumbnail
			saveFileOnSystem(new File(directory, name), bytes.toByteArray());
			
			// close output stream
			if (bytes != null) {
				bytes.close();
			}
		} catch (IOException ioe) {
			throw new EthanolException("Cannot close output stream" , ioe);
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
			fos.flush();
			fos.close();
		} catch (IOException ioe) {
			// something went wrong
			throw new EthanolException("cannot write file to filesystem", ioe);
		} finally {
			try {
				// close output stream
				if (fos != null) {
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
				return getBitmapFromDirectory(new File(rootDir + Value.THUMBNAIL_FOLDER_A), name);
			case B:
				return getBitmapFromDirectory(new File(rootDir + Value.THUMBNAIL_FOLDER_B), name);
			case C:
				return getBitmapFromDirectory(new File(rootDir + Value.THUMBNAIL_FOLDER_C), name);
			case D:
				return getBitmapFromDirectory(new File(rootDir + Value.THUMBNAIL_FOLDER_D), name);
			case E:
				return getBitmapFromDirectory(new File(rootDir + Value.THUMBNAIL_FOLDER_E), name);
			case F:
				return getBitmapFromDirectory(new File(rootDir + Value.THUMBNAIL_FOLDER_F), name);
			case G:
				return getBitmapFromDirectory(new File(rootDir + Value.THUMBNAIL_FOLDER_G), name);
			case H:
				return getBitmapFromDirectory(new File(rootDir + Value.THUMBNAIL_FOLDER_H), name);
			case I:
				return getBitmapFromDirectory(new File(rootDir + Value.THUMBNAIL_FOLDER_I), name);
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
		
		// if the image was found return it as a bitmap; if not return the default bitmap
		return images.length == 1 ?
				getBitmapFromImageFile(images[0])
				: getBitmapFromImageFile(new File(Value.THUMBNAIL_DEFAULT));
	}
}