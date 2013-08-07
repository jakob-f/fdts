package at.ac.tuwien.media.io.file.bitmap;

import java.io.File;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import at.ac.tuwien.media.io.file.model.Dimension;
import at.ac.tuwien.media.util.Value;

/**
 * The {@link BitmapManipulator} class is used to manipulate, i.e. resize, rotate and warp images
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class BitmapManipulator {
	
	/**
	 * Rotates the given image.
	 * 
	 * @param imageFile the image {@link File} to rotate
	 * @return a new {@link Bitmap} image with the given rotation
	 * @throws IOException thrown if the image file cannot be read
	 */
	public static Bitmap rotate(final File imageFile) throws IOException {
		Bitmap image = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
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
	
	/**
	 * Resizes the given image {@link Bitmap}
	 * 
	 * @param image the image {@link Bitmap} to resize
	 * @param dimension the {@link Dimension} of the output image
	 * @return a new {@link Bitmap} image with the given {@link Dimension}
	 */
	public static Bitmap resize(final Bitmap image, final Dimension dimension, final boolean isFIAR) {
		// create a blank bitmap with the dimension as a background to copy the input image on
		final Bitmap resizedImage = Bitmap.createBitmap(dimension.getWidth(), dimension.getHeight(), image.getConfig());
		
		// fill the background with FIAR Color if needed
		if (isFIAR) {
			resizedImage.eraseColor(Value.COLOR_BACKGROUND_FIAR);
		}
		
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
	
	/**
	 * Resizes and crops the given image {@link File}
	 * 
	 * @param image the image {@link File} to resize and warp
	 * @param dimension the {@link Dimension} of the output image
	 * @return a new {@link Bitmap} image with the given {@link Dimension}
	 */
	public static Bitmap resizeCrop(final Bitmap image, final Dimension dimension) {
		// the according 16:9 landscape image dimension
		final Dimension scaledDimension = new Dimension(image.getWidth());
		
		// calculate the y point to center the image
		final int y = (image.getHeight() / 2) - (scaledDimension.getHeight() / 2);
		// crop what is too much
		Bitmap croppedImage = Bitmap.createBitmap(image, 0, y, scaledDimension.getWidth(), scaledDimension.getHeight());
		
		// return a scaled bitmap with the given dimension
		return Bitmap.createScaledBitmap(croppedImage, dimension.getWidth(), dimension.getHeight(), false);
	}
}