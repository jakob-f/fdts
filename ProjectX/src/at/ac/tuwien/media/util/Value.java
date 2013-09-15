package at.ac.tuwien.media.util;

import java.io.File;

import android.graphics.Bitmap;
import android.os.Environment;

/**
 * The {@link Value} class is central in the program with static values and
 * various central parameters.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public interface Value {
	// FILE PATHS
	String SDCARD = Environment.getExternalStorageDirectory().getPath() + File.separator;
	String ROOT_FOLDER = SDCARD + "X" + File.separator;
	String RESIZED_IMAGE_FOLDER = "preview";
	
	// CONFIGURATION VALUES
	String CONFIG_FILENAME = ".configuration";
	String CONFIG_COMMENT = "Configuration File";
	
	// CONFIGURATION KEYS
	String CONFIG_CROP_IMAGES = "key_crop_images";
	String CONFIG_DELETE = "key_delete";
	String CONFIG_IMAGE_FOLDER = "key_image_folder";
	String CONFIG_RELOAD = "key_reload";
	String CONFIG_RESET = "key_reset";
	String CONFIG_ROTATE_IMAGES = "key_rotate_images";
		
	//  CONFIGURATION DEFAULT VALUES
	boolean CONFIG_DEFAULT_VALUE_CROP_IMAGES  = false;			// if true crop images
	String CONFIG_DEFAULT_VALUE_IMAGE_FOLDER = SDCARD + "DCIM" + File.separator + "Camera";	// image collection folder
	boolean CONFIG_DEFAULT_VALUE_RESET = true;					// if true forces the program to re-create the thumbnails
	boolean CONFIG_DEFAULT_VALUE_ROTATE_IMAGES = false;		// if true rotate images
	
	// GESTURES
	long TIMEOUT_IN_MILLIS_FLING = 10;

	// VIEW COORDINATES
	int DISPLAY_WIDTH = Util.getDisplayMetrics().x;				// ugly but it works
	int DISPLAY_HEIGHT = Util.getDisplayMetrics().y;
	int THUMBNAIL_PADDING = 8;
	enum EThumbnailPostion {LEFT, MIDDLE, RIGHT};
	
	// IMAGES
	String REGEX_IMAGE = "([^\\s]+(\\.(?i)(jpg))$)";
	int THUMBNAIL_COMPRESS_QUALITY = 50;
	int THUMBNAIL_HIGHLIGHT_PADDING = 25;
	int THUMBNAIL_WIDTH = Math.round(((DISPLAY_WIDTH - (4 * THUMBNAIL_PADDING)) / 5.0f));
	int THUMBNAIL_HEIGHT = (int) ((THUMBNAIL_WIDTH / 16.0f) * 9.0f);
	Bitmap EMPTY_BITMAP = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
}
