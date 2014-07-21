package at.ac.tuwien.media.methanol.io.file.bitmap;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

import android.graphics.Bitmap;
import at.ac.tuwien.media.methanol.io.file.ImageIO;
import at.ac.tuwien.media.methanol.io.file.ImageOrderListIO;
import at.ac.tuwien.media.methanol.io.file.model.EThumbnailType;
import at.ac.tuwien.media.methanol.util.Configuration;
import at.ac.tuwien.media.methanol.util.MethanolLogger;
import at.ac.tuwien.media.methanol.util.Util;
import at.ac.tuwien.media.methanol.util.Value;
import at.ac.tuwien.media.methanol.util.exception.MethanolException;

/**
 * This class handles the i/o of all thumbnails lists and caches
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class ThumbnailHandler {
	// file io and thumbnails
	private final ImageIO io;
	
	private int currentThumbnailNo = -1;
	private File fixedThumbnail = null;
	private int fixedThumbnailPos = -1;
	
	private final List<File> imageFiles;
	private final LinkedHashMap<String, Bitmap> thumbnailsCacheB;
	private final LinkedHashMap<String, Bitmap> thumbnailsCacheC;
	private List<Bitmap> thumbnailsListD;
	private List<Bitmap> thumbnailsListE;
	private List<Bitmap> thumbnailsListF;
	private List<Bitmap> thumbnailsListG;
	private List<Bitmap> thumbnailsListH;
	private List<Bitmap> thumbnailsListI;
	
	public ThumbnailHandler() throws MethanolException {
		// save the start time of this operation for the debug message
		MethanolLogger.saveCurrentTime();

		// load images from sdCard
		// create thumbnails if needed
		io = new ImageIO();
		imageFiles = io.loadThumbnails();
		
		MethanolLogger.addDebugMessage("Read " + imageFiles.size() + " images");
		
		thumbnailsCacheB = new LinkedHashMap<String, Bitmap>();
		thumbnailsCacheC = new LinkedHashMap<String, Bitmap>();
	}
	
	public List<File> getImageFiles() {
		return imageFiles;
	}
	
	public Bitmap getBitmapWithSize(final int thumbnailNumber, final EThumbnailType thumbnailType, final boolean isFIAR) {
		// return the thumbnail from the file system or from a list with the given number and size
		switch (thumbnailType) {
			case A:
				return getThumbnail(Util.toThumbnailName(imageFiles.get(thumbnailNumber).getName()), thumbnailType, isFIAR);
			case B:
			case C:				
				return getFromCache(thumbnailNumber, thumbnailType, isFIAR);
			case D:
				if (isFIAR) {
					return getThumbnail(Util.toThumbnailName(imageFiles.get(thumbnailNumber).getName()), thumbnailType, true);
				}
				return getThumbnailsD().get(thumbnailNumber);
			case E:
				return getThumbnailsE().get(thumbnailNumber);
			case F:
				return getThumbnailsF().get(thumbnailNumber);
			case G:
				return getThumbnailsG().get(thumbnailNumber);
			case H:
				return getThumbnailsH().get(thumbnailNumber);
			case I:
				return getThumbnailsI().get(thumbnailNumber);
			default:
				return null;
		}
	}
	
	public void removeThumbnailFromListsAtLocation(final int location) {
		// remove a thumbnail at the given location from all lists
		imageFiles.remove(location);
		if (thumbnailsListD != null) {
			thumbnailsListD.remove(location);
		}
		if (thumbnailsListE != null) {
			thumbnailsListE.remove(location);
		}
		if (thumbnailsListF != null) {
			thumbnailsListF.remove(location);
		}
		if (thumbnailsListG != null) {
			thumbnailsListG.remove(location);
		}
		if (thumbnailsListH != null) {
			thumbnailsListH.remove(location);
		}
		if (thumbnailsListI != null) {
			thumbnailsListI.remove(location);
		}
	}
	
	public void insertFixedThumbnailIntoListsAtCurrentLocation() {
		// insert a thumbnail at the given location into all lists
		imageFiles.add(currentThumbnailNo, fixedThumbnail);
		
		if (thumbnailsListD != null) {
			thumbnailsListD.add(currentThumbnailNo, getThumbnail(fixedThumbnail.getName(), EThumbnailType.D, false));
		}
		if (thumbnailsListE != null) {
			thumbnailsListE.add(currentThumbnailNo, getThumbnail(fixedThumbnail.getName(), EThumbnailType.E, false));
		}
		if (thumbnailsListF != null) {
			thumbnailsListF.add(currentThumbnailNo, getThumbnail(fixedThumbnail.getName(), EThumbnailType.F, false));
		}
		if (thumbnailsListG != null) {
			thumbnailsListG.add(currentThumbnailNo, getThumbnail(fixedThumbnail.getName(), EThumbnailType.G, false));
		}
		if (thumbnailsListH != null) {
			thumbnailsListH.add(currentThumbnailNo, getThumbnail(fixedThumbnail.getName(), EThumbnailType.H, false));
		}
		if (thumbnailsListI != null) {
			thumbnailsListI.add(currentThumbnailNo, getThumbnail(fixedThumbnail.getName(), EThumbnailType.I, false));
		}
		
		// save image order list if wished
		if (Configuration.getAsBoolean(Value.CONFIG_AUTOSAVE)) {
			saveImageOrder();
		}
	}
	
	public void saveImageOrder() {
		try {
			ImageOrderListIO.write(imageFiles);
			
			MethanolLogger.displayDebugMessage("Saved image order list.");
		} catch (MethanolException ee) {
			ee.printStackTrace();
		}		
	}
	
	public Bitmap getThumbnail(final String name, final EThumbnailType thumbnailType, final boolean isFIAR) {
		return io.getThumbnail(name, thumbnailType, isFIAR);
	}
	
	public int getCurrentThumbnailNo() {
		return currentThumbnailNo;
	}
	
	public void setCurrentThumbnailNo(final int currentThumbnailNo) {
		this.currentThumbnailNo = currentThumbnailNo;
	}
	
	public void increaseCurrentThumbnailNo(final int interval) {
		currentThumbnailNo -= interval;
	}
	
	public void decreaseCurrentThumbnailNo(final int interval) {
		currentThumbnailNo += interval;
	}
	
	public int getFixedThumbnailPos() {
		return fixedThumbnailPos;
	}
	
	public void setFixedThumbnailPos(final int fixedThumbnailPos) {
		this.fixedThumbnailPos = fixedThumbnailPos;
	}
	
	public void saveCurrentPosition() {
		fixedThumbnailPos = currentThumbnailNo;
		fixedThumbnail = getImageFiles().get(currentThumbnailNo);
	}
	
	public void resetCurrentPosition() {
		currentThumbnailNo = fixedThumbnailPos;
	}
	
	public void clearCurrentPosition() {
		fixedThumbnailPos = -1;
		fixedThumbnail = null;
	}
	
	public File getFixedThumbnail() {
		return fixedThumbnail;
	}
	
	public boolean isFIAR() {
		return fixedThumbnail != null;
	}
	
	// load thumbnails in the correct order
	// since they are the biggest files, thumbnail A is loaded directly, and sizes B and C are cached (see Value.MAX_THUMBNAILS_CACHE),
	private Bitmap getFromCache(final int thumbnailNumber, final EThumbnailType thumbnailType, final boolean isFIAR) {
		final LinkedHashMap<String, Bitmap> cacheToUse;
		
		switch (thumbnailType) {
		case B:
			cacheToUse = thumbnailsCacheB;
			break;
		case C:
			cacheToUse = thumbnailsCacheC;
			break;
		default:
			return null;
		}
		
		final String thumbnailName = Util.toThumbnailName(imageFiles.get(thumbnailNumber).getName());
		final String key = thumbnailName + isFIAR;
		Bitmap bitmap;
		
		// check if thumbnail was cached
		if (!cacheToUse.containsKey(key)) {
			// try to remove first (oldest) thumbnail
			if (cacheToUse.size() >= Value.MAX_THUMBNAILS_CACHE) {
				cacheToUse.remove(cacheToUse.entrySet().iterator().next().getKey());
			}
			
			// cache new thumbnail
			bitmap = getThumbnail(thumbnailName, thumbnailType, isFIAR);
			cacheToUse.put(key, bitmap);
			
		// get cached thumbnail
		} else {
			bitmap = cacheToUse.get(key);
		}
		
		return bitmap;
	}
	
	// for performance issues thumbnails in sizes D - I are always cached (if needed)	
	// if a specific size is loaded it will recursively load all bigger sizes too,
	// in order to speed up the swiping when the program has finished loading
	private List<Bitmap> getThumbnailsD() {
		if (thumbnailsListD == null) {
			thumbnailsListD = io.getThumbnailList(imageFiles, EThumbnailType.D);
		}
		
		return thumbnailsListD;
	}
	
	private List<Bitmap> getThumbnailsE() {
		if (thumbnailsListE == null) {
			getThumbnailsD();
			
			thumbnailsListE = io.getThumbnailList(imageFiles, EThumbnailType.E);
		}
		
		return thumbnailsListE;
	}
	
	private List<Bitmap> getThumbnailsF() {
		if (thumbnailsListF == null) {
			getThumbnailsE();
			
			thumbnailsListF = io.getThumbnailList(imageFiles, EThumbnailType.F);
		}
		
		return thumbnailsListF;
	}
	
	private List<Bitmap> getThumbnailsG() {
		if (thumbnailsListG == null) {
			getThumbnailsF();
			
			thumbnailsListG = io.getThumbnailList(imageFiles, EThumbnailType.G);
		}
		
		return thumbnailsListG;
	}
	
	private List<Bitmap> getThumbnailsH() {
		if (thumbnailsListH == null) {
			getThumbnailsG();
			
			thumbnailsListH = io.getThumbnailList(imageFiles, EThumbnailType.H);
		}
		
		return thumbnailsListH;
	}
	
	private List<Bitmap> getThumbnailsI() {
		if (thumbnailsListI == null) {
			getThumbnailsH();
			
			thumbnailsListI = io.getThumbnailList(imageFiles, EThumbnailType.I);
		}
		
		return thumbnailsListI;
	}
}
