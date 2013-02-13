package at.ac.tuwien.media.util;

import android.os.Environment;

public final class Values {
	// ALL OTHERS
	public final static String JPG = ".jpg";
	public final static int IMAGE_COMPRESS_QUALITY = 100;
	
	// FOLDERS
	public final static String SDCARD = Environment.getExternalStorageDirectory().getPath() + "/";
	public final static String REGEX_IMAGE_DIRECTORIES = "[0-9]{4}";
	private final static String RESIZED_IMAGE_FOLDER = "preview";
	public final static String THUMBNAIL_FOLDER_A = RESIZED_IMAGE_FOLDER + EImageSize.A.getName();
	public final static String THUMBNAIL_FOLDER_B = RESIZED_IMAGE_FOLDER + EImageSize.B.getName();
	public final static String THUMBNAIL_FOLDER_C = RESIZED_IMAGE_FOLDER + EImageSize.C.getName();
	public final static String THUMBNAIL_FOLDER_D = RESIZED_IMAGE_FOLDER + EImageSize.D.getName();
	public final static String THUMBNAIL_FOLDER_E = RESIZED_IMAGE_FOLDER + EImageSize.E.getName();
	public final static String THUMBNAIL_FOLDER_F = RESIZED_IMAGE_FOLDER + EImageSize.F.getName();
	
	// IMAGES
	public final static String STATUS_FILE_NAME = "c2h6o";
	public final static String FIRST_IMAGE_NAME = "0001" + JPG;
	public final static String DEFAULT_IMAGE = SDCARD + "images/xxx/kenny" + JPG; //TODO set me!
	

}