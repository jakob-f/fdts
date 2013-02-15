package at.ac.tuwien.media.util;

import android.os.Environment;

public final class Values {
	// ALL OTHERS
	public static final boolean RESET = false;
	public static final String JPG = ".jpg";
	public static final int IMAGE_COMPRESS_QUALITY = 100;
	
	// FOLDERS
	public static final String SDCARD = Environment.getExternalStorageDirectory().getPath() + "/";
	public static final String REGEX_IMAGE_DIRECTORIES = "[0-9]{4}";
	private static final String RESIZED_IMAGE_FOLDER = "preview";
	public static final String THUMBNAIL_FOLDER_A = RESIZED_IMAGE_FOLDER + EImageSize.A.getName();
	public static final String THUMBNAIL_FOLDER_B = RESIZED_IMAGE_FOLDER + EImageSize.B.getName();
	public static final String THUMBNAIL_FOLDER_C = RESIZED_IMAGE_FOLDER + EImageSize.C.getName();
	public static final String THUMBNAIL_FOLDER_D = RESIZED_IMAGE_FOLDER + EImageSize.D.getName();
	public static final String THUMBNAIL_FOLDER_E = RESIZED_IMAGE_FOLDER + EImageSize.E.getName();
	public static final String THUMBNAIL_FOLDER_F = RESIZED_IMAGE_FOLDER + EImageSize.F.getName();
	
	// IMAGES
	public static final String STATUS_FILE_NAME = ".c2h6o";
	public static final String FIRST_IMAGE_NAME = "0001" + JPG;
	public static final String DEFAULT_IMAGE = SDCARD + "images/default" + JPG; //TODO set me!
	
	// GESTURES
	public static final int SWIPE_THRESHOLD_VELOCITY = 200;
	public static final int FAST_SWIPE_INTERVAL = 5;
	
	// VIEW COORDINATES
	public static final int HORIZONTAL_TOP = 0;
	public static final int HORIZONTAL_BOTTOM = 100;
	public static final int HORIZONTAL_CENTER = HORIZONTAL_BOTTOM / 2;
	public static final int HORIZONTAL_FIRST_FRAME_EDGE = 33;
	public static final int HORIZONTAL_SECOND_FRAME_EDGE = 66;
	
	public static final int VERTICAL_LEFT = 0;
	public static final int VERTICAL_RIGHT = 100;
	public static final int VERTICAL_CENTER = VERTICAL_RIGHT / 2;
	public static final int VERTICAL_FIRST_PICTURE_EDGE = 33;
	public static final int VERTICAL_SECOND_PICTURE_EDGE = 66;
}