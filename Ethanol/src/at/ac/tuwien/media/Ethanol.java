package at.ac.tuwien.media;

import java.io.File;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import at.ac.tuwien.media.io.file.EImageSize;
import at.ac.tuwien.media.io.file.ImageIO;
import at.ac.tuwien.media.io.gesture.EthanolGestureDetector;
import at.ac.tuwien.media.util.Values;
import at.ac.tuwien.media.util.Values.EDirection;
import at.ac.tuwien.media.util.Values.EProgram;
import at.ac.tuwien.media.util.exception.EthanolException;

public class Ethanol extends Activity implements IImageSwipe {
	private final static String VIDEO_NAME = "images";
	private Point displaySize;
	private GestureDetector gestureDetector;

	private ImageIO imageIO;
	private int currentThumbnailNo = -1;
	private File fixedImage = null;

	private ImageView[] imageViews;
	
	private List<File> thumbnailFiles;
	private List<Bitmap> thumbnailsD;
	private List<Bitmap> thumbnailsE;
	private List<Bitmap> thumbnailsF;
	private List<Bitmap> thumbnailsG;
	private List<Bitmap> thumbnailsH;
	private List<Bitmap> thumbnailsI;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
		setContentView(R.layout.activity_ethanol);
		
		// load thumbnails
        loadThumbnails();
        
        // Gesture detection
        initGestureDetection();
        
        // add view items
        initViews();
        
        // load first image
        skipToImage(EDirection.NEXT, 1);
        
		} catch (EthanolException ee) {
			makeToast("Cannot start Ethanol: " + ee.getMessage());
			finish();
		} catch (Exception e) {
			makeToast("Cannot start Ethanol");
			finish();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	private void loadThumbnails() throws EthanolException {
		// load images from sdCard
		// create thumbnails if needed
		imageIO = new ImageIO();
		thumbnailFiles = imageIO.loadThumbnails(VIDEO_NAME);

		// since they are the biggest files, thumbnai sizes A - C
		// are loaded directly
		// for performance issues thumbnail sizes D - I are cached
		thumbnailsD = imageIO.getThumbnailList(EImageSize.D);
		thumbnailsE = imageIO.getThumbnailList(EImageSize.E);
		thumbnailsF = imageIO.getThumbnailList(EImageSize.F);
		thumbnailsG = imageIO.getThumbnailList(EImageSize.G);
		thumbnailsH = imageIO.getThumbnailList(EImageSize.H);
		thumbnailsI = imageIO.getThumbnailList(EImageSize.I);
	}
	
	private void initGestureDetection() {
		// get the display size
		Display display = getWindowManager().getDefaultDisplay();
		displaySize = new Point();
		display.getSize(displaySize);
		
		// init new gesture detector
		gestureDetector = new GestureDetector(this, new EthanolGestureDetector(this, displaySize));
	}
	
	private void initViews() {
		// create an imageView for every thumbnail:
    	imageViews = new ImageView[thumbnailFiles.size()];
    	
    	for (int i = 0; i < imageViews.length; i++) {
    		imageViews[i] = addViews(i);
    	}
	}
	
	private ImageView addViews(int viewId) {
		// create a new image view with the given id
		ImageView iv = new ImageView(this);
		iv.setId(viewId);
		
		return iv;
	}
	
	@Override
	public void skipToImage(EDirection direction, int interval) {
		// if we have a fixed image disable fast swipe,
		// i.e. make a only short swipe
		if (Math.abs(interval) == 2 && fixedImage != null) {
			interval *= 0.5;
		}
		
		switch (direction) {
			case PREVIOUS:
				// skip to a previous picture with the given interval
				currentThumbnailNo -= interval;
				break;
			case NEXT:
				// skip to a next picture with the given interval
				currentThumbnailNo += interval;
				break;
			default:
				// do nothing
				return;
		}
		
		// update the screen
		updateImageViews();
	}
	
	@Override
	public void skipToImageFromRow(int row, int percent) {
		int thumbsInRow;
		
		// which row to calculate from?
		switch (row) {
			case Values.HORIZONTAL_TOP:
				// we have ... thumbnails in the top row
				thumbsInRow = currentThumbnailNo - 2;
				// determine the current thumbnail number
				currentThumbnailNo = Math.round((thumbsInRow * percent) / 100);
				break;
			case Values.HORIZONTAL_BOTTOM:
				// we have ... thumbnails in the bottom row
				thumbsInRow = thumbnailFiles.size() - currentThumbnailNo - 2;
				// determine the current thumbnail number
				currentThumbnailNo = currentThumbnailNo + 2 + Math.round((thumbsInRow * percent) / 100);
				break;
			default:
				// do nothing
				return;
		}
		
		// update the screen
		updateImageViews();
	}
		
	public void updateImageViews() {		
		// check image boundaries
		if (currentThumbnailNo < 0) {
			currentThumbnailNo = 0;
		} else if (currentThumbnailNo > (imageViews.length - 1)) {
			currentThumbnailNo = (imageViews.length - 1);
		}
		
		// first remove all image views
		((LinearLayout) findViewById(R.id.row_top)).removeAllViews();
		((LinearLayout) findViewById(R.id.main_section)).removeAllViews();
		((LinearLayout) findViewById(R.id.row_bottom)).removeAllViews();
		
		// now reset the image views with the right thumbnails
		
		// some configuration values
		int layoutId;
		Bitmap thumbnail;
		int offsetToBottomRow = fixedImage == null ? 2 : 1;
		boolean passedFixedImage = false;

		// get the right parameters and set the thumbnails
		for (int i = 0; i < thumbnailFiles.size(); i++) {
			
			// 1) already view thumbnails in the top row
			if (i < (currentThumbnailNo - 1)) {
				layoutId = R.id.row_top;
				thumbnail = getBitmapWithSize(i, getSizeForThumbnail(currentThumbnailNo - 2, (thumbnailFiles.size() - i)));
				
			// 2) thumbnails in the main section
			} else if (i < (currentThumbnailNo + offsetToBottomRow)) {
				layoutId = R.id.main_section;
				
				// the current image is in the center
				if (i == currentThumbnailNo) {
					// there is no fixed thumbnail -> display the current one in the center
					if (fixedImage == null) {
						thumbnail = getBitmapWithSize(i, EImageSize.A);
						
					// display the fixed thumbnail in the center and the succesor on the right
					} else {
						addImageViewToLayout(layoutId, imageViews[i], imageIO.getThumbnail(fixedImage.getName(), EImageSize.A), true);
						passedFixedImage = true;
						
						thumbnail = getBitmapWithSize(i, EImageSize.B);
					}
				
				// the thumbnails on the left and the right side
				} else {
					thumbnail = getBitmapWithSize(i, EImageSize.B);
					
					// if the last thumbnail is fixed display the predecessor on the left and the fixed thumbnail in the center
					if ((i >= (thumbnailFiles.size() - 1)) && (fixedImage != null)) {
						addImageViewToLayout(layoutId, imageViews[i], thumbnail, false);
						addImageViewToLayout(layoutId, imageViews[i + 1], imageIO.getThumbnail(fixedImage.getName(), EImageSize.A), true);
						
						// everything is done for now, so skip the rest of the cycle
						continue;
					}
				}
				
			// 3) the rest are upcoming thumbnails in the bottom row
			} else {
				layoutId = R.id.row_bottom;
				thumbnail = getBitmapWithSize(i, getSizeForThumbnail((thumbnailFiles.size() - (currentThumbnailNo + 2)), (i + 1)));
			}
			
			// finally add the image to the view
			// if we passed the fixed image skip one image view position for it
			if (passedFixedImage) {
				if (i < (imageViews.length - 1)) {
					addImageViewToLayout(layoutId, imageViews[i + 1], thumbnail, false);
				}
			} else {
				addImageViewToLayout(layoutId, imageViews[i], thumbnail, false);
			}
		}
	}

	private void addImageViewToLayout(int layoutId, ImageView iv, Bitmap bm, boolean highlightImage) {
		// highlight the thumbnail
		if (highlightImage) {
			iv.setPadding(Values.IMAGE_PADDING, Values.IMAGE_HIGHLIGHT_WIDTH, Values.IMAGE_PADDING, Values.IMAGE_HIGHLIGHT_WIDTH);
			iv.setBackgroundResource(R.layout.highlight);
		
		// reset default values
		} else {
			iv.setPadding(Values.IMAGE_PADDING, 0, Values.IMAGE_PADDING, 0);
			iv.setBackgroundColor(Values.IMAGE_BACKGROUND_COLOR);
		}
	
		// add the thumbnail to the image view
		iv.setImageBitmap(bm);
		// add the image view to the layout
		((LinearLayout) findViewById(layoutId)).addView(iv);
	}
	
	private EImageSize getSizeForThumbnail(int thumbnailsToDisplayLeft, int thumbnailNo) {
		int startThumbnail = currentThumbnailNo + 2;
		
		// max display images
		if (thumbnailsToDisplayLeft <= 5) {
			return EImageSize.C;
			
		} else if (thumbnailsToDisplayLeft == 6) {
			if (thumbnailNo < (startThumbnail + 3)) {
				return EImageSize.C;
			} else {
				return EImageSize.D;
			}
		} else if (thumbnailsToDisplayLeft == 7) {
			if (thumbnailNo < (startThumbnail + 2)) {
				return EImageSize.C;
			} else if (thumbnailNo < (startThumbnail + 4)) {
				return EImageSize.D;
			} else {
				return EImageSize.E;
			}
			
		} else if (thumbnailsToDisplayLeft == 8) {
			if (thumbnailNo < (startThumbnail + 1)) {
				return EImageSize.C;
			} else if (thumbnailNo < (startThumbnail + 3)) {
				return EImageSize.D;
			} else if (thumbnailNo < (startThumbnail + 6)) {
				return EImageSize.E;
			} else {
				return EImageSize.F;
			}
			
		} else if (thumbnailsToDisplayLeft == 9) {
			if (thumbnailNo < (startThumbnail + 1)) {
				return EImageSize.C;
			} else if (thumbnailNo < (startThumbnail + 3)) {
				return EImageSize.D;
			} else if (thumbnailNo < (startThumbnail + 6)) {
				return EImageSize.E;
			} else if (thumbnailNo < (startThumbnail + 7)) {
				return EImageSize.F;
			} else {
				return EImageSize.G;
			}
			
		} else if (thumbnailsToDisplayLeft == 10) {
			if (thumbnailNo < (startThumbnail + 1)) {
				return EImageSize.C;
			} else if (thumbnailNo < (startThumbnail + 3)) {
				return EImageSize.D;
			} else if (thumbnailNo < (startThumbnail + 6)) {
				return EImageSize.E;
			} else if (thumbnailNo < (startThumbnail + 7)) {
				return EImageSize.F;
			} else if (thumbnailNo < (startThumbnail + 8)) {
				return EImageSize.G;
			} else {
				return EImageSize.H;
			}
			
			//TODO
		} else if (thumbnailsToDisplayLeft == 11) {
			if (thumbnailNo < (startThumbnail + 1)) {
				return EImageSize.C;
			} else if (thumbnailNo < (startThumbnail + 2)) {
				return EImageSize.D;
			} else if (thumbnailNo < (startThumbnail + 6)) {
				return EImageSize.E;
			} else if (thumbnailNo < (startThumbnail + 7)) {
				return EImageSize.F;
			} else if (thumbnailNo < (startThumbnail + 8)) {
				return EImageSize.G;
			} else {
				return EImageSize.H;
			}
		
		} else if (thumbnailsToDisplayLeft <= 15) {
			if (thumbnailNo < (startThumbnail + 1)) {
				return EImageSize.C;
			} else if (thumbnailNo < (startThumbnail + 2)) {
				return EImageSize.D;
			} else if (thumbnailNo < (startThumbnail + 5)) {
				return EImageSize.E;
			} else if (thumbnailNo < (startThumbnail + 8)) {
				return EImageSize.F;
			} else if (thumbnailNo < (startThumbnail + 10)) {
				return EImageSize.G;
			} else {
				return EImageSize.H;
			}
			
		} else if (thumbnailsToDisplayLeft <= 25) {
			if (thumbnailNo < (startThumbnail + 1)) {
				return EImageSize.C;
			} else if (thumbnailNo < (startThumbnail + 2)) {
				return EImageSize.D;
			} else if (thumbnailNo < (startThumbnail + 4)) {
				return EImageSize.E;
			} else if (thumbnailNo < (startThumbnail + 6)) {
				return EImageSize.F;
			} else if (thumbnailNo < (startThumbnail + 8)) {
				return EImageSize.G;
			} else {
				return EImageSize.H;
			}
			
		} else if (thumbnailsToDisplayLeft <= 40) {
			if (thumbnailNo < (startThumbnail + 1)) {
				return EImageSize.C;
			} else if (thumbnailNo < (startThumbnail + 2)) {
				return EImageSize.D;
			} else if (thumbnailNo < (startThumbnail + 3)) {
				return EImageSize.E;
			} else if (thumbnailNo < (startThumbnail + 4)) {
				return EImageSize.F;
			} else if (thumbnailNo < (startThumbnail + 6)) {
				return EImageSize.G;
			} else {
				return EImageSize.H;
			}
			
			
		} else if (thumbnailsToDisplayLeft <= 50) {
			if (thumbnailNo < (startThumbnail + 1)) {
				return EImageSize.C;
			} else if (thumbnailNo < (startThumbnail + 2)) {
				return EImageSize.D;
			} else if (thumbnailNo < (startThumbnail + 3)) {
				return EImageSize.E;
			} else if (thumbnailNo < (startThumbnail + 4)) {
				return EImageSize.F;
			} else if (thumbnailNo < (startThumbnail + 5)) {
				return EImageSize.G;
			} else {
				return EImageSize.H;
			}
			
		// 
		} else if (thumbnailsToDisplayLeft <= 65) {
			if (thumbnailNo < (startThumbnail + 1)) {
				return EImageSize.C;
			} else if (thumbnailNo < (startThumbnail + 2)) {
				return EImageSize.D;
			} else if (thumbnailNo < (startThumbnail + 3)) {
				return EImageSize.E;
			} else if (thumbnailNo < (startThumbnail + 4)) {
				return EImageSize.F;
			} else if (thumbnailNo < (startThumbnail + 8)) {
				return EImageSize.G;
			} else if (thumbnailNo < (startThumbnail + 12)) {
				return EImageSize.H;
			} else {
				return EImageSize.I;
			}
			
		// if there are more than 65 pictures left to display
		} else {
			if (thumbnailNo == (currentThumbnailNo + 2)) {
				return EImageSize.D;
			} else if (thumbnailNo == (currentThumbnailNo + 4)) {
				return EImageSize.E;
			} else if (thumbnailNo < (currentThumbnailNo + 6)) {
				return EImageSize.F;
			} else if (thumbnailNo < (currentThumbnailNo + 8)) {
				return EImageSize.G;
			} else if (thumbnailNo < (currentThumbnailNo + 10)) {
				return EImageSize.H;
			} else {
				return EImageSize.I;
			}
		}
	}
	
	private Bitmap getBitmapWithSize(int thumbnailNumber, EImageSize thumbnailSize) {
		// return the thumbnail from the file system or from a list with the given number and size
		switch (thumbnailSize) {
			case A:
				return imageIO.getThumbnail(thumbnailFiles.get(thumbnailNumber).getName(), EImageSize.A);
			case B:
				return imageIO.getThumbnail(thumbnailFiles.get(thumbnailNumber).getName(), EImageSize.B);
			case C:
				return imageIO.getThumbnail(thumbnailFiles.get(thumbnailNumber).getName(), EImageSize.C);
			case D:
				return thumbnailsD.get(thumbnailNumber);
			case E:
				return thumbnailsE.get(thumbnailNumber);
			case F:
				return thumbnailsF.get(thumbnailNumber);
			case G:
				return thumbnailsG.get(thumbnailNumber);
			case H:
				return thumbnailsH.get(thumbnailNumber);
			case I:
				return thumbnailsI.get(thumbnailNumber);
			default:
				return null;
		}
	}

	@Override
	public void fixOrReleaseImage() {
		// first remove all thumbnails from the main section and redraw the in the correct order
		((LinearLayout) findViewById(R.id.main_section)).removeAllViews();
		// set the image in the right
		if (currentThumbnailNo > 0) {
			addImageViewToLayout(R.id.main_section, imageViews[(currentThumbnailNo - 1)], getBitmapWithSize((currentThumbnailNo - 1), EImageSize.B), false);
		}
		
		// no image fixed - fix the current one
		if (fixedImage == null) {
			// set the fixed thumbnail
			fixedImage = thumbnailFiles.get(currentThumbnailNo);
			// and remove it from all lists
			removeThumbnailFromListsAtLocation(currentThumbnailNo);

			// set and highlight the fixed thumbnail
			addImageViewToLayout(R.id.main_section, imageViews[currentThumbnailNo], imageIO.getThumbnail(fixedImage.getName(), EImageSize.A), true);
			// finally set the image on the left
			// (btw. it's the current picture because we already removed the fixed one from the lists)
			if (currentThumbnailNo < thumbnailFiles.size()) {
				addImageViewToLayout(R.id.main_section, imageViews[(currentThumbnailNo + 1)], getBitmapWithSize((currentThumbnailNo), EImageSize.B), false);
			}
			
		// store the fixed image at the current position and release it
		} else {
			// insert the fixed thumbnail into all lists at the current position,
			insertThumbnailIntoListsAtLocation(currentThumbnailNo, fixedImage);
			
			// set the released thumbnail and remove the highlighting
			addImageViewToLayout(R.id.main_section, imageViews[currentThumbnailNo], imageIO.getThumbnail(fixedImage.getName(), EImageSize.A), false);
			// finally set the image on the left
			if (currentThumbnailNo < (thumbnailFiles.size() - 1)) {
				addImageViewToLayout(R.id.main_section, imageViews[(currentThumbnailNo + 1)], getBitmapWithSize((currentThumbnailNo + 1), EImageSize.B), false);
			}
			
			// and release the fixed thumbnail
			fixedImage = null;
		}
	}
	
	private void removeThumbnailFromListsAtLocation(int location) {
		// remove a thumbnail at the given location from all lists
		thumbnailFiles.remove(location);
		thumbnailsD.remove(location);
		thumbnailsE.remove(location);
		thumbnailsF.remove(location);
		thumbnailsG.remove(location);
		thumbnailsH.remove(location);
		thumbnailsI.remove(location);
	}
	
	private void insertThumbnailIntoListsAtLocation(int location, File thumbnail) {
		// insert a thumbnail at the given location into all lists
		thumbnailFiles.add(location, thumbnail);
		thumbnailsD.add(location, imageIO.getThumbnail(thumbnail.getName(), EImageSize.D));
		thumbnailsE.add(location, imageIO.getThumbnail(thumbnail.getName(), EImageSize.E));
		thumbnailsF.add(location, imageIO.getThumbnail(thumbnail.getName(), EImageSize.F));
		thumbnailsG.add(location, imageIO.getThumbnail(thumbnail.getName(), EImageSize.G));
		thumbnailsH.add(location, imageIO.getThumbnail(thumbnail.getName(), EImageSize.H));
		thumbnailsI.add(location, imageIO.getThumbnail(thumbnail.getName(), EImageSize.I));
	}
	
	@Override
	public void startExternalProgram(EProgram program) {
		// TODO start various programs here
		switch (program) {
			case PROG_1:
				makeToast("Start Program 1");	
				break;
			case PROG_2:
				makeToast("Start Program 2");	
				break;
			default:
				;
		}
	}
	
	private void makeToast(String msg) {
		// displays a Toast on the screen
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}
}