package at.ac.tuwien.media.io.file.image;

import java.io.File;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import at.ac.tuwien.media.io.file.model.Dimension;

/**
 * The {@link BitmapManipulator} class is used to manipulate, i.e. resize, rotate and warp images
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class BitmapManipulator {
	
	/**
	 * Resizes the given image {@link File}
	 * 
	 * @param image the image {@link File} to resize
	 * @param dimension the {@link Dimension} of the output image
	 * @return a new {@link Bitmap} image with the given {@link Dimension}
	 */
	public static Bitmap resize(final File imageFile, final Dimension dimension) {
		//TODO
		return null;
	}
	
	/**
	 * Resizes and rotates the given image {@link File}
	 * 
	 * @param image the image {@link File} to resize and rotate
	 * @param dimension the {@link Dimension} of the output image
	 * @return a new {@link Bitmap} image with the given {@link Dimension} and rotation
	 */
	public static Bitmap resizeRotate(final File imageFile, final Dimension dimension) {
		//TODO
		return null;
	}
	
	/**
	 * Resizes and warps the given image {@link File}
	 * 
	 * @param image the image {@link File} to resize and warp
	 * @param dimension the {@link Dimension} of the output image
	 * @return a new {@link Bitmap} image with the given {@link Dimension}
	 */
	public static Bitmap resizeWarp(final File imageFile, final Dimension dimension) {
		// return a rotated thumbnail with the given dimension
		return resizeWarp(getBitmapFromImageFile(imageFile), dimension);
	}
	
	/**
	 * Resizes, rotates and warps the given image {@link File}
	 * 
	 * @param image the image {@link File} to resize, rotate and warp
	 * @param dimension the {@link Dimension} of the output image
	 * @return a new {@link Bitmap} image with the given {@link Dimension} and rotation
	 * @throws IOException  thrown if the image cannot be rotated
	 */
	public static Bitmap resizeRotateWarp(final File imageFile, final Dimension dimension) throws IOException {
		// return a rotated thumbnail with the given dimension
		Bitmap image = getBitmapFromImageFile(imageFile);
		final Matrix matrix = new Matrix();
		final int imageRotation = new ExifInterface(imageFile.getAbsolutePath()).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
		
		// rotate image
		switch (imageRotation) {
			case ExifInterface.ORIENTATION_NORMAL:
				matrix.postRotate(0);
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				matrix.postRotate(90);
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				matrix.postRotate(180);
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				matrix.postRotate(270);
				break;
				
			// if no orientation flag can be found rotate anyway
			default:
				if (image.getWidth() < image.getHeight()) {
					matrix.postRotate(90);
				}
				break;
		}
		
		// return the rotated image
		return resizeWarp(Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true), dimension);
	}
	
	private static Bitmap resizeWarp(final Bitmap image, final Dimension dimension) {
		// return a rotated thumbnail with the given dimension
		return Bitmap.createScaledBitmap(image, dimension.getWidth(), dimension.getHeight(), false);
	}
	
	private static Bitmap getBitmapFromImageFile(final File imageFile) {
		// returns the given file as an Bitmap
		return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
	}
}