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

public class ImageIO {
	//TODO rename images to thumbnails
	private String rootDir;
	
	public List<File> loadThumbnails(String videoRootFolder) throws EthanolException {
		// video root directory
		rootDir = Values.SDCARD + videoRootFolder + "/";
		
		// check if resized images have been already created
		// if not create them!
		if (!imagesExist() || Values.RESET) {
			readAndResizeImages();
		}
		
		// at this point all needed images do exist
		// therefore get only a list of the images to work with
		// from thumbnail folder A
		return getThumbnailFilesFromDirectory(Values.THUMBNAIL_FOLDER_A);
	}
	
	private List<File> getThumbnailFilesFromDirectory(String folder) {
		LinkedList<File> images = new LinkedList<File>();
		
		// get all images from a thumbnail folder
		File[] thumbnailFiles = new File(rootDir + folder).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String filename) {
				// return only images
//FIXME				return file.isFile() && filename.matches(Values.REGEX_IMAGE_DIRECTORIES + Values.JPG);
				return filename.matches(Values.REGEX_IMAGE_DIRECTORIES + Values.JPG);
			}
		});
		
		// create a List with all file
		for (File imageFile : thumbnailFiles) {
			// since they were stored in reverse order,
			// reverse them once again to get them in correct order
			images.addFirst(imageFile);
		}
		
		return images;
	}
	
	private List<Bitmap> getBitmapsFromDirectory(String folder) {
		LinkedList<Bitmap> images = new LinkedList<Bitmap>();
		
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
		writeStatusFile();
	}
	
	private boolean imagesExist() {
		// check if the status file already exists
		return new File(rootDir, Values.STATUS_FILE_NAME).exists();
	}
	
	private void writeStatusFile() throws EthanolException {
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

		// if the image was found return it; if not return the default image
		return images.length == 1 ? //FIXME does not work
				BitmapFactory.decodeFile(images[0].getAbsolutePath())
				: BitmapFactory.decodeFile(Values.DEFAULT_IMAGE);
	}

	private void resizeAndPersistThumbnail(Bitmap image, String name) throws EthanolException {
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
		// save the image with size 7
		saveImage(resizeImage(image, EImageSize.G.getDimension()),
				rootDir + Values.THUMBNAIL_FOLDER_G, name);
		// save the image with size 8
		saveImage(resizeImage(image, EImageSize.H.getDimension()),
				rootDir + Values.THUMBNAIL_FOLDER_H, name);
		// save the image with size 9
		saveImage(resizeImage(image, EImageSize.I.getDimension()),
				rootDir + Values.THUMBNAIL_FOLDER_I, name);
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
		try {
			file.setWritable(true);
			file.createNewFile();
			
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(data);
			fos.flush();
			fos.close();
		} catch (IOException ioe) {
			throw new EthanolException(ioe.getMessage());
		}
	}
	
	public List<Bitmap> getThumbnailList(EImageSize imageSize) {
		switch (imageSize) {
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
	
	public Bitmap getThumbnail(String name, EImageSize imageSize) {
		switch (imageSize) {
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