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
public final class Value {
	// TEXTS
	public static final String LOADER_TITLE = "Loading...";
	public static final String LOADER_MESSAGE = "Reading thumbnails, please wait!";
	public static final String DEFAULT_DEBUG_MESSAGE = "No message to display!";
	public static final int DEBUG_DISPLAY_LENGHT = Toast.LENGTH_SHORT;

	// FOLDERS
	public static final String SDCARD = Environment.getExternalStorageDirectory().getPath() + File.separator;
	public static final String ETHANOL_ROOT_FOLDER = SDCARD + "Ethanol" + File.separator;
	public static final String RESIZED_IMAGE_FOLDER = "preview";
	public static final String THUMBNAIL_FOLDER_A = RESIZED_IMAGE_FOLDER + EThumbnailType.A.getName();
	public static final String THUMBNAIL_FOLDER_B = RESIZED_IMAGE_FOLDER + EThumbnailType.B.getName();
	public static final String THUMBNAIL_FOLDER_C = RESIZED_IMAGE_FOLDER + EThumbnailType.C.getName();
	public static final String THUMBNAIL_FOLDER_D = RESIZED_IMAGE_FOLDER + EThumbnailType.D.getName();
	public static final String THUMBNAIL_FOLDER_E = RESIZED_IMAGE_FOLDER + EThumbnailType.E.getName();
	public static final String THUMBNAIL_FOLDER_F = RESIZED_IMAGE_FOLDER + EThumbnailType.F.getName();
	public static final String THUMBNAIL_FOLDER_G = RESIZED_IMAGE_FOLDER + EThumbnailType.G.getName();
	public static final String THUMBNAIL_FOLDER_H = RESIZED_IMAGE_FOLDER + EThumbnailType.H.getName();
	public static final String THUMBNAIL_FOLDER_I = RESIZED_IMAGE_FOLDER + EThumbnailType.I.getName();
	
	// CONFIGURATION VALUES
	public static final String CONFIG_FILE = ".c2h6o";
	public static final String CONFIG_COMMENT = "Ethanol Configuration File";
	public static final String CONFIG_DEBUG = "debug";
	public static final String CONFIG_IMAGE_FOLDER = "image_folder";
	public static final String CONFIG_RESET = "reset";
	public static final String CONFIG_ROTATE_IMAGES = "rotate_images";
	public static final String CONFIG_WARP_IMAGES = "warp_images";

	// DEFAULT CONFIGURATION
	public static final String CONFIG_DEFAULT_VALUE_DEBUG = "false";			// if true display debug messages
	public static final String CONFIG_DEFAULT_VALUE_IMAGE_FOLDER = SDCARD + "DCIM" + File.separator + "Camera";	// image collection folder
	public static final String CONFIG_DEFAULT_VALUE_RESET = "true";			// if true forces the program to re-create the thumbnails
	public static final String CONFIG_DEFAULT_VALUE_ROTATE_IMAGES  = "false";	// if true rotate images
	public static final String CONFIG_DEFAULT_VALUE_WARP_IMAGES  = "false";	// if true warp images
	
	// IMAGES
	public static final String REGEX_IMAGE = "([^\\s]+(\\.(?i)(jpg))$)";
	public static final int THUMBNAIL_COMPRESS_QUALITY = 50;
	public static final int THUMBNAIL_BACKGROUND_COLOR = Color.TRANSPARENT;
	public static final int THUMBNAIL_HIGHLIGHT_PADDING = 25;
	
	// GESTURES
	public static final long SWIPE_TIMEOUT_IN_MILLIS = 10;
	public static final int SWIPE_INTERVAL_FAST = 6;
	public static final int SWIPE_INTERVAL_HALF = 3;
	public enum EDirection {PREVIOUS, FORWARD;}
	
	// VIEW COORDINATES
	public static final int HORIZONTAL_TOP = 0;
	public static final int HORIZONTAL_TOP_ROW_EDGE = 26;
	public static final int HORIZONTAL_MAIN_SECTION_EDGE = 74;
	public static final int HORIZONTAL_BOTTOM = 100;
	
	public static final int VERTICAL_LEFT = 0;
	public static final int VERTICAL_LEFT_10_PERCENT = 10;
	public static final int VERTICAL_LEFT_90_PERCENT = 90;
	public static final int VERTICAL_RIGHT = 100;
	
	public static final int VERTICAL_FIRST_PICTURE_EDGE = 28;
	public static final int VERTICAL_SECOND_PICTURE_EDGE = 72;
}