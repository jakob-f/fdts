package at.ac.tuwien.media.ethanol;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import at.ac.tuwien.media.R;
import at.ac.tuwien.media.ethanol.gallery.EthanolImageGallery;
import at.ac.tuwien.media.ethanol.io.file.FileIO;
import at.ac.tuwien.media.ethanol.io.file.bitmap.ThumbnailListsHandler;
import at.ac.tuwien.media.ethanol.io.file.model.EThumbnailType;
import at.ac.tuwien.media.ethanol.io.gesture.EthanolGestureDetector;
import at.ac.tuwien.media.ethanol.io.gesture.model.ERectangleType;
import at.ac.tuwien.media.ethanol.io.util.EthanolFileChooser;
import at.ac.tuwien.media.ethanol.io.util.EthanolPreferences;
import at.ac.tuwien.media.ethanol.util.Configuration;
import at.ac.tuwien.media.ethanol.util.EthanolLogger;
import at.ac.tuwien.media.ethanol.util.Value;
import at.ac.tuwien.media.ethanol.util.Value.EDirection;
import at.ac.tuwien.media.ethanol.util.Value.ERow;
import at.ac.tuwien.media.ethanol.util.exception.EthanolException;

/**
 * {@link Ethanol} class implements the main activity for the Ethanol-App.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class Ethanol extends Activity implements IEthanol {
	// gesture detection
	private EthanolGestureDetector egd;
	private GestureDetector gestureDetector;
	
	// thumbnail handling
	private ThumbnailListsHandler thumbnailHandler;
	private int currentThumbnailNo = -1;
	private File fixedThumbnail = null;
	private int fixedThumbnailPos = -1;
	
	// views
	private int displayWidth;
	private ImageView[] imageViews;
	private List<EThumbnailType> thumbnailSizesTopRow;
	private List<EThumbnailType> thumbnailSizesBottomRow;
	
	private ERow currentRow = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// initialize the logger
		EthanolLogger.setParent(this);
		
		// create root folder if not exists
		final File rootDir = new File(Value.ETHANOL_ROOT_FOLDER);
		if (rootDir.exists() || rootDir.mkdirs()) {
			// display a loader while loading and resizing the thumbnails
			new LoaderTask().execute();
		}
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
        		thumbnailHandler = new ThumbnailListsHandler();
    		} catch (EthanolException ee) {
    			ee.printStackTrace();
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
	        if (thumbnailHandler.getImageFiles().isEmpty()) {
	        	initDefaultView();
	        } else {
	        	currentRow = ERow.NONE;
	        	
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
		if (!thumbnailHandler.getImageFiles().isEmpty()) {
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
	        	thumbnailHandler.saveImageOrder();
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
	
	private void initGestureDetection() {
		// get the display size
		DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
		// init new gesture detector
		egd = new EthanolGestureDetector(this, new Point(metrics.widthPixels, metrics.heightPixels));
		gestureDetector = new GestureDetector(this, egd);
		displayWidth = metrics.widthPixels;
	}
	
	private void initViews() {
		// create an imageView for every thumbnail:
    	imageViews = new ImageView[thumbnailHandler.getImageFiles().size()];
    	
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
		iv.setImageResource(R.drawable.im_open_images);
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
				break;
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
		
		// which row to calculate from?
		// swipe from the upper row
		switch (rectangleRow) {
		case ROW_TOP:
			if (!currentRow.equals(ERow.TOP)) {
				currentRow = ERow.TOP;
				updateImageViews();
			}
			
			// set top row background color
			if (isFIAR()) {
				setBackgroundColor(R.id.row_top, Value.COLOR_BACKGROUND_FIAR);
			}
			
			newThumbnailNo = getThumbnailNoAtPosFromUpperRow(percent);
				
			break;
			
		// swipe from the bottom row
		case ROW_BOTTOM:
			if (!currentRow.equals(ERow.BOTTOM)) {
				currentRow = ERow.BOTTOM;
				updateImageViews();
			}
			
			// set bottom row background color
			if (isFIAR()) {
				setBackgroundColor(R.id.row_bottom, Value.COLOR_BACKGROUND_FIAR);
			}

			newThumbnailNo = getThumbnailNoAtPosFromBottomRow(percent);
			
			break;
			
		default:
			break;
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
		if ((thumbnailA >= 0 && thumbnailB >= 0)
				&& thumbnailA != thumbnailB) {
			if (thumbnailA > thumbnailB) {
				skipToThumbnail(EDirection.FORWARD, 1);
			} else {
				skipToThumbnail(EDirection.PREVIOUS, 1);
			}
			
		// used for the first or the last thumbnail in the according row
		} else if (thumbnailA == 0 && thumbnailB < 0) {
			skipToThumbnail(EDirection.FORWARD, 1);
		} else if (thumbnailA == (thumbnailHandler.getImageFiles().size() - 1) && thumbnailB < 0) {
			skipToThumbnail(EDirection.PREVIOUS, 1);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void showSlider(final boolean show, final float centerX) {
		final LinearLayout ll = (LinearLayout) findViewById(R.id.slider_bottom);
		final LayoutParams params = ll.getLayoutParams();
		
		if (show) {
			// show all FIAR thumbnails
			currentRow = ERow.BOTH;
			
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
		// check image boundaries
		checkImageBoundaries();

		// save the start time of this operation for the debug message
		EthanolLogger.saveCurrentTime();
		
		// change background color
		if (isFIAR()) {
			setBackgroundColor(R.id.main_section, Value.COLOR_BACKGROUND_FIAR);
		} else {
			setBackgroundColor(R.id.main_section, Value.COLOR_BACKGROUND_NORMAL);
		}
		
		// first remove only image views which will be reset
		removeAllViewsFromViewGroup(R.id.main_section_left);
		removeAllViewsFromViewGroup(R.id.main_section_center);
		removeAllViewsFromViewGroup(R.id.main_section_right);
		
		// redraw the center thumbnail
		addImageViewToLayout(R.id.main_section_center, new ImageView(this), thumbnailHandler.getThumbnail(fixedThumbnail.getName(), EThumbnailType.A, isFIAR()), EThumbnailType.A, true);
		
		// place images
		if (currentThumbnailNo >= 1) {
			addImageViewToLayout(R.id.main_section_left, new ImageView(this), thumbnailHandler.getBitmapWithSize(currentThumbnailNo - 1, EThumbnailType.B, isFIAR()), EThumbnailType.B, false);
		}
		if (currentThumbnailNo < thumbnailHandler.getImageFiles().size()) {
			addImageViewToLayout(R.id.main_section_right, new ImageView(this), thumbnailHandler.getBitmapWithSize(currentThumbnailNo, EThumbnailType.B, isFIAR()), EThumbnailType.B, false);
		}
		
		// add the debug message
		EthanolLogger.addDebugMessageWithOpTime("Placing all thumbnails took:");
	}
	
	private void updateImageViews() {
		// check image boundaries
		checkImageBoundaries();

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
		int offsetToBottomRow = !isFIAR() ? 2 : 1;
		boolean passedFixedImage = false;

		// save the start time of this operation for the debug message
		EthanolLogger.saveCurrentTime();

		thumbnailSizesTopRow = calculateThumbnailSizes(Math.max((currentThumbnailNo - 1), 0), true);
		thumbnailSizesBottomRow = calculateThumbnailSizes((thumbnailHandler.getImageFiles().size() - (currentThumbnailNo + offsetToBottomRow)), false);

		// add the debug message
		EthanolLogger.addDebugMessageWithOpTime("Calculating image positions took:");

		// save the start time of this operation for the debug message
		EthanolLogger.saveCurrentTime();

		// get the right parameters and set the thumbnails
		for (int i = 0; i < thumbnailHandler.getImageFiles().size(); i++) {

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
					if (!isFIAR()) {
						thumbnailType = EThumbnailType.A;

					// display the fixed thumbnail in the center and the successor on the right
					} else {
						addImageViewToLayout(layoutId, imageViews[i], thumbnailHandler.getThumbnail(fixedThumbnail.getName(), EThumbnailType.A, isFIAR()), EThumbnailType.A, true);
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
					if ((i >= (thumbnailHandler.getImageFiles().size() - 1)) && isFIAR()) {
						addImageViewToLayout(layoutId, imageViews[i], thumbnailHandler.getBitmapWithSize(i, thumbnailType, isFIAR()), thumbnailType, false);
						addImageViewToLayout(R.id.main_section_center, imageViews[i + 1], thumbnailHandler.getThumbnail(fixedThumbnail.getName(), EThumbnailType.A, isFIAR()), EThumbnailType.A, true);

						// everything is done for now, so skip the rest of the cycle
						continue;
					}
				}

			// 3) the remaining ones are upcoming thumbnails in the bottom row
			} else {
				layoutId = R.id.row_bottom;
				thumbnailType = thumbnailSizesBottomRow.get(i - (currentThumbnailNo + offsetToBottomRow));
			}

			// determine if the image should be loaded with the FIAR background
			final boolean isFIAR = isFIAR() && (
					(currentRow.equals(ERow.NONE) && thumbnailType.equals(EThumbnailType.B))
					|| (currentRow.equals(ERow.BOTH) ||
					((currentRow.equals(ERow.TOP) && !passedFixedImage) || (currentRow.equals(ERow.BOTTOM) && passedFixedImage))));
			
			// finally add the image to the view
			// if we passed the fixed image skip one image view position for it
			if (passedFixedImage) {
				if (i < (imageViews.length - 1)) {
					addImageViewToLayout(layoutId, imageViews[i + 1], thumbnailHandler.getBitmapWithSize(i, thumbnailType, isFIAR), thumbnailType, false);
				}
			} else {
				addImageViewToLayout(layoutId, imageViews[i], thumbnailHandler.getBitmapWithSize(i, thumbnailType, isFIAR), thumbnailType, false);
			}
		}

		// add the debug message
		EthanolLogger.addDebugMessageWithOpTime("Placing all thumbnails took:");
	}
	
	private void checkImageBoundaries() {
		if (currentThumbnailNo < 0) {
			currentThumbnailNo = 0;
		} else if (currentThumbnailNo > (imageViews.length - 1)) {
			currentThumbnailNo = (imageViews.length - 1);
		}
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
			fixedThumbnail = thumbnailHandler.getImageFiles().get(currentThumbnailNo);
			
			// and remove it from all lists
			thumbnailHandler.removeThumbnailFromListsAtLocation(currentThumbnailNo);
			
			// redraw thumbnails in the center
			updateCenterViews();
			
		// store the fixed image at the current position and release it
		} else {
			// insert the fixed thumbnail into all lists at the current position,
			thumbnailHandler.insertThumbnailIntoListsAtLocation(currentThumbnailNo, fixedThumbnail);
			
			// set thumbnail number to jump to
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
		// check if we are in FIAR mode and anything changed
		if (isFIAR() && !currentRow.equals(ERow.NONE)) {
			// reset changes
			currentThumbnailNo = fixedThumbnailPos;
			currentRow = ERow.NONE;
			
			// reset background
			resetBackgroundColor();
			setBackgroundColor(R.id.main_section, Value.COLOR_BACKGROUND_FIAR);
			
			// update all image views
			updateImageViews();
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
		EthanolImageGallery.setImageList(thumbnailHandler.getImageFiles());
		
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
	
	private boolean isFIAR() {
		return fixedThumbnail != null;
	}
}