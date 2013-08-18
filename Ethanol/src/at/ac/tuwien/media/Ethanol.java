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
import android.graphics.drawable.GradientDrawable;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import at.ac.tuwien.media.io.file.FileIO;
import at.ac.tuwien.media.io.file.ImageIO;
import at.ac.tuwien.media.io.file.ImageOrderListIO;
import at.ac.tuwien.media.io.file.model.EThumbnailType;
import at.ac.tuwien.media.io.gesture.EthanolGestureDetector;
import at.ac.tuwien.media.io.gesture.model.ERectangleType;
import at.ac.tuwien.media.util.Configuration;
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
	private ImageIO io;
	private int currentThumbnailNo = -1;
	private File fixedThumbnail = null;
	private int fixedThumbnailPos = -1;
	private List<File> imageFiles;
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
            pd.setTitle(R.string.loader_title);
            pd.setMessage(getResources().getString(R.string.loader_message));
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
	        setContentView(R.layout.ethanol_main_activity);  
	            
	        // initialize the gesture detection
	        initGestureDetection();
		        
		    // start only if there are files to display
	        if (imageFiles.isEmpty()) {
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
		if (!imageFiles.isEmpty()) {
			// save the start time of this operation for the debug message
			long overallOpTime = System.currentTimeMillis();
			
			//XXX for future implementations read the return value
			// (yet not returned by GestureDetector) of onTouchEvent
			// if true is returned use it to end the method
			
			// try to perform a touch event
			gestureDetector.onTouchEvent(me);
			
			// save the current down event
			if (me.getAction() == MotionEvent.ACTION_DOWN) {
				egd.onDown(me);
				
			// try to perform a swipe event
			} else if (me.getAction() == MotionEvent.ACTION_UP) {
				egd.onUp(me);
				
				// add the debug message with the right time
				EthanolLogger.setOpStartTime(overallOpTime);
				EthanolLogger.addDebugMessageWithOpTime("Performing this action took:");
				
			// try to perform a move	
			} else if (me.getAction() == MotionEvent.ACTION_MOVE) {
				egd.onMove(me);
			}
	
			// after this a motion is completed
			// time to display a debug message
			EthanolLogger.displayDebugMessage();
			
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
    	return onOptionsItemSelected(item.getItemId()) ? true
    			: super.onOptionsItemSelected(item);
    }
    
    @Override
    public boolean onOptionsItemSelected(final int itemId) {
    	switch (itemId) {
	        case R.id.menu_save:
	            saveImageOrder();
	            return true;
	 
	        case R.id.menu_search:
	        	new EthanolFileChooser(this, Configuration.getAsString(Value.CONFIG_IMAGE_FOLDER));
	            return true;
	 
	        case R.id.menu_settings:
	        	// Display the fragment as the main content.
	        	EthanolPreferences.setParent(this);
	            startActivity(new Intent(this, EthanolPreferences.class));
	        	
	            return true;
	 
	        default:
	            return false;
    	}
    }
    	
	private void loadThumbnails() throws EthanolException {
		// save the start time of this operation for the debug message
		EthanolLogger.saveCurrentTime();

		// load images from sdCard
		// create thumbnails if needed
		io = new ImageIO();
		imageFiles = io.loadThumbnails();
		EthanolLogger.addDebugMessage("Read " + imageFiles.size() + " images");

		// load the other thumbnails in the correct order
		// since they are the biggest files, thumbnail sizes A - C are loaded directly,
		// for performance issues thumbnail sizes D - I are cached
		thumbnailsD = io.getThumbnailList(imageFiles, EThumbnailType.D);
		thumbnailsE = io.getThumbnailList(imageFiles, EThumbnailType.E);
		thumbnailsF = io.getThumbnailList(imageFiles, EThumbnailType.F);
		thumbnailsG = io.getThumbnailList(imageFiles, EThumbnailType.G);
		thumbnailsH = io.getThumbnailList(imageFiles, EThumbnailType.H);
		thumbnailsI = io.getThumbnailList(imageFiles, EThumbnailType.I);
		
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
    	imageViews = new ImageView[imageFiles.size()];
    	
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
    	iv.setBackgroundColor(Value.COLOR_TRANSPARENT);
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
		switch (direction) {
			case PREVIOUS:
				// skip to previous picture with the given interval
				currentThumbnailNo -= interval;
				break;
			case FORWARD:
				// skip to next picture with the given interval
				currentThumbnailNo += interval;
				break;
			default:
				// do nothing
		}
		
		// update the screen
		updateImageViews();
	}
	
	@Override
	public void skipToThumbnail(final int thumbnailNumber) {
		// set the current thumbnail number
		currentThumbnailNo = thumbnailNumber;
		
		// update the screen
		updateImageViews();
	}		
	
	@Override
	public void skipToThumbnail(final ERectangleType rectangleRow, final int percent) {	
		// disable slider (just in case it was shown before...)
		showSlider(false, -1.0f);
		
		int newThumbnailNo = -1;
		
		// reset
		if (rectangleRow == null) {
			// but reset background colors
			setBackgroundColor(R.id.row_top, Value.COLOR_BACKGROUND_NORMAL);
			setBackgroundColor(R.id.row_bottom, Value.COLOR_BACKGROUND_NORMAL);
			
			// reset to old thumbnail number if set
			if (fixedThumbnailPos >= 0) {
				currentThumbnailNo = fixedThumbnailPos;
			}
			
		// which row to calculate from?
		// swipe from the upper row
		} else if (rectangleRow == ERectangleType.ROW_TOP) {
			// set top row background color
			if (isFIAR()) {
				setBackgroundColor(R.id.row_top, Value.COLOR_BACKGROUND_FIAR);
			}
			
			newThumbnailNo = getThumbnailNoAtPosFromUpperRow(percent);
				
		// swipe from the bottom row
		} else if (rectangleRow == ERectangleType.ROW_BOTTOM) {
			// set bottom row background color
			if (isFIAR()) {
				setBackgroundColor(R.id.row_bottom, Value.COLOR_BACKGROUND_FIAR);
			}

			newThumbnailNo = getThumbnailNoAtPosFromBottomRow(percent);
		}
		
		if (newThumbnailNo >= 0) {
			currentThumbnailNo = newThumbnailNo;
		}
		
		// update the screen
		if (!isFIAR()) {
			updateImageViews();
		} else {
			updateCenterViews();
		}
	}
	
	private int getThumbnailNoAtPosFromUpperRow(final int percent) {
		final int pixelPercentage = (displayWidth * percent) / 100;
		
		// start at the right edge - this compensates blank spaces on the left
		int pixelsUsed = displayWidth;

		// go though the size list in reverse order
		for (int i = (thumbnailSizesTopRow.size() - 1); i >= 0; i--) {
			// subtract each thumbnail from the display width
			pixelsUsed -= thumbnailSizesTopRow.get(i).getTotalWidth();

			// determine if we reached the thumbnail with the pixels percentage
			// and set the current thumbnail number
			if (pixelsUsed <= pixelPercentage) {
				return i;
			}
		}
		
		return -1;
	}
	
	private int getThumbnailNoAtPosFromBottomRow(final int percent) {
		final int pixelPercentage = (displayWidth * percent) / 100;
		
		// start at the left edge - this compensates blank spaces on the right
		int pixelsUsed = 0;

		// go though the size list in normal order
		for (int i = 0; i < thumbnailSizesBottomRow.size(); i++) {
			// add each thumbnail to the total width
			pixelsUsed += thumbnailSizesBottomRow.get(i).getTotalWidth();

			// determine if we reached the thumbnail with the pixels percentage
			// and set the current thumbnail number
			if (pixelsUsed >= pixelPercentage) {
				// no image fixed
				if (!isFIAR()) {
					return currentThumbnailNo + i + 2;
				}
				
				// we have a fixed image in the center
				return fixedThumbnailPos + i + 2;
			}
		}
		return -1;
	}
	
	@Override
	public void scrollToThumbnail(final ERectangleType rectangleRow, final int percentA, final int percentB) {
		int thumbnailA = -1;
		int thumbnailB = -1;
		
		// calculate the right thumbnail numbers
		switch (rectangleRow) {
		case ROW_TOP:
			thumbnailA = getThumbnailNoAtPosFromUpperRow(percentA);
			thumbnailB = getThumbnailNoAtPosFromUpperRow(percentB);
			
			break;
			
		case ROW_BOTTOM:
			thumbnailA = getThumbnailNoAtPosFromBottomRow(percentA);
			thumbnailB = getThumbnailNoAtPosFromBottomRow(percentB);
			
			break;
			
		default:
			
			break;
		}
		
		// we must have valid thumbnail numbers which are different from each other
		if (thumbnailA != -1 && thumbnailB != -1
				&& thumbnailA != thumbnailB) {
			if (thumbnailA > thumbnailB) {
				skipToThumbnail(EDirection.FORWARD, 1);
			} else {
				skipToThumbnail(EDirection.PREVIOUS, 1);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void showSlider(final boolean show, final float centerX) {
		final LinearLayout ll = (LinearLayout) findViewById(R.id.slider_bottom);
		final LayoutParams params = ll.getLayoutParams();
		
		if (show) {
			// set background colors
			setBackgroundColor(R.id.row_top, Value.COLOR_BACKGROUND_FIAR);
			setBackgroundColor(R.id.main_section, Value.COLOR_BACKGROUND_FIAR);
			setBackgroundColor(R.id.row_bottom, Value.COLOR_BACKGROUND_FIAR);
		
			// create a new gradient to indicate the center of the slider
			final GradientDrawable gd = new GradientDrawable(
		            GradientDrawable.Orientation.LEFT_RIGHT,
		            new int[] {Value.COLOR_BACKGROUND_GRADIENT, Value.COLOR_BACKGROUND_SLIDER});
			gd.setGradientType(GradientDrawable.RADIAL_GRADIENT);
			gd.setGradientRadius(500.0f);
			gd.setGradientCenter(centerX, 0.0f);
		    gd.setCornerRadius(60.0f);
		    gd.setStroke(10, Value.COLOR_TRANSPARENT);
		    
		    // set gradient
			ll.setBackgroundDrawable(gd);
			
			// show the layout
			params.height = Value.SLIDER_WIDTH;
		} else {
			setBackgroundColor(R.id.row_top, Value.COLOR_BACKGROUND_NORMAL);
			
			params.height = 0;
		}
	}
	
	private void updateCenterViews() {
		// save the start time of this operation for the debug message
		EthanolLogger.saveCurrentTime();	
		
		// first remove only image views which will be reset
		removeAllViewsFromViewGroup(R.id.main_section_left);
		removeAllViewsFromViewGroup(R.id.main_section_right);
		
		// place images
		if (currentThumbnailNo >= 1) {
			addImageViewToLayout(R.id.main_section_left, new ImageView(this), getBitmapWithSize(currentThumbnailNo - 1, EThumbnailType.B), EThumbnailType.B, false);
		}
		if (currentThumbnailNo < imageFiles.size()) {
			addImageViewToLayout(R.id.main_section_right, new ImageView(this), getBitmapWithSize(currentThumbnailNo, EThumbnailType.B), EThumbnailType.B, false);
		}
		
		// add the debug message
		EthanolLogger.addDebugMessageWithOpTime("Placing all thumbnails took:");
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
		thumbnailSizesBottomRow = calculateThumbnailSizes((imageFiles.size() - (currentThumbnailNo + offsetToBottomRow)), false);

		// add the debug message
		EthanolLogger.addDebugMessageWithOpTime("Calculating image positions took:");

		// save the start time of this operation for the debug message
		EthanolLogger.saveCurrentTime();

		// get the right parameters and set the thumbnails
		for (int i = 0; i < imageFiles.size(); i++) {

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
						addImageViewToLayout(layoutId, imageViews[i], io.getThumbnail(fixedThumbnail.getName(), EThumbnailType.A, fixedThumbnail != null), EThumbnailType.A, true);
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
					if ((i >= (imageFiles.size() - 1)) && (fixedThumbnail != null)) {
						addImageViewToLayout(layoutId, imageViews[i], getBitmapWithSize(i, thumbnailType), thumbnailType, false);
						addImageViewToLayout(R.id.main_section_center, imageViews[i + 1], io.getThumbnail(fixedThumbnail.getName(), EThumbnailType.A, fixedThumbnail != null), EThumbnailType.A, true);

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
	
	private void resetBackgroundColor() {
		setBackgroundColor(R.id.row_top, Value.COLOR_TRANSPARENT);
		setBackgroundColor(R.id.main_section, Value.COLOR_TRANSPARENT);
		setBackgroundColor(R.id.row_bottom, Value.COLOR_TRANSPARENT);
		
		if (Configuration.getAsBoolean(Value.CONFIG_DEBUG)) {
			setBackgroundColor(R.id.layout_main, Value.COLOR_BACKGROUND_DEBUG);
		} else {
			setBackgroundColor(R.id.layout_main, Value.COLOR_BACKGROUND_NORMAL);
		}
	}
	
	// set background color in view
	private void setBackgroundColor(final int layoutId, final int color) {
		((ViewGroup) findViewById(layoutId)).setBackgroundColor(color);
	}
	

	private void addImageViewToLayout(final int layoutId, final ImageView iv, final Bitmap bm, final EThumbnailType thumbnailType, final boolean highlightImage) {
		// highlight the thumbnail
		if (highlightImage) {
			iv.setBackgroundResource(R.layout.highlight);
		// reset default backgorund color
		} else {
			iv.setBackgroundColor(Value.COLOR_TRANSPARENT);
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
		// no image fixed - fix the current one
		if (!isFIAR()) {
			// save currentPosition
			fixedThumbnailPos = currentThumbnailNo;
			// set the fixed thumbnail
			fixedThumbnail = imageFiles.get(currentThumbnailNo);
			
			// and remove it from all lists
			removeThumbnailFromListsAtLocation(currentThumbnailNo);
			
			// change background color
			setBackgroundColor(R.id.main_section, Value.COLOR_BACKGROUND_FIAR);
			
			// remove the fixed thumbnail from the main section
			removeAllViewsFromViewGroup(R.id.main_section_center);
			// redraw and highlight the fixed thumbnail
			addImageViewToLayout(R.id.main_section_center, imageViews[currentThumbnailNo], io.getThumbnail(fixedThumbnail.getName(), EThumbnailType.A, isFIAR()), EThumbnailType.A, true);
			
		// store the fixed image at the current position and release it
		} else {
			// insert the fixed thumbnail into all lists at the current position,
			insertThumbnailIntoListsAtLocation(currentThumbnailNo, fixedThumbnail);
			
			if (!Configuration.getAsBoolean(Value.CONFIG_JUMP_BACK)) {
				currentThumbnailNo = fixedThumbnailPos;
			}
			
			// clear current position
			fixedThumbnailPos = -1;
			// release the fixed thumbnail
			fixedThumbnail = null;
			
			// disable slider (just in case it was shown before...)
			showSlider(false, -1.0f);
			
			// reset background color
			resetBackgroundColor();
			
			// and update all image views
			updateImageViews();
		}
	}
	
	@Override
	public void resetFIAR() {
		if (isFIAR()) {
			// reset changes
			currentThumbnailNo = fixedThumbnailPos;
			
			// reset background
			resetBackgroundColor();
			setBackgroundColor(R.id.main_section, Value.COLOR_BACKGROUND_FIAR);
			
			// update all image views
			updateImageViews();
		}
	}
	
	private Bitmap getBitmapWithSize(final int thumbnailNumber, final EThumbnailType thumbnailType) {
		// return the thumbnail from the file system or from a list with the given number and size
		switch (thumbnailType) {
			case A:
				return io.getThumbnail(imageFiles.get(thumbnailNumber).getName(), EThumbnailType.A, isFIAR());
			case B:
				return io.getThumbnail(imageFiles.get(thumbnailNumber).getName(), EThumbnailType.B, isFIAR());
			case C:
				return io.getThumbnail(imageFiles.get(thumbnailNumber).getName(), EThumbnailType.C, isFIAR());
			case D:
				if (isFIAR()) {
					return io.getThumbnail(imageFiles.get(thumbnailNumber).getName(), EThumbnailType.D, true);
				}
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
				System.out.println("I " +(thumbnailNumber + 1) );
				return thumbnailsI.get(thumbnailNumber);
			default:
				return null;
		}
	}
	
	private void removeThumbnailFromListsAtLocation(final int location) {
		// remove a thumbnail at the given location from all lists
		imageFiles.remove(location);
		if (!thumbnailsD.isEmpty()) {
			thumbnailsD.remove(location);
		}
		if (!thumbnailsE.isEmpty()) {
			thumbnailsE.remove(location);
		}
		if (!thumbnailsF.isEmpty()) {
			thumbnailsF.remove(location);
		}
		if (!thumbnailsG.isEmpty()) {
			thumbnailsG.remove(location);
		}
		if (!thumbnailsH.isEmpty()) {
			thumbnailsH.remove(location);
		}
		if (!thumbnailsI.isEmpty()) {
			thumbnailsI.remove(location);
		}
	}
	
	private void insertThumbnailIntoListsAtLocation(final int location, final File thumbnail) {
		// insert a thumbnail at the given location into all lists
		imageFiles.add(location, thumbnail);
		if (!thumbnailsD.isEmpty()) {
			thumbnailsD.add(location, io.getThumbnail(thumbnail.getName(), EThumbnailType.D));
		}
		if (!thumbnailsE.isEmpty()) {
			thumbnailsE.add(location, io.getThumbnail(thumbnail.getName(), EThumbnailType.E));
		}
		if (!thumbnailsF.isEmpty()) {
			thumbnailsF.add(location, io.getThumbnail(thumbnail.getName(), EThumbnailType.F));
		}
		if (!thumbnailsG.isEmpty()) {
			thumbnailsG.add(location, io.getThumbnail(thumbnail.getName(), EThumbnailType.G));
		}
		if (!thumbnailsH.isEmpty()) {
			thumbnailsH.add(location, io.getThumbnail(thumbnail.getName(), EThumbnailType.H));
		}
		if (!thumbnailsI.isEmpty()) {
			thumbnailsI.add(location, io.getThumbnail(thumbnail.getName(), EThumbnailType.I));
		}
		
		// save image order list if wished
		if (Configuration.getAsBoolean(Value.CONFIG_AUTOSAVE)) {
			saveImageOrder();
		}
	}

	@Override
	public void restart() {
		Intent intent = getBaseContext().getPackageManager()
				.getLaunchIntentForPackage(getBaseContext().getPackageName());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		startActivity(intent);
	}
	
	@Override
	public void showCurrentThumbnail() {
		// set the parent
		EthanolImageGallery.setParent(this);
		// add a list with all original images to the gallery
		EthanolImageGallery.setImageList(imageFiles);
		
		// start a new image gallery activity
		final Intent intent = new Intent(this, EthanolImageGallery.class);
		// set the current image number
		final Bundle b = new Bundle();
		b.putInt("position", currentThumbnailNo);
		intent.putExtras(b);
		
		// start intent
		startActivity(intent);
	}

	@Override
	public void deleteAllFiles() {
		FileIO.delete(new File(Value.ETHANOL_ROOT_FOLDER));
			
		// exit the application
		finish();
	}

	private void saveImageOrder() {
		try {
			ImageOrderListIO.write(imageFiles);
			
			EthanolLogger.displayDebugMessage("Saved image order list.");
		} catch (EthanolException ee) {
			ee.printStackTrace();
		}		
	}
	
	private boolean isFIAR() {
		return fixedThumbnail != null;
	}
}