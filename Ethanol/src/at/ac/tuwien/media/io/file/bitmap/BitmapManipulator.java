package at.ac.tuwien.media.io.file.bitmap;

import java.io.File;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
		// return a resized bitmap with the given dimension
		return resize(getBitmapFromImageFile(imageFile), dimension);
	}
	
	/**
	 * Resizes and rotates the given image {@link File}
	 * 
	 * @param image the image {@link File} to resize and rotate
	 * @param dimension the {@link Dimension} of the output image
	 * @return a new {@link Bitmap} image with the given {@link Dimension} and rotation
	 * @throws IOException thrown if the image cannot be rotated
	 */
	public static Bitmap resizeRotate(final File imageFile, final Dimension dimension) throws IOException {
		// return a resized and rotated bitmap with the given dimension
		return resize(rotate(imageFile), dimension);
	}
	
	/**
	 * Resizes and crops the given image {@link File}
	 * 
	 * @param image the image {@link File} to resize and warp
	 * @param dimension the {@link Dimension} of the output image
	 * @return a new {@link Bitmap} image with the given {@link Dimension}
	 */
	public static Bitmap resizeCrop(final File imageFile, final Dimension dimension) {
		// return a resized and cropped bitmap with the given dimension
		return resizeCrop(getBitmapFromImageFile(imageFile), dimension);
	}
	
	/**
	 * Resizes, rotates and crops the given image {@link File}
	 * 
	 * @param image the image {@link File} to resize, rotate and warp
	 * @param dimension the {@link Dimension} of the output image
	 * @return a new {@link Bitmap} image with the given {@link Dimension} and rotation
	 * @throws IOException thrown if the image cannot be rotated
	 */
	public static Bitmap resizeRotateCrop(final File imageFile, final Dimension dimension) throws IOException {
		// return the rotated, resize and cropped bitmap
		return resizeCrop(rotate(imageFile), dimension);
	}
	
	private static Bitmap resize(final Bitmap image, final Dimension dimension) {
		// create a blank bitmap with the dimension as a background to copy the input image on
		final Bitmap resizedImage = Bitmap.createBitmap(dimension.getWidth(), dimension.getHeight(), image.getConfig());
		
		// calculate the dimension of the scaled image
		final float scale = image.getHeight() / dimension.getHeight();
		final float scaledImageWidth = (image.getWidth() / scale) < dimension.getWidth() ?
				(image.getWidth() / scale) : dimension.getWidth();
		final float scaledImageHeight = (image.getHeight() / scale);
		// calculate the offset of the image on the background
		final float offsetLeft = (dimension.getWidth() / 2) - (scaledImageWidth / 2);
		final float offsetTop = (dimension.getHeight() / 2) - (scaledImageHeight / 2);
		
		// copy image on background
		// since we calculated the right dimension before this will actually not crop the image (...if there is enough room left)
		Canvas canvas = new Canvas(resizedImage);
		canvas.drawBitmap(resizeCrop(image, new Dimension(scaledImageWidth, scaledImageHeight)), offsetLeft, offsetTop, null);
		
		return resizedImage;
	}
	
	private static Bitmap resizeCrop(final Bitmap image, final Dimension dimension) {
		// the according 16:9 landscape image dimension
		final Dimension scaledDimension = new Dimension(image.getWidth());
		
		// crop what is too much
		Bitmap croppedImage = Bitmap.createBitmap(image, 0, 0, scaledDimension.getWidth(), scaledDimension.getHeight()); // TODO set x and y
		
		// return a scaled bitmap with the given dimension
		return Bitmap.createScaledBitmap(croppedImage, dimension.getWidth(), dimension.getHeight(), false);
	}
	
	private static Bitmap rotate(final File imageFile) throws IOException {
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
				
			// if no orientation flag can be found try to rotate the image anyway
			default:
				if (image.getWidth() < image.getHeight()) {
					matrix.postRotate(270);
				}
				break;
		}
		
		// return the rotated bitmap
		return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
	}	
	
	private static Bitmap getBitmapFromImageFile(final File imageFile) {
		// returns the given file as an Bitmap
		return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
	}
}