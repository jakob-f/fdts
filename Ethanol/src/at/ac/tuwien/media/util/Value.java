package at.ac.tuwien.media.util;

import java.io.File;

import android.graphics.Color;
import android.os.Environment;
import android.widget.Toast;
import at.ac.tuwien.media.io.file.EThumbnailType;

/**
 * The {@link Value} class is central in the program with static values and various parameters for the Ethanol-App.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public interface Value {
	// TEXTS
	String LOADER_TITLE = "Loading...";
	String LOADER_MESSAGE = "Reading thumbnails, please wait...";
	String DEFAULT_DEBUG_MESSAGE = "No message to display!";
	int DEBUG_DISPLAY_LENGHT = Toast.LENGTH_SHORT;

	// FOLDERS
	String SDCARD = Environment.getExternalStorageDirectory().getPath() + File.separator;
	String ETHANOL_ROOT_FOLDER = SDCARD + "Ethanol" + File.separator;
	String RESIZED_IMAGE_FOLDER = "preview";
	String THUMBNAIL_FOLDER_A = RESIZED_IMAGE_FOLDER + EThumbnailType.A.getName();
	String THUMBNAIL_FOLDER_B = RESIZED_IMAGE_FOLDER + EThumbnailType.B.getName();
	String THUMBNAIL_FOLDER_C = RESIZED_IMAGE_FOLDER + EThumbnailType.C.getName();
	String THUMBNAIL_FOLDER_D = RESIZED_IMAGE_FOLDER + EThumbnailType.D.getName();
	String THUMBNAIL_FOLDER_E = RESIZED_IMAGE_FOLDER + EThumbnailType.E.getName();
	String THUMBNAIL_FOLDER_F = RESIZED_IMAGE_FOLDER + EThumbnailType.F.getName();
	String THUMBNAIL_FOLDER_G = RESIZED_IMAGE_FOLDER + EThumbnailType.G.getName();
	String THUMBNAIL_FOLDER_H = RESIZED_IMAGE_FOLDER + EThumbnailType.H.getName();
	String THUMBNAIL_FOLDER_I = RESIZED_IMAGE_FOLDER + EThumbnailType.I.getName();
	
	// CONFIGURATION VALUES
	String CONFIG_FILE = ".c2h6o";
	String CONFIG_COMMENT = "Ethanol Configuration File";
	// CONFIGURATION KEYS
	String CONFIG_DEBUG = "key_debug";
	String CONFIG_IMAGE_FOLDER = "key_image_folder";
	String CONFIG_RESET = "key_reset";
	String CONFIG_ROTATE_IMAGES = "key_rotate_images";
	String CONFIG_WARP_IMAGES = "key_warp_images";
	String CONFIG_RELOAD = "key_reload";
	String CONFIG_DELETE = "key_delete";
	//  CONFIGURATION DEFAULT VALUES
	boolean CONFIG_DEFAULT_VALUE_DEBUG = false;				// if true display debug messages
	String CONFIG_DEFAULT_VALUE_IMAGE_FOLDER = SDCARD + "DCIM" + File.separator + "Camera";	// image collection folder
	boolean CONFIG_DEFAULT_VALUE_RESET = true;					// if true forces the program to re-create the thumbnails
	boolean CONFIG_DEFAULT_VALUE_ROTATE_IMAGES  = false;		// if true rotate images
	boolean CONFIG_DEFAULT_VALUE_WARP_IMAGES  = false;			// if true warp images
	
	// IMAGES
	String REGEX_IMAGE = "([^\\s]+(\\.(?i)(jpg))$)";
	int THUMBNAIL_COMPRESS_QUALITY = 50;
	int THUMBNAIL_BACKGROUND_COLOR = Color.TRANSPARENT;
	int THUMBNAIL_HIGHLIGHT_PADDING = 25;
	
	// GESTURES
	long SWIPE_TIMEOUT_IN_MILLIS = 10;
	int SWIPE_INTERVAL_FAST = 6;
	int SWIPE_INTERVAL_HALF = 3;
	public enum EDirection {PREVIOUS, FORWARD;}
	
	// VIEW COORDINATES
	int HORIZONTAL_TOP = 0;
	int HORIZONTAL_TOP_ROW_EDGE = 26;
	int HORIZONTAL_MAIN_SECTION_EDGE = 74;
	int HORIZONTAL_BOTTOM = 100;
	
	int VERTICAL_LEFT = 0;
	int VERTICAL_LEFT_10_PERCENT = 10;
	int VERTICAL_LEFT_90_PERCENT = 90;
	int VERTICAL_RIGHT = 100;
	
	int VERTICAL_FIRST_PICTURE_EDGE = 28;
	int VERTICAL_SECOND_PICTURE_EDGE = 72;
}