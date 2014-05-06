package at.ac.tuwien.media.ethanol.io.file.bitmap;

import java.io.File;
import java.util.List;

import android.graphics.Bitmap;
import at.ac.tuwien.media.ethanol.io.file.ImageIO;
import at.ac.tuwien.media.ethanol.io.file.ImageOrderListIO;
import at.ac.tuwien.media.ethanol.io.file.model.EThumbnailType;
import at.ac.tuwien.media.ethanol.util.Configuration;
import at.ac.tuwien.media.ethanol.util.EthanolLogger;
import at.ac.tuwien.media.ethanol.util.Value;
import at.ac.tuwien.media.ethanol.util.exception.EthanolException;

public class ThumbnailListsHandler {
	// file io and thumbnails
	private final ImageIO io;
	
	private final List<File> imageFiles;
	private List<Bitmap> thumbnailsD;
	private List<Bitmap> thumbnailsE;
	private List<Bitmap> thumbnailsF;
	private List<Bitmap> thumbnailsG;
	private List<Bitmap> thumbnailsH;
	private List<Bitmap> thumbnailsI;
	
	public ThumbnailListsHandler() throws EthanolException {
		// save the start time of this operation for the debug message
		EthanolLogger.saveCurrentTime();

		// load images from sdCard
		// create thumbnails if needed
		io = new ImageIO();
		imageFiles = io.loadThumbnails();
		
		EthanolLogger.addDebugMessage("Read " + imageFiles.size() + " images");
	}
	
	public List<File> getImageFiles() {
		return imageFiles;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public Bitmap getBitmapWithSize(final int thumbnailNumber, final EThumbnailType thumbnailType, final boolean isFIAR) {
		// return the thumbnail from the file system or from a list with the given number and size
		switch (thumbnailType) {
			case A:
				return getThumbnail(imageFiles.get(thumbnailNumber).getName(), EThumbnailType.A, isFIAR);
			case B:
				return getThumbnail(imageFiles.get(thumbnailNumber).getName(), EThumbnailType.B, isFIAR);
			case C:
				return getThumbnail(imageFiles.get(thumbnailNumber).getName(), EThumbnailType.C, isFIAR);
			case D:
				if (isFIAR) {
					return getThumbnail(imageFiles.get(thumbnailNumber).getName(), EThumbnailType.D, true);
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
	
	public void insertThumbnailIntoListsAtLocation(final int location, final File thumbnail) {
		// insert a thumbnail at the given location into all lists
		imageFiles.add(location, thumbnail);
		if (thumbnailsD != null) {
			thumbnailsD.add(location, getThumbnail(thumbnail.getName(), EThumbnailType.D));
		}
		if (thumbnailsE != null) {
			thumbnailsE.add(location, getThumbnail(thumbnail.getName(), EThumbnailType.E));
		}
		if (thumbnailsF != null) {
			thumbnailsF.add(location, getThumbnail(thumbnail.getName(), EThumbnailType.F));
		}
		if (thumbnailsG != null) {
			thumbnailsG.add(location, getThumbnail(thumbnail.getName(), EThumbnailType.G));
		}
		if (thumbnailsH != null) {
			thumbnailsH.add(location, getThumbnail(thumbnail.getName(), EThumbnailType.H));
		}
		if (thumbnailsI != null) {
			thumbnailsI.add(location, getThumbnail(thumbnail.getName(), EThumbnailType.I));
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
	
	// load the other thumbnails in the correct order
	// since they are the biggest files, thumbnail sizes A - C are loaded directly,
	// for performance issues thumbnail sizes D - I are cached	
	public List<Bitmap> getThumbnailsD() {
		if (thumbnailsD == null) {
			thumbnailsD = io.getThumbnailList(imageFiles, EThumbnailType.D);
		}
		
		return thumbnailsD;
	}
	
	public List<Bitmap> getThumbnailsE() {
		if (thumbnailsE == null) {
			thumbnailsE = io.getThumbnailList(imageFiles, EThumbnailType.E);
		}
		
		return thumbnailsE;
	}
	
	public List<Bitmap> getThumbnailsF() {
		if (thumbnailsF == null) {
			thumbnailsF = io.getThumbnailList(imageFiles, EThumbnailType.F);
		}
		
		return thumbnailsF;
	}
	
	public List<Bitmap> getThumbnailsG() {
		if (thumbnailsG == null) {
			thumbnailsG = io.getThumbnailList(imageFiles, EThumbnailType.G);
		}
		
		return thumbnailsG;
	}
	
	public List<Bitmap> getThumbnailsH() {
		if (thumbnailsH == null) {
			thumbnailsH = io.getThumbnailList(imageFiles, EThumbnailType.H);
		}
		
		return thumbnailsH;
	}
	
	public List<Bitmap> getThumbnailsI() {
		if (thumbnailsI == null) {
			thumbnailsI = io.getThumbnailList(imageFiles, EThumbnailType.I);
		}
		
		return thumbnailsI;
	}
}
