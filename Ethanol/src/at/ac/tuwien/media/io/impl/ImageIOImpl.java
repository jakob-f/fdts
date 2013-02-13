package at.ac.tuwien.media.io.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import at.ac.tuwien.media.exception.EthanolException;
import at.ac.tuwien.media.util.Dimension;
import at.ac.tuwien.media.util.EImageSize;
import at.ac.tuwien.media.util.Values;

public class ImageIOImpl {
	
	public void readAndResizeImages(String videoRootFolder) throws EthanolException {
		// video root directory
		String rootDir = Values.SDCARD + videoRootFolder + "/";
		
		// check if resized images have been already created
		if (!imagesExist(rootDir)) {
			// get all subdirectories in video root directory
			for (File currentDirectory : getSubdirectories(rootDir)) {
				// get preview image
				Bitmap image = getImageFromDirectory(currentDirectory, Values.FIRST_IMAGE_NAME);
				// resize the image and save it with the name of the clip
				resizeAndPersistImage(image, rootDir, currentDirectory.getName() + Values.JPG);
			}
			
			// if everything succeeded write the status file
			writeStatusFile(rootDir);
		}
	}
	
	private boolean imagesExist(String rootDir) {
		// check if the status file already exists
		return new File(rootDir, Values.STATUS_FILE_NAME).exists();
	}
	
	private void writeStatusFile(String rootDir) throws EthanolException {
		// writes a new status file containing the current date
//		saveFileOnSystem(new File(rootDir, Values.STATUS_FILE_NAME), new Date().toString().getBytes());

		saveFileOnSystem(new File(rootDir, Values.STATUS_FILE_NAME), "TEST".getBytes());
	}

	private File[] getSubdirectories(String parentDirectory) {
		// search only for directories
		File[] directories = new File(parentDirectory).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String filename) {
				// return only directories with a specified name
				return file.isDirectory() && filename.matches(Values.REGEX_IMAGE_DIRECTORIES);
			}
		});

		return directories;
	}
	
// FIXME set to private	
	public Bitmap getImageFromDirectory(File directory, final String imageName) {
		// search for the preview image with the given name
		File[] images = directory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String filename) {
				return filename.equals(imageName);
//FIXME				return file.isFile() && filename.equals(Values.FIRST_IMAGE_NAME);
			}
		});

		// if the image was found return it; if not return the default image
		return images.length == 1 ? //FIXME does not work
				BitmapFactory.decodeFile(images[0].getAbsolutePath())
				: BitmapFactory.decodeFile(Values.DEFAULT_IMAGE);
	}

	private void resizeAndPersistImage(Bitmap image, String rootDir, String name) throws EthanolException {
		// save the image with size 1
		saveImage(resizeImage(image, EImageSize.A.getDimension()),
				rootDir + Values.THUMBNAIL_FOLDER_A, name);
		// save the image with size 2
		saveImage(resizeImage(image, EImageSize.B.getDimension()),
				rootDir + Values.THUMBNAIL_FOLDER_B, name);
		// save the image with size 3
		saveImage(resizeImage(image, EImageSize.C.getDimension()),
				rootDir + Values.THUMBNAIL_FOLDER_C, name);
		// save the image with size 4
		saveImage(resizeImage(image, EImageSize.D.getDimension()),
				rootDir + Values.THUMBNAIL_FOLDER_D, name);
		// save the image with size 5
		saveImage(resizeImage(image, EImageSize.E.getDimension()),
				rootDir + Values.THUMBNAIL_FOLDER_E, name);
		// save the image with size 6
		saveImage(resizeImage(image, EImageSize.F.getDimension()),
				rootDir + Values.THUMBNAIL_FOLDER_F, name);
	}
	
	private Bitmap resizeImage(Bitmap image, Dimension dimension) {
		// return a image with the given dimension
		return Bitmap.createScaledBitmap(image, dimension.getWidth(), dimension.getHeight(), true);
	}
	
	private void saveImage(Bitmap image, String directory, String name) throws EthanolException {
		// get byte[] from bitmap
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, Values.IMAGE_COMPRESS_QUALITY, bytes);

		// create directory if not exists
		new File(directory).mkdir();
		
		// save the image
		saveFileOnSystem(new File(directory, name), bytes.toByteArray());
	}
	
	private void saveFileOnSystem(File file, byte[] data) throws EthanolException {
		//FIXME write a file to the filesystem
		try {
			file.setWritable(true);
			file.createNewFile();
			
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(data);
			fos.flush();
			fos.close();
		} catch (IOException ioe) {
//			throw new EthanolException(ioe.getMessage());
			throw new EthanolException(file.getAbsolutePath());
		}
	}
	
	public Bitmap getImage(String name, EImageSize imageSize) {
		switch (imageSize) {
			case A:
				return getImageFromDirectory(new File(Values.THUMBNAIL_FOLDER_A), name);
			case B:
				return getImageFromDirectory(new File(Values.THUMBNAIL_FOLDER_B), name);
			case C:
				return getImageFromDirectory(new File(Values.THUMBNAIL_FOLDER_C), name);
			case D:
				return getImageFromDirectory(new File(Values.THUMBNAIL_FOLDER_D), name);
			case E:
				return getImageFromDirectory(new File(Values.THUMBNAIL_FOLDER_E), name);
			case F:
				return getImageFromDirectory(new File(Values.THUMBNAIL_FOLDER_F), name);
			default:
				return null;
		}
	}
}