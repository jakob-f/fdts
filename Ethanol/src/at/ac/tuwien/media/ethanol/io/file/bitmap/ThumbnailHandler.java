package at.ac.tuwien.media.ethanol.io.file.bitmap;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

import android.graphics.Bitmap;
import at.ac.tuwien.media.ethanol.io.file.ImageIO;
import at.ac.tuwien.media.ethanol.io.file.ImageOrderListIO;
import at.ac.tuwien.media.ethanol.io.file.model.EThumbnailType;
import at.ac.tuwien.media.ethanol.util.Configuration;
import at.ac.tuwien.media.ethanol.util.EthanolLogger;
import at.ac.tuwien.media.ethanol.util.Value;
import at.ac.tuwien.media.ethanol.util.exception.EthanolException;

public class ThumbnailHandler {
	// file io and thumbnails
	private final ImageIO io;
	
	private int currentThumbnailNo = -1;
	private File fixedThumbnail = null;
	private int fixedThumbnailPos = -1;
	
	private final List<File> imageFiles;
	private final LinkedHashMap<String, Bitmap> thumbnailsCacheB;
	private final LinkedHashMap<String, Bitmap> thumbnailsCacheC;
	private List<Bitmap> thumbnailsD;
	private List<Bitmap> thumbnailsE;
	private List<Bitmap> thumbnailsF;
	private List<Bitmap> thumbnailsG;
	private List<Bitmap> thumbnailsH;
	private List<Bitmap> thumbnailsI;
	
	public ThumbnailHandler() throws EthanolException {
		// save the start time of this operation for the debug message
		EthanolLogger.saveCurrentTime();

		// load images from sdCard
		// create thumbnails if needed
		io = new ImageIO();
		imageFiles = io.loadThumbnails();
		
		EthanolLogger.addDebugMessage("Read " + imageFiles.size() + " images");
		
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
				return getThumbnail(imageFiles.get(thumbnailNumber).getName(), thumbnailType, isFIAR);
			case B:
			case C:				
				return getFromCache(thumbnailNumber, thumbnailType, isFIAR);
			case D:
				if (isFIAR) {
					return getThumbnail(imageFiles.get(thumbnailNumber).getName(), thumbnailType, true);
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
		if (thumbnailsD != null) {
			thumbnailsD.remove(location);
		}
		if (thumbnailsE != null) {
			thumbnailsE.remove(location);
		}
		if (thumbnailsF != null) {
			thumbnailsF.remove(location);
		}
		if (thumbnailsG != null) {
			thumbnailsG.remove(location);
		}
		if (thumbnailsH != null) {
			thumbnailsH.remove(location);
		}
		if (thumbnailsI != null) {
			thumbnailsI.remove(location);
		}
	}
	
	public void insertFixedThumbnailIntoListsAtCurrentLocation() {
		// insert a thumbnail at the given location into all lists
		imageFiles.add(currentThumbnailNo, fixedThumbnail);
		
		if (thumbnailsD != null) {
			thumbnailsD.add(currentThumbnailNo, getThumbnail(fixedThumbnail.getName(), EThumbnailType.D));
		}
		if (thumbnailsE != null) {
			thumbnailsE.add(currentThumbnailNo, getThumbnail(fixedThumbnail.getName(), EThumbnailType.E));
		}
		if (thumbnailsF != null) {
			thumbnailsF.add(currentThumbnailNo, getThumbnail(fixedThumbnail.getName(), EThumbnailType.F));
		}
		if (thumbnailsG != null) {
			thumbnailsG.add(currentThumbnailNo, getThumbnail(fixedThumbnail.getName(), EThumbnailType.G));
		}
		if (thumbnailsH != null) {
			thumbnailsH.add(currentThumbnailNo, getThumbnail(fixedThumbnail.getName(), EThumbnailType.H));
		}
		if (thumbnailsI != null) {
			thumbnailsI.add(currentThumbnailNo, getThumbnail(fixedThumbnail.getName(), EThumbnailType.I));
		}
		
		// save image order list if wished
		if (Configuration.getAsBoolean(Value.CONFIG_AUTOSAVE)) {
			saveImageOrder();
		}
	}
	
	public void saveImageOrder() {
		try {
			ImageOrderListIO.write(imageFiles);
			
			EthanolLogger.displayDebugMessage("Saved image order list.");
		} catch (EthanolException ee) {
			ee.printStackTrace();
		}		
	}
	
	public Bitmap getThumbnail(final String name, final EThumbnailType thumbnailType) {
		return io.getThumbnail(name, thumbnailType);
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
	// for performance issues thumbnail sizes D - I are always cached (if needed)
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
		
		final String thumbnailName = imageFiles.get(thumbnailNumber).getName();
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
	
	private List<Bitmap> getThumbnailsD() {
		if (thumbnailsD == null) {
			thumbnailsD = io.getThumbnailList(imageFiles, EThumbnailType.D);
		}
		
		return thumbnailsD;
	}
	
	private List<Bitmap> getThumbnailsE() {
		if (thumbnailsE == null) {
			thumbnailsE = io.getThumbnailList(imageFiles, EThumbnailType.E);
		}
		
		return thumbnailsE;
	}
	
	private List<Bitmap> getThumbnailsF() {
		if (thumbnailsF == null) {
			thumbnailsF = io.getThumbnailList(imageFiles, EThumbnailType.F);
		}
		
		return thumbnailsF;
	}
	
	private List<Bitmap> getThumbnailsG() {
		if (thumbnailsG == null) {
			thumbnailsG = io.getThumbnailList(imageFiles, EThumbnailType.G);
		}
		
		return thumbnailsG;
	}
	
	private List<Bitmap> getThumbnailsH() {
		if (thumbnailsH == null) {
			thumbnailsH = io.getThumbnailList(imageFiles, EThumbnailType.H);
		}
		
		return thumbnailsH;
	}
	
	private List<Bitmap> getThumbnailsI() {
		if (thumbnailsI == null) {
			thumbnailsI = io.getThumbnailList(imageFiles, EThumbnailType.I);
		}
		
		return thumbnailsI;
	}
}
