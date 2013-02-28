package at.ac.tuwien.media.io.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import at.ac.tuwien.media.io.file.model.Dimension;
import at.ac.tuwien.media.util.Values;
import at.ac.tuwien.media.util.exception.EthanolException;

// handles the whole reading and writing of images
public class FileIO {
	private String rootDir;
	
	public List<File> loadThumbnails(String videoRootFolder) throws EthanolException {
		// video root directory
		rootDir = Values.SDCARD + videoRootFolder + "/";
		
		// check if resized thumbnails have been already created
		// if not create them!
		if (!statusOk() || Values.RESET) {
			readAndResizeImages();
		}
		
		// at this point all needed thumbnails do exist
		// therefore get only a list of names to work with
		// from thumbnail folder A
		List<File> thumbnails = getThumbnailFilesFromDirectory(Values.THUMBNAIL_FOLDER_A);
		if (thumbnails.size() > 0) {
			return thumbnails;
		}
		
		// throw an exception if no thumbnails are available
		throw new EthanolException("no thumbnails to display!");
	}
	
	private List<File> getThumbnailFilesFromDirectory(String folder) {
		LinkedList<File> thumbnails = new LinkedList<File>();
		
		// get all images from a thumbnail folder with the given name
		File[] thumbnailFiles = new File(rootDir + folder).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String filename) {
				// return only images
//FIXME				return file.isFile() && filename.matches(Values.REGEX_IMAGE_DIRECTORIES + Values.JPG);
				return filename.matches(Values.REGEX_IMAGE_DIRECTORIES + Values.JPG);
			}
		});
		
		// create a List with all file
		for (File thumbnailFile : thumbnailFiles) {
			// TODO is this correct?
			// since they were read in reverse order,
			// reverse them once again to get them in correct order
			thumbnails.add(thumbnailFile);
		}
		
		return thumbnails;
	}
	
	private List<Bitmap> getBitmapsFromDirectory(String folder) {
		List<Bitmap> images = new LinkedList<Bitmap>();
		
		// get a List with all image bitmaps
		for (File imageFile : getThumbnailFilesFromDirectory(folder)) {
			images.add(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
		}
		
		return images;
	}
	
	private void readAndResizeImages() throws EthanolException {
		// get all subdirectories in video root directory
		for (File currentDirectory : getSubdirectories(rootDir)) {
			// get preview image
			Bitmap image = getBitmapFromDirectory(currentDirectory, Values.FIRST_IMAGE_NAME);
			// resize the image and save it with the name of the clip
			resizeAndPersistThumbnail(image, currentDirectory.getName() + Values.JPG);
		}
			
		// if everything succeeded write the status file
		writeStatusOk();
	}
	
	private boolean statusOk() {
		// check if the status file already exists
		return new File(rootDir, Values.STATUS_FILE_NAME).exists();
	}
	
	private void writeStatusOk() throws EthanolException {
		// writes a new status file containing the current date
		saveFileOnSystem(new File(rootDir, Values.STATUS_FILE_NAME), new Date().toString().getBytes());
	}

	private File[] getSubdirectories(String parentDirectory) {
		// search only for directories
		File[] directories = new File(parentDirectory).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String filename) {
				// return only directories with a specified name
//FIXME				return file.isDirectory() && filename.matches(Values.REGEX_IMAGE_DIRECTORIES);
				return filename.matches(Values.REGEX_IMAGE_DIRECTORIES);
			}
		});

		return directories;
	}
	
	private Bitmap getBitmapFromDirectory(File directory, final String name) {
		// search for the preview image with the given name
		File[] images = directory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String filename) {
				return filename.equals(name);
//FIXME				return file.isFile() && filename.equals(Values.FIRST_IMAGE_NAME);
			}
		});

		// if the image was found return it as a bitmap; if not return the default bitmap
		return images.length == 1 ? //FIXME does not work
				BitmapFactory.decodeFile(images[0].getAbsolutePath())
				: BitmapFactory.decodeFile(Values.THUMBNAIL_DEFAULT);
	}

	private void resizeAndPersistThumbnail(Bitmap image, String name) throws EthanolException {
		// save a thumbnail with size 1
		saveThumbnail(resizeImage(image, EThumbnailType.A.getDimension()),
				rootDir + Values.THUMBNAIL_FOLDER_A, name);
		// save a thumbnail with size 2
		saveThumbnail(resizeImage(image, EThumbnailType.B.getDimension()),
				rootDir + Values.THUMBNAIL_FOLDER_B, name);
		// save a thumbnail with size 3
		saveThumbnail(resizeImage(image, EThumbnailType.C.getDimension()),
				rootDir + Values.THUMBNAIL_FOLDER_C, name);
		// save a thumbnail with size 4
		saveThumbnail(resizeImage(image, EThumbnailType.D.getDimension()),
				rootDir + Values.THUMBNAIL_FOLDER_D, name);
		// save a thumbnail with size 5
		saveThumbnail(resizeImage(image, EThumbnailType.E.getDimension()),
				rootDir + Values.THUMBNAIL_FOLDER_E, name);
		// save a thumbnail with size 6
		saveThumbnail(resizeImage(image, EThumbnailType.F.getDimension()),
				rootDir + Values.THUMBNAIL_FOLDER_F, name);
		// save a thumbnail with size 7
		saveThumbnail(resizeImage(image, EThumbnailType.G.getDimension()),
				rootDir + Values.THUMBNAIL_FOLDER_G, name);
		// save a thumbnail with size 8
		saveThumbnail(resizeImage(image, EThumbnailType.H.getDimension()),
				rootDir + Values.THUMBNAIL_FOLDER_H, name);
		// save a thumbnail with size 9
		saveThumbnail(resizeImage(image, EThumbnailType.I.getDimension()),
				rootDir + Values.THUMBNAIL_FOLDER_I, name);
	}
	
	private Bitmap resizeImage(Bitmap image, Dimension dimension) {
		// return a thumbnail with the given dimension
		return Bitmap.createScaledBitmap(image, dimension.getWidth(), dimension.getHeight(), true);
	}
	
	private void saveThumbnail(Bitmap thumbnail, String directory, String name) throws EthanolException {
		// get byte[] from bitmap
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		thumbnail.compress(Bitmap.CompressFormat.JPEG, Values.THUMBNAIL_COMPRESS_QUALITY, bytes);

		// create directory if not exists
		new File(directory).mkdir();
		
		// save the thumbnail
		saveFileOnSystem(new File(directory, name), bytes.toByteArray());
	}
	
	private void saveFileOnSystem(File file, byte[] data) throws EthanolException {
		// try to save a file the file system
		try {
			file.setWritable(true);
			file.createNewFile();
			
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(data);
			fos.flush();
			fos.close();
		} catch (IOException ioe) {
			// something went wrong
			throw new EthanolException(ioe.getMessage());
		}
	}
	
	public List<Bitmap> getThumbnailList(EThumbnailType thumbnailType) {
		// return a list with all thumbnails of a given size
		switch (thumbnailType) {
			case A:
				return getBitmapsFromDirectory(Values.THUMBNAIL_FOLDER_A);
			case B:
				return getBitmapsFromDirectory(Values.THUMBNAIL_FOLDER_B);
			case C:
				return getBitmapsFromDirectory(Values.THUMBNAIL_FOLDER_C);
			case D:
				return getBitmapsFromDirectory(Values.THUMBNAIL_FOLDER_D);
			case E:
				return getBitmapsFromDirectory(Values.THUMBNAIL_FOLDER_E);
			case F:
				return getBitmapsFromDirectory(Values.THUMBNAIL_FOLDER_F);
			case G:
				return getBitmapsFromDirectory(Values.THUMBNAIL_FOLDER_G);
			case H:
				return getBitmapsFromDirectory(Values.THUMBNAIL_FOLDER_H);
			case I:
				return getBitmapsFromDirectory(Values.THUMBNAIL_FOLDER_I);
			default:
				return null;
		}
	}
	
	public Bitmap getThumbnail(String name, EThumbnailType thumbnailType) {
		// return a thumbnail with the given name and size
		switch (thumbnailType) {
			case A:
				return getBitmapFromDirectory(new File(rootDir + Values.THUMBNAIL_FOLDER_A), name);
			case B:
				return getBitmapFromDirectory(new File(rootDir + Values.THUMBNAIL_FOLDER_B), name);
			case C:
				return getBitmapFromDirectory(new File(rootDir + Values.THUMBNAIL_FOLDER_C), name);
			case D:
				return getBitmapFromDirectory(new File(rootDir + Values.THUMBNAIL_FOLDER_D), name);
			case E:
				return getBitmapFromDirectory(new File(rootDir + Values.THUMBNAIL_FOLDER_E), name);
			case F:
				return getBitmapFromDirectory(new File(rootDir + Values.THUMBNAIL_FOLDER_F), name);
			case G:
				return getBitmapFromDirectory(new File(rootDir + Values.THUMBNAIL_FOLDER_G), name);
			case H:
				return getBitmapFromDirectory(new File(rootDir + Values.THUMBNAIL_FOLDER_H), name);
			case I:
				return getBitmapFromDirectory(new File(rootDir + Values.THUMBNAIL_FOLDER_I), name);
			default:
				return null;
		}
	}
}