package at.ac.tuwien.media.io.file;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import at.ac.tuwien.media.io.file.bitmap.BitmapManipulator;
import at.ac.tuwien.media.io.file.model.Dimension;
import at.ac.tuwien.media.util.EthanolLogger;
import at.ac.tuwien.media.util.Util;
import at.ac.tuwien.media.util.Value;
import at.ac.tuwien.media.util.exception.EthanolException;

/**
 * The {@link FileIO} class handles the whole reading and writing of images and thumbnails.
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
			// first delete old folders(if there are any)
			deleteDirectory(new File(previewImageFolder));
			
			// the read all images from the image folder and
			// write all thumbnail files
			readAndResizeImages();
			
			// prevent recreation on next startup
			Configuration.set(Value.CONFIG_RESET, false);
		}
		
		// at this point all needed thumbnails do exist
		return readThumbnailFileList();
	}
	
	
	private List<File> readThumbnailFileList() throws EthanolException {
		final File imageOrderList = new File(previewImageFolder + Value.IMAGE_ORDER_LIST_FILENAME);
		
		if (imageOrderList.exists()) {
			final String filePaths = readFile(imageOrderList);
			final List<File> imageFileList = new ArrayList<File>();

			for (String filePath : filePaths.split("\n")) {
				imageFileList.add(new File(filePath));
			}
			
			return imageFileList;
		}
		
		return null;
	}
	
	private void writeThumbnailFileList(final List<File> thumbnailFileList) throws EthanolException {
		String orderList = "";
		
		for (File thumbnailFile : thumbnailFileList) {
			orderList += thumbnailFile.getAbsolutePath() + "\n";
		}
			
		saveFile(new File(previewImageFolder + Value.IMAGE_ORDER_LIST_FILENAME), orderList.getBytes());
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
		final List<File> imageFileList = new ArrayList<File>();
		
		if (imageFiles != null) {
			for (int i = 0; i < imageFiles.length; i++) {
				// resize the image and save it with the name of the clip
				resizeAndPersistThumbnail(imageFiles[i]);
				
				// add the save file to image file list
				imageFileList.add(imageFiles[i]);
			}
		}
		
		// save image order list
		writeThumbnailFileList(imageFileList);
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
	
	private void resizeAndPersistThumbnail(final File imageFile) throws EthanolException {
		try {
			// rotate the image if needed
			final Bitmap baseBitmap = Configuration.getAsBoolean(Value.CONFIG_ROTATE_IMAGES) ?
					BitmapManipulator.rotate(imageFile)
					: BitmapFactory.decodeFile(imageFile.getAbsolutePath());

			// save a thumbnail with size A
			saveThumbnail(manipulateImage(baseBitmap, EThumbnailType.A.getDimension(), false),
					previewImageFolder + Value.THUMBNAIL_FOLDER_A, imageFile.getName());
			// save a FIAR thumbnail with size A
			saveThumbnail(manipulateImage(baseBitmap, EThumbnailType.A.getDimension(), true),
					previewImageFolder + Value.THUMBNAIL_FOLDER_A_FIAR, imageFile.getName());

			// save a thumbnail with size B
			saveThumbnail(manipulateImage(baseBitmap, EThumbnailType.B.getDimension(), false),
					previewImageFolder + Value.THUMBNAIL_FOLDER_B, imageFile.getName());
			// save a FIAR thumbnail with size B
			saveThumbnail(manipulateImage(baseBitmap, EThumbnailType.B.getDimension(), true),
					previewImageFolder + Value.THUMBNAIL_FOLDER_B_FIAR, imageFile.getName());

			// save a thumbnail with size C
			saveThumbnail(manipulateImage(baseBitmap, EThumbnailType.C.getDimension(), false),
					previewImageFolder + Value.THUMBNAIL_FOLDER_C, imageFile.getName());
			// save a FIAR thumbnail with size C
			saveThumbnail(manipulateImage(baseBitmap, EThumbnailType.C.getDimension(), true),
					previewImageFolder + Value.THUMBNAIL_FOLDER_C_FIAR, imageFile.getName());

			// save a thumbnail with size D
			saveThumbnail(manipulateImage(baseBitmap, EThumbnailType.D.getDimension(), false),
					previewImageFolder + Value.THUMBNAIL_FOLDER_D, imageFile.getName());
			// save a FIAR thumbnail with size D
			saveThumbnail(manipulateImage(baseBitmap, EThumbnailType.D.getDimension(), true),
					previewImageFolder + Value.THUMBNAIL_FOLDER_D_FIAR, imageFile.getName());

			// save a thumbnail with size E
			saveThumbnail(manipulateImage(baseBitmap, EThumbnailType.E.getDimension(), false),
					previewImageFolder + Value.THUMBNAIL_FOLDER_E, imageFile.getName());

			// save a thumbnail with size F
			saveThumbnail(manipulateImage(baseBitmap, EThumbnailType.F.getDimension(), false),
					previewImageFolder + Value.THUMBNAIL_FOLDER_F, imageFile.getName());

			// save a thumbnail with size G
			saveThumbnail(manipulateImage(baseBitmap, EThumbnailType.G.getDimension(), false),
					previewImageFolder + Value.THUMBNAIL_FOLDER_G, imageFile.getName());

			// save a thumbnail with size H
			saveThumbnail(manipulateImage(baseBitmap, EThumbnailType.H.getDimension(), false),
					previewImageFolder + Value.THUMBNAIL_FOLDER_H, imageFile.getName());

			// save a thumbnail with size I
			saveThumbnail(manipulateImage(baseBitmap, EThumbnailType.I.getDimension(), false),
					previewImageFolder + Value.THUMBNAIL_FOLDER_I, imageFile.getName());
		} catch (Exception ex) {
			throw new EthanolException("Cannot resize an manipulate image", ex);
		}
	}
	
	private Bitmap manipulateImage(final Bitmap image, final Dimension dimension, final boolean isFIAR) {
		// resize or crop the image to generate the preview thumbnails
		if (Configuration.getAsBoolean(Value.CONFIG_CROP_IMAGES)) {
			return BitmapManipulator.resizeCrop(image, dimension);
		}

		return BitmapManipulator.resize(image, dimension, isFIAR);
	}
	
	private void saveThumbnail(final Bitmap thumbnail, final String directory, final String name) throws EthanolException {
		try {
			// get byte[] from bitmap
			final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			thumbnail.compress(Bitmap.CompressFormat.JPEG, Value.THUMBNAIL_COMPRESS_QUALITY, bytes);
	
			// create directory if not exists
			new File(directory).mkdirs();
			
			// save the thumbnail
			saveFile(new File(directory, name), bytes.toByteArray());
			
			// close output stream
			if (bytes != null) {
				bytes.close();
			}
		} catch (Exception ex) {
			throw new EthanolException("Cannot close output stream" , ex);
		}
		
	}
	
	private void saveFile(final File file, final byte[] data) throws EthanolException {
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
	
	private String readFile(final File file) throws EthanolException {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		 
		try {
			br = new BufferedReader(new FileReader(file));
			
			String nextLine;
 
			while ((nextLine = br.readLine()) != null) {
				sb.append(nextLine).append("\n");
			}
 
		} catch (IOException ioe) {
			// something went wrong
			throw new EthanolException("cannot read file from filesystem", ioe);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ioe) {
				throw new EthanolException("Cannot close buffered reader" , ioe);
			}
		}
		
		return sb.toString();
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
	
	public Bitmap getThumbnail(final String name, final EThumbnailType thumbnailType, final boolean isFIAR) {
		if (isFIAR) {
			return getFIARThumbnail(name, thumbnailType);
		}
		
		return getThumbnail(name, thumbnailType);
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
	
	private Bitmap getFIARThumbnail(final String name, final EThumbnailType thumbnailType) {
		// return a FIAR thumbnail with the given name and size
		switch (thumbnailType) {
			case A:
				return getBitmapFromDirectory(new File(previewImageFolder + Value.THUMBNAIL_FOLDER_A_FIAR), name);
			case B:
				return getBitmapFromDirectory(new File(previewImageFolder + Value.THUMBNAIL_FOLDER_B_FIAR), name);
			case C:
				return getBitmapFromDirectory(new File(previewImageFolder + Value.THUMBNAIL_FOLDER_C_FIAR), name);
			case D:
				return getBitmapFromDirectory(new File(previewImageFolder + Value.THUMBNAIL_FOLDER_D_FIAR), name);
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
	
	public void deleteDirectory(final File fileOrFolder) {
	    if (fileOrFolder.isDirectory()) {
	        for (File childFolder : fileOrFolder.listFiles()) {
	        	deleteDirectory(childFolder);
	        }
	    }
	    
	    fileOrFolder.delete();
	}
	
	public List<File> getOriginalImageFilesFromList(final List<File> thumbnailFiles) {
		final List<File> imageFiles = new ArrayList<File>();
		
		// filter and reorder images
		for (File thumbnailFile : thumbnailFiles) {
			// get the image file an check if it exists
			final File imageFile = new File(imageFolder + thumbnailFile.getName());
			if (imageFile.exists()) {
				imageFiles.add(imageFile);
			}
		}
		
		return imageFiles;
	}
}