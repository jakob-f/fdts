package at.ac.tuwien.media.ethanol.util;

import java.io.File;

import android.graphics.Color;
import android.os.Environment;
import android.widget.Toast;
import at.ac.tuwien.media.ethanol.io.file.model.EThumbnailType;

/**
 * The {@link Value} class is central in the program with static values and various parameters for the Ethanol-App.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public interface Value {
	int DEBUG_DISPLAY_LENGHT = Toast.LENGTH_SHORT;

	// FOLDERS
	String SDCARD = Environment.getExternalStorageDirectory().getPath() + File.separator;
	String ETHANOL_ROOT_FOLDER = SDCARD + "Ethanol" + File.separator;
	String RESIZED_IMAGE_FOLDER = "preview";
	String FIAR_IMAGE_FOLDER = "_fiar";
	String THUMBNAIL_FOLDER_A = RESIZED_IMAGE_FOLDER + EThumbnailType.A.getName();
	String THUMBNAIL_FOLDER_B = RESIZED_IMAGE_FOLDER + EThumbnailType.B.getName();
	String THUMBNAIL_FOLDER_C = RESIZED_IMAGE_FOLDER + EThumbnailType.C.getName();
	String THUMBNAIL_FOLDER_D = RESIZED_IMAGE_FOLDER + EThumbnailType.D.getName();
	String THUMBNAIL_FOLDER_E = RESIZED_IMAGE_FOLDER + EThumbnailType.E.getName();
	String THUMBNAIL_FOLDER_F = RESIZED_IMAGE_FOLDER + EThumbnailType.F.getName();
	String THUMBNAIL_FOLDER_G = RESIZED_IMAGE_FOLDER + EThumbnailType.G.getName();
	String THUMBNAIL_FOLDER_H = RESIZED_IMAGE_FOLDER + EThumbnailType.H.getName();
	String THUMBNAIL_FOLDER_I = RESIZED_IMAGE_FOLDER + EThumbnailType.I.getName();
	String THUMBNAIL_FOLDER_A_FIAR = THUMBNAIL_FOLDER_A + FIAR_IMAGE_FOLDER;
	String THUMBNAIL_FOLDER_B_FIAR = THUMBNAIL_FOLDER_B + FIAR_IMAGE_FOLDER;
	String THUMBNAIL_FOLDER_C_FIAR = THUMBNAIL_FOLDER_C + FIAR_IMAGE_FOLDER;
	String THUMBNAIL_FOLDER_D_FIAR = THUMBNAIL_FOLDER_D + FIAR_IMAGE_FOLDER;
	
	// CONFIGURATION VALUES
	String CONFIG_FILENAME = ".c2h6o";
	String CONFIG_COMMENT = "Ethanol Configuration File";
	String IMAGE_ORDER_LIST_FILENAME = ".order";
	// CONFIGURATION KEYS
	String CONFIG_AUTOSAVE = "key_autosave";
	String CONFIG_CROP_IMAGES = "key_crop_images";
	String CONFIG_DEBUG = "key_debug";
	String CONFIG_DELETE = "key_delete";
	String CONFIG_IMAGE_FOLDER = "key_image_folder";
	String CONFIG_JUMP_BACK = "key_jump_back";
	String CONFIG_PREVIEW_BACK = "key_preview_back";
	String CONFIG_RELOAD = "key_reload";
	String CONFIG_RESET = "key_reset";
	String CONFIG_ROTATE_IMAGES = "key_rotate_images";
	String CONFIG_SWIPE_SCROLL = "key_swipe_scroll";
	String CONFIG_SWIPE_SELECT = "key_swipe_select";
	String CONFIG_SWIPE_SIMPLE = "key_swipe_simple";
	String CONFIG_SWIPE_SLIDE = "key_swipe_slide";
	String CONFIG_SWIPE_VERTICAL = "key_swipe_vertical";
	
	//  CONFIGURATION DEFAULT VALUES
	boolean CONFIG_DEFAULT_VALUE_AUTOSAVE = false;				// if true autosave image order
	boolean CONFIG_DEFAULT_VALUE_CROP_IMAGES  = false;			// if true crop images
	boolean CONFIG_DEFAULT_VALUE_DEBUG = false;					// if true display debug messages
	String CONFIG_DEFAULT_VALUE_IMAGE_FOLDER = SDCARD + "DCIM" + File.separator + "Camera";	// image collection folder
	boolean CONFIG_DEFAULT_VALUE_JUMP_BACK = false;				// if true jump back after FIAR
	boolean CONFIG_DEFAULT_VALUE_PREVIEW_BACK = false;			// if true jump back after image preview
	boolean CONFIG_DEFAULT_VALUE_RESET = true;					// if true forces the program to re-create the thumbnails
	boolean CONFIG_DEFAULT_VALUE_ROTATE_IMAGES = false;			// if true rotate images
	boolean CONFIG_DEFAULT_VALUE_SWIPE_SCROLL = true;			// if true enable scroll swipe
	boolean CONFIG_DEFAULT_VALUE_SWIPE_SELECT = true;			// if true enable select swipe
	boolean CONFIG_DEFAULT_VALUE_SWIPE_SIMPLE = true;			// if true enable simple swipe
	boolean CONFIG_DEFAULT_VALUE_SWIPE_SLIDE = true;			// if true enable slide swipe
	boolean CONFIG_DEFAULT_VALUE_SWIPE_VERTICAL = true;			// if true enable vertical swipes

	// COLORS
	int COLOR_BACKGROUND_DEBUG = Color.DKGRAY;
	int COLOR_BACKGROUND_FIAR = Color.parseColor("#325F8C");
	int COLOR_BACKGROUND_NORMAL = Color.BLACK;
	int COLOR_BACKGROUND_SLIDER = Color.WHITE;
	int COLOR_BACKGROUND_GRADIENT = Color.parseColor("#123456");
	int COLOR_TRANSPARENT = Color.TRANSPARENT;
	
	// SLIDER
	int SLIDER_WIDTH = 50;
	
	// IMAGES
	String REGEX_IMAGE = "([^\\s]+(\\.(?i)(jpg))$)";
	int THUMBNAIL_COMPRESS_QUALITY = 50;
	int THUMBNAIL_HIGHLIGHT_PADDING = 25;
	
	// GESTURES
	long TIMEOUT_IN_MILLIS_SWIPE = 10;
	int SWIPE_INTERVAL_FAST = 6;
	int SWIPE_INTERVAL_HALF = 3;
	int SWIPE_MIN_DISTANCE = 5;
	enum EDirection {PREVIOUS, FORWARD, NONE;}
	
	// VIEW COORDINATES
	enum ERow {NONE, TOP, BOTTOM, BOTH;}
	
	int HORIZONTAL_TOP = 0;
	int HORIZONTAL_TOP_ROW_EDGE = 26;
	int HORIZONTAL_MAIN_SECTION_EDGE = 74;
	int HORIZONTAL_BOTTOM_LINE = 95;
	int HORIZONTAL_BOTTOM = 100;
	
	int VERTICAL_LEFT = 0;
	int VERTICAL_LEFT_10_PERCENT = 10;
	int VERTICAL_LEFT_90_PERCENT = 90;
	int VERTICAL_RIGHT = 100;
	
	int VERTICAL_FIRST_PICTURE_EDGE = 28;
	int VERTICAL_SECOND_PICTURE_EDGE = 72;
}