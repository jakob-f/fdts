package at.ac.tuwien.media;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import at.ac.tuwien.media.io.file.Configuration;
import at.ac.tuwien.media.io.file.EthanolFileChooser;
import at.ac.tuwien.media.io.file.EThumbnailType;
import at.ac.tuwien.media.io.file.FileIO;
import at.ac.tuwien.media.io.gesture.EthanolGestureDetector;
import at.ac.tuwien.media.util.EthanolLogger;
import at.ac.tuwien.media.util.Value;
import at.ac.tuwien.media.util.Value.EDirection;
import at.ac.tuwien.media.util.exception.EthanolException;

/**
 * {@link Ethanol} class implements the main activity for the Ethanol-App.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class Ethanol extends Activity implements IEthanol {
	// gesture detection
	private EthanolGestureDetector egd;
	private GestureDetector gestureDetector;

	// file io and thumbnails
	private FileIO io;
	private int currentThumbnailNo = -1;
	private File fixedThumbnail = null;
	private List<File> thumbnailFiles;
	private List<Bitmap> thumbnailsD;
	private List<Bitmap> thumbnailsE;
	private List<Bitmap> thumbnailsF;
	private List<Bitmap> thumbnailsG;
	private List<Bitmap> thumbnailsH;
	private List<Bitmap> thumbnailsI;
	
	// views
	private int displayWidth;
	private ImageView[] imageViews;
	private List<EThumbnailType> thumbnailSizesTopRow;
	private List<EThumbnailType> thumbnailSizesBottomRow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// initialize the logger
		EthanolLogger.setParent(this);
		
		// create root folder if not exists
		new File(Value.ETHANOL_ROOT_FOLDER).mkdirs();
		
		// display a loader while loading and resizing the thumbnails
		new LoaderTask().execute();
	}
	
    private class LoaderTask extends AsyncTask<Void, Integer, Void> {
    	private ProgressDialog pd; 
    	
        @Override  
        protected void onPreExecute() {  
            // create a new progress dialog  
            pd = new ProgressDialog(Ethanol.this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setTitle(Value.LOADER_TITLE);
            pd.setMessage(Value.LOADER_MESSAGE);
            pd.setCancelable(false);
            pd.setIndeterminate(false);
            
            // and display it
            pd.show();  
        }  
  
        @Override
        protected Void doInBackground(Void... arg0) {
        	try {
    			// (resize and) load thumbnails in background
    	        loadThumbnails();
    		} catch (EthanolException ee) {
    			EthanolLogger.addDebugMessage("Cannot start Ethanol correctly: " + ee.getMessage());
    		}
    		
            return null;  
        }
  
        @Override  
        protected void onPostExecute(Void result) {
	        // initialize the main view  
	        setContentView(R.layout.activity_ethanol);  
	            
	        // initialize the gesture detection
	        initGestureDetection();
		        
		    // start only if there are files to display
	        if (thumbnailFiles.isEmpty()) {
	        	initDefaultView();
	        } else {
		        // add view items
		        initViews();
		        
		        // load first image
		        skipToThumbnail(EDirection.FORWARD, 1);
	        }
	        
	        // ... and close the progress dialog  
            pd.dismiss();
            
            // after startup display a debug message
            EthanolLogger.displayDebugMessage();
        }  
    } 
	
	@Override
	public boolean onTouchEvent(final MotionEvent me) {
		// disable if no thmubnails are available to display
		if (!thumbnailFiles.isEmpty()) {
			// save the start time of this operation for the debug message
			long overallOpTime = System.currentTimeMillis();
			
			// TODO: for future implementations read the return value
			// (yet not returned by GestureDetector) of onTouchEvent
			// if true is returned use it to end the method
			
			// try to perform a touch event
			gestureDetector.onTouchEvent(me);
			
			// save the current down event
			if (me.getAction() == MotionEvent.ACTION_DOWN) {
				egd.setDownEvent(me);
				
			// try to perform a swipe event
			} else if (me.getAction() == MotionEvent.ACTION_UP) {
				egd.onSwipe(null, me);
				
				// add the debug message with the right time
				EthanolLogger.setOpStartTime(overallOpTime);
				EthanolLogger.addDebugMessageWithOpTime("Performing this action took:");
				
				// after an up event a motion is completed
				// time to display a debug message
				EthanolLogger.displayDebugMessage();
			}
	
			// the event is consumed
			return true;
		}
		
		//show default view
		initDefaultView();
		EthanolLogger.displayDebugMessage("No thumbnails available - nothing to do");
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.layout.menu, menu);
		
		return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
	        case R.id.menu_save:
	            Toast.makeText(Ethanol.this, "Save is Selected", Toast.LENGTH_SHORT).show();
	            return true;
	 
	        case R.id.menu_search:
	        	new EthanolFileChooser(this, Configuration.get(Value.CONFIG_IMAGE_FOLDER));
	            return true;
	 
	        case R.id.menu_settings:
	        	 // Display the fragment as the main content.
	            startActivity(new Intent(this, EthanolPreferences.class));
	            EthanolPreferences.setParent(this);
	        	
	            return true;
	 
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
    
	private void loadThumbnails() throws EthanolException {
		// save the start time of this operation for the debug message
		EthanolLogger.saveCurrentTime();

		// load images from sdCard
		// create thumbnails if needed
		io = new FileIO();
		thumbnailFiles = io.loadThumbnails();

//		// since they are the biggest files, thumbnail sizes A - C are loaded directly,
//		// for performance issues thumbnail sizes D - I are cached
		thumbnailsD = io.getThumbnailList(EThumbnailType.D);
		thumbnailsE = io.getThumbnailList(EThumbnailType.E);
		thumbnailsF = io.getThumbnailList(EThumbnailType.F);
		thumbnailsG = io.getThumbnailList(EThumbnailType.G);
		thumbnailsH = io.getThumbnailList(EThumbnailType.H);
		thumbnailsI = io.getThumbnailList(EThumbnailType.I);
		
		// add the debug message
		EthanolLogger.addDebugMessageWithOpTime("Loading all thumbnails took:");
	}
	
	private void initGestureDetection() {
		// get the display size
		Display display = getWindowManager().getDefaultDisplay();
		Point displaySize = new Point();
		display.getSize(displaySize);
		
		// init new gesture detector
		egd = new EthanolGestureDetector(this, displaySize);
		gestureDetector = new GestureDetector(this, egd);
		displayWidth = displaySize.x;
	}
	
	private void initViews() {
		// create an imageView for every thumbnail:
    	imageViews = new ImageView[thumbnailFiles.size()];
    	
    	for (int i = 0; i < imageViews.length; i++) {
    		imageViews[i] = newView(i);
    	}
	}
	
	private void initDefaultView() {
		// delete old view
		removeAllViewsFromViewGroup(R.id.main_section_center);
		
		// create an imageView
    	final ImageView iv = newView(0);
    	
    	// set values
    	// set background
    	iv.setBackgroundColor(Value.THUMBNAIL_BACKGROUND_COLOR);
		// add the drawable to the image view
		iv.setImageDrawable(getResources().getDrawable(R.drawable.im_open_images));
		// add the image view to the layout
		((LinearLayout) findViewById(R.id.main_section_center)).addView(iv);
		
		// show options menu
		openOptionsMenu();
	}
	
	private ImageView newView(final int viewId) {
		// create a new image view with the given id
		ImageView iv = new ImageView(this);
		iv.setId(viewId);
		
		return iv;
	}
	
	@Override
	public void skipToThumbnail(final EDirection direction, int interval) {
		// if we have a fixed image disable fast swipe, i.e. make a only short swipe
		// all other swipes will be shortened by one
		if (interval > 1 && fixedThumbnail != null) {
			interval -= 1;
		}
		
		switch (direction) {
			case PREVIOUS:
				// skip to a previous picture with the given interval
				currentThumbnailNo -= interval;
				break;
			case FORWARD:
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
	public void skipToThumbnailFromRow(final int row, final int percent) {
		int pixelsUsed;
		int pixelPercentage = (displayWidth * percent) / 100;
		
		// which row to calculate from?
		switch (row) {
			// swipe from the upper row
			case Value.HORIZONTAL_TOP:
				// start at the right edge - this compensates blank spaces on the left 
				pixelsUsed = displayWidth;
				
				// go though the size list in reverse order
				for (int i = (thumbnailSizesTopRow.size() - 1); i >= 0; i--) {
					// subtract each thumbnail from the display width
					pixelsUsed -= thumbnailSizesTopRow.get(i).getTotalWidth();
					
					// determine if we reached the thumbnail with the pixels percentage
					// and set the current thumbnail number
					if (pixelsUsed <= pixelPercentage) {
						currentThumbnailNo = i;
						
						break;
					}
				}
			
				break;
			
			// swipe from the lower row
			case Value.HORIZONTAL_BOTTOM:
				// start at the left edge - this compensates blank spaces on the right 
				pixelsUsed = 0;
				
				// go though the size list in normal order
				for (int i = 0; i < thumbnailSizesBottomRow.size(); i++) {
					// add each thumbnail to the total width
					pixelsUsed += thumbnailSizesBottomRow.get(i).getTotalWidth();
					
					// determine if we reached the thumbnail with the pixels percentage
					// and set the current thumbnail number
					if (pixelsUsed >= pixelPercentage) {
						currentThumbnailNo += i + 2;
						
						break;
					}
				}
				
				break;
				
			default:
				// do nothing
				return;
		}
		
		// update the screen
		updateImageViews();
	}	
		
	private void updateImageViews() {
		// check image boundaries
		if (currentThumbnailNo < 0) {
			currentThumbnailNo = 0;
		} else if (currentThumbnailNo > (imageViews.length - 1)) {
			currentThumbnailNo = (imageViews.length - 1);
		}
		
		// first remove all image views
		removeAllViewsFromViewGroup(R.id.row_top);
		removeAllViewsFromViewGroup(R.id.main_section_left);
		removeAllViewsFromViewGroup(R.id.main_section_center);
		removeAllViewsFromViewGroup(R.id.main_section_right);
		removeAllViewsFromViewGroup(R.id.row_bottom);
		
		// now reset the image views with the right thumbnails
		// some configuration values
		int layoutId;
		EThumbnailType thumbnailType;
		int offsetToBottomRow = (fixedThumbnail == null) ? 2 : 1;
		boolean passedFixedImage = false;
		
		// save the start time of this operation for the debug message
		EthanolLogger.saveCurrentTime();
		
		thumbnailSizesTopRow = calculateThumbnailSizes(Math.max((currentThumbnailNo - 1), 0), true);
		thumbnailSizesBottomRow = calculateThumbnailSizes((thumbnailFiles.size() - (currentThumbnailNo + offsetToBottomRow)), false);

		// add the debug message
		EthanolLogger.addDebugMessageWithOpTime("Calculating image positions took:");
		
		// save the start time of this operation for the debug message
		EthanolLogger.saveCurrentTime();
		
		// get the right parameters and set the thumbnails
		for (int i = 0; i < thumbnailFiles.size(); i++) {
			
			// 1) already view thumbnails in the top row
			if (i < (currentThumbnailNo - 1)) {
				layoutId = R.id.row_top;
				thumbnailType = EThumbnailType.I;
				thumbnailType = thumbnailSizesTopRow.get(i);
				
			// 2) there are three (or at least two) thumbnails in the main section
			} else if (i < (currentThumbnailNo + offsetToBottomRow)) {
				// the current thumbnail is in the center
				if (i == currentThumbnailNo) {
					layoutId = R.id.main_section_center;
					
					// there is no fixed thumbnail -> display the current one in the center
					if (fixedThumbnail == null) {
						thumbnailType = EThumbnailType.A;
						
					// display the fixed thumbnail in the center and the successor on the right
					} else {
						addImageViewToLayout(layoutId, imageViews[i], io.getThumbnail(fixedThumbnail.getName(), EThumbnailType.A), EThumbnailType.A, true);
						passedFixedImage = true;
						
						thumbnailType = EThumbnailType.B;
						layoutId = R.id.main_section_right;
					}
				
				// the thumbnails on the left and the right side
				} else {
					if (i < currentThumbnailNo) {
						layoutId = R.id.main_section_left;
					} else {
						layoutId = R.id.main_section_right;
					}
					
					thumbnailType = EThumbnailType.B;
					
					// if the last thumbnail is fixed display the predecessor on the left and the fixed thumbnail in the center
					if ((i >= (thumbnailFiles.size() - 1)) && (fixedThumbnail != null)) {
						addImageViewToLayout(layoutId, imageViews[i], getBitmapWithSize(i, thumbnailType), thumbnailType, false);
						addImageViewToLayout(R.id.main_section_center, imageViews[i + 1], io.getThumbnail(fixedThumbnail.getName(), EThumbnailType.A), EThumbnailType.A, true);
						
						// everything is done for now, so skip the rest of the cycle
						continue;
					}
				}
				
			// 3) the remaining ones are upcoming thumbnails in the bottom row
			} else {
				layoutId = R.id.row_bottom;
				thumbnailType = thumbnailSizesBottomRow.get(i - (currentThumbnailNo + offsetToBottomRow));
			}
			
			// finally add the image to the view
			// if we passed the fixed image skip one image view position for it
			if (passedFixedImage) {
				if (i < (imageViews.length - 1)) {
					addImageViewToLayout(layoutId, imageViews[i + 1], getBitmapWithSize(i, thumbnailType), thumbnailType, false);
				}
			} else {
				addImageViewToLayout(layoutId, imageViews[i], getBitmapWithSize(i, thumbnailType), thumbnailType, false);
			}
		}
		
		// add the debug message
		EthanolLogger.addDebugMessageWithOpTime("Placing all thumbnails took:");
	}
	
	private void removeAllViewsFromViewGroup(final int id) {
		// removes all views from a given view group
		((ViewGroup) findViewById(id)).removeAllViews();
	}

	private void addImageViewToLayout(final int layoutId, final ImageView iv, final Bitmap bm, final EThumbnailType thumbnailType, final boolean highlightImage) {
		// highlight the thumbnail
		if (highlightImage) {
			iv.setBackgroundResource(R.layout.highlight);
		// reset default backgorund color
		} else {
			iv.setBackgroundColor(Value.THUMBNAIL_BACKGROUND_COLOR);
		}
			
		// set padding
		iv.setPadding(thumbnailType.getPaddingLeft(), thumbnailType.getPaddingTop(), thumbnailType.getPaddingRight(), thumbnailType.getPaddingBottom());
		
		// add the thumbnail to the image view
		iv.setImageBitmap(bm);
		// add the image view to the layout
		((LinearLayout) findViewById(layoutId)).addView(iv);
	}
	
	private List<EThumbnailType> calculateThumbnailSizes(final int thumbnailsToDisplay, final boolean reverse) {
		List<EThumbnailType> thumbnailTypes = new ArrayList<EThumbnailType>();
		
		int pixelsUsed = 0;
		// start with thumbnail type C
		EThumbnailType currentThumbnailType = EThumbnailType.C;
		
		// calculate every thumbnail size
		for (int i = 0; i < thumbnailsToDisplay; i++) {
			// simple case: we have enough space so we just add the thumbnail with the given size
			if ((pixelsUsed + currentThumbnailType.getTotalWidth()) <= displayWidth) {
				// add new thumbnail with size
				thumbnailTypes.add(currentThumbnailType);
				
				// calculate pixels used
				pixelsUsed += currentThumbnailType.getTotalWidth();
			
			// not enough room left: split one thumbnail and replace it with two smaller ones
			} else {
				// check how many different thumbnail types we have
				// if we have at least three different types, split a bigger one in the middle
				// this will give us a fluent image gallery with smoothly increasing thumbnail sizes
				if (getNumberOfDifferentThumbnailTypes(thumbnailTypes) > 2) {
					// if we have at least more than two thumbnails present which can split - split them
					// two smaller one will always fit into a bigger one - additional space can be left open
					if (getTotalNumberOfReferencedThumbnailType(thumbnailTypes, currentThumbnailType.getNextBiggerThumbnailType()) > 2) {
						// search for the first thumbnail which is bigger and split it into two new ones of the current type
						int firstBiggerThumbnail = thumbnailTypes.lastIndexOf(currentThumbnailType.getNextBiggerThumbnailType());
						
						thumbnailTypes.set(firstBiggerThumbnail, currentThumbnailType);
						thumbnailTypes.add((firstBiggerThumbnail + 1), currentThumbnailType);
						
						// calculate pixels used
						pixelsUsed += 2 * currentThumbnailType.getTotalWidth() - currentThumbnailType.getNextBiggerThumbnailType().getTotalWidth();
						
					// else search for the next next bigger one and split it with two bigger ones
					// so we have a bigger one to split in the next round
					} else {
						// search the next next bigger one and replace it with two bigger ones
						int firstBiggerBiggerThumbnail = thumbnailTypes.lastIndexOf(currentThumbnailType.getNextBiggerThumbnailType().getNextBiggerThumbnailType());
						
						thumbnailTypes.set(firstBiggerBiggerThumbnail, currentThumbnailType.getNextBiggerThumbnailType());
						thumbnailTypes.add((firstBiggerBiggerThumbnail + 1), currentThumbnailType.getNextBiggerThumbnailType());
						
						// calculate pixels used
						pixelsUsed += 2 * currentThumbnailType.getNextBiggerThumbnailType().getTotalWidth()
								- currentThumbnailType.getNextBiggerThumbnailType().getNextBiggerThumbnailType().getTotalWidth();
					}
					
				// we have only two or less thumbnails types - so just split the last position
				} else {
					// split the last thumbnail and add two thumbnails with the next smaller size to the end of the list
					currentThumbnailType = currentThumbnailType.getNextSmallerThumbnailType();
					
					thumbnailTypes.set((thumbnailTypes.size() - 1), currentThumbnailType);
					thumbnailTypes.add(currentThumbnailType);
					
					// calculate pixels used
					pixelsUsed += 2 * currentThumbnailType.getTotalWidth() - currentThumbnailType.getNextBiggerThumbnailType().getTotalWidth();
				}
				
				// just a check for further debugging
				if (pixelsUsed > displayWidth) {
					EthanolLogger.displayDebugMessage("ALAS, THIS SHOULD NOT HAVE HAPPENED!");
				}
			}
		}
		
		// reverse the list for the upper row if needed
		if (reverse) {
			 Collections.reverse(thumbnailTypes);
			 
			// add the debug message for the upper row
			// and calculate the space left in the row in pixels
			 EthanolLogger.addDebugMessage("Pixel error in upper row: " + (displayWidth - pixelsUsed) + "px");
		} else {
			// add the debug message for the bottom row
			// and calculate the space left in the row in pixels
			EthanolLogger.addDebugMessage("Pixel error in bottom row: " + (displayWidth - pixelsUsed) + "px");
		}
		
		return thumbnailTypes;
	}
	
	private int getTotalNumberOfReferencedThumbnailType(final List<EThumbnailType> thumbnailTypes, final EThumbnailType refType) {
		// return the total number of the occurrence of the referenced thumbnail type in the list
		return Collections.frequency(thumbnailTypes, refType);
	}
	
	private int getNumberOfDifferentThumbnailTypes(final List<EThumbnailType> thumbnailTypes) {
		// return the number of different thumbnail types in the list
		return new HashSet<EThumbnailType>(thumbnailTypes).size();
	}
	
	@Override
	public void fixOrReleaseCurrentThumbnail() {
		// first remove all thumbnails from the main section and redraw the in the correct order
		removeAllViewsFromViewGroup(R.id.main_section_left);
		removeAllViewsFromViewGroup(R.id.main_section_center);
		removeAllViewsFromViewGroup(R.id.main_section_right);
		
		// then set the image in the left
		if (currentThumbnailNo > 0) {
			addImageViewToLayout(R.id.main_section_left, imageViews[(currentThumbnailNo - 1)], getBitmapWithSize((currentThumbnailNo - 1), EThumbnailType.B), EThumbnailType.B, false);
		}
		
		// no image fixed - fix the current one
		if (fixedThumbnail == null) {
			// set the fixed thumbnail
			fixedThumbnail = thumbnailFiles.get(currentThumbnailNo);
			// and remove it from all lists
			removeThumbnailFromListsAtLocation(currentThumbnailNo);

			// set and highlight the fixed thumbnail
			addImageViewToLayout(R.id.main_section_center, imageViews[currentThumbnailNo], io.getThumbnail(fixedThumbnail.getName(), EThumbnailType.A), EThumbnailType.A, true);
			// finally set the image on the right
			// (btw. it's the current picture because we already removed the fixed one from the lists)
			if (currentThumbnailNo < thumbnailFiles.size()) {
				addImageViewToLayout(R.id.main_section_right, imageViews[(currentThumbnailNo + 1)], getBitmapWithSize((currentThumbnailNo), EThumbnailType.B), EThumbnailType.B, false);
			}
			
		// store the fixed image at the current position and release it
		} else {
			// insert the fixed thumbnail into all lists at the current position,
			insertThumbnailIntoListsAtLocation(currentThumbnailNo, fixedThumbnail);
			
			// set the released thumbnail and remove the highlighting
			addImageViewToLayout(R.id.main_section_center, imageViews[currentThumbnailNo], io.getThumbnail(fixedThumbnail.getName(), EThumbnailType.A), EThumbnailType.A, false);
			// and set the image on the right
			if (currentThumbnailNo < (thumbnailFiles.size() - 1)) {
				addImageViewToLayout(R.id.main_section_right, imageViews[(currentThumbnailNo + 1)], getBitmapWithSize((currentThumbnailNo + 1), EThumbnailType.B), EThumbnailType.B, false);
			}
			
			// finally release the fixed thumbnail
			fixedThumbnail = null;
		}
	}
	
	private Bitmap getBitmapWithSize(final int thumbnailNumber, final EThumbnailType thumbnailType) {
		// return the thumbnail from the file system or from a list with the given number and size
		switch (thumbnailType) {
			case A:
				return io.getThumbnail(thumbnailFiles.get(thumbnailNumber).getName(), EThumbnailType.A);
			case B:
				return io.getThumbnail(thumbnailFiles.get(thumbnailNumber).getName(), EThumbnailType.B);
			case C:
				return io.getThumbnail(thumbnailFiles.get(thumbnailNumber).getName(), EThumbnailType.C);
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
	
	private void removeThumbnailFromListsAtLocation(final int location) {
		// remove a thumbnail at the given location from all lists
		thumbnailFiles.remove(location);
		thumbnailsD.remove(location);
		thumbnailsE.remove(location);
		thumbnailsF.remove(location);
		thumbnailsG.remove(location);
		thumbnailsH.remove(location);
		thumbnailsI.remove(location);
	}
	
	private void insertThumbnailIntoListsAtLocation(final int location, final File thumbnail) {
		// insert a thumbnail at the given location into all lists
		thumbnailFiles.add(location, thumbnail);
		thumbnailsD.add(location, io.getThumbnail(thumbnail.getName(), EThumbnailType.D));
		thumbnailsE.add(location, io.getThumbnail(thumbnail.getName(), EThumbnailType.E));
		thumbnailsF.add(location, io.getThumbnail(thumbnail.getName(), EThumbnailType.F));
		thumbnailsG.add(location, io.getThumbnail(thumbnail.getName(), EThumbnailType.G));
		thumbnailsH.add(location, io.getThumbnail(thumbnail.getName(), EThumbnailType.H));
		thumbnailsI.add(location, io.getThumbnail(thumbnail.getName(), EThumbnailType.I));
	}

	@Override
	public void restart() {
		Intent intent = getBaseContext().getPackageManager()
				.getLaunchIntentForPackage(getBaseContext().getPackageName());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		startActivity(intent);
	}
	
	@Override
	public void startExternalProgram() {
		// TODO start an external program here
		Toast.makeText(this, "Start External Program", Toast.LENGTH_SHORT).show();
	}
}