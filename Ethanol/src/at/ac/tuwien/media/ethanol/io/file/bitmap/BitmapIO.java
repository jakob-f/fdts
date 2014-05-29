package at.ac.tuwien.media.ethanol.io.file.bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import at.ac.tuwien.media.ethanol.io.file.FileIO;
import at.ac.tuwien.media.ethanol.io.file.model.Dimension;
import at.ac.tuwien.media.ethanol.io.file.model.EThumbnailType;
import at.ac.tuwien.media.ethanol.util.Value;
import at.ac.tuwien.media.ethanol.util.exception.EthanolException;

/**
 * This class reads images from the file system in a save way to prevent {@link OutOfMemoryError}s
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class BitmapIO {

	/**
	 * Reads an image from the file system with the minimum sample rate possible
	 * 
	 * @param imagefile the image {@link File} to read
	 * @param dimension the {@link Dimension} of the output image
	 * @return a {@link Bitmap} of the given imageFile
	 */
	public static Bitmap read(final File imageFile, final Dimension dimension) {
		// to speed up the decoding (and to prevent out of memory exceptions)
		// load only a downsampled version of the image:
		// first get only the images width and height ...
		final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
	    // ... get the minimum dimension ...
	    final Dimension scaledDimension = getScaledDimension(new Dimension(options.outWidth, options.outHeight), dimension);
	    // ... and then calculate the minimum sample size to use ...
	    options.inJustDecodeBounds = false;
	    options.inSampleSize = calculateInSampleSizeForImage(options.outWidth, options.outHeight, scaledDimension.getWidth(), scaledDimension.getHeight());
		
	    // ... finally load and scale the bitmap to the dimension
	    return Bitmap.createScaledBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options), scaledDimension.getWidth(), scaledDimension.getHeight(), false);
	}

	/**
	 * Reads an image from the file system with the minimum sample rate possible
	 * The method will always return an image with the minimum size of {@link EThumbnailType} A 
	 * 
	 * @param imagefile the image {@link File} to read
	 * @return a {@link Bitmap} of the given imageFile
	 */
	public static Bitmap read(final File imageFile) {
		// we need at least an image with the size of the biggest thumbnail
		return read(imageFile, EThumbnailType.A.getDimension());
	}
	
	/**
	 * code from
	 * @link http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
	 */
	private static int calculateInSampleSizeForImage(final int imageWidht, final int imageHeight, final int minWidht, final int minHeight) {
		int inSampleSize = 1;
		
		if (minWidht < imageWidht || minHeight < imageHeight) {
			final int halfImageWidth = imageWidht / 2;
			final int halfImageHeight = imageHeight / 2;

			while ((halfImageWidth / inSampleSize) > minWidht &&
					(halfImageHeight / inSampleSize) > minHeight) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}
	
	private static Dimension getScaledDimension(final Dimension bitmapDimension, final Dimension dimensionTo) {
		// check if scaling is necessary
		if (bitmapDimension.getWidth() <= dimensionTo.getWidth() && bitmapDimension.getHeight() <= dimensionTo.getHeight())
	    	return bitmapDimension;
	    
	    // calculate the dimension of the scaled image
	    float scale = (float) bitmapDimension.getHeight() / dimensionTo.getHeight();
		final float scaledImageWidth = (bitmapDimension.getWidth() / scale) < dimensionTo.getWidth() ?
				(bitmapDimension.getWidth() / scale) : dimensionTo.getWidth();
		final float scaledImageHeight = bitmapDimension.getHeight() / scale;
	    
		// return the scaled image
	    return new Dimension((int) scaledImageWidth, (int) scaledImageHeight);
	}
	
	/**
	 * Saves a given {@link Bitmap} in the specified directory with name
	 * 
	 * @param thumbnail the {@link Bitmap} to save
	 * @param directory the directory to save the {@link Bitmap} in
	 * @param name the name of the {@link Bitmap} to save
	 * @throws EthanolException thrown if the image cannot be saved
	 */
	public static void saveBitmap(final Bitmap thumbnail, final String directory, final String name) throws EthanolException {
		try {
			// get byte[] from bitmap
			final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			thumbnail.compress(Bitmap.CompressFormat.JPEG, Value.THUMBNAIL_COMPRESS_QUALITY, bytes);
	
			// create directory if not exists
			final File dir = new File(directory);
			if (dir.exists() || dir.mkdirs()) {
				// save the thumbnail
				FileIO.write(new File(directory, name), bytes.toByteArray());
				
				// close output stream
				bytes.close();
			}
		} catch (IOException ioe) {
			throw new EthanolException("Cannot close output stream" , ioe);
		}
	}
}