package at.ac.tuwien.media;

import java.io.File;
import java.lang.annotation.ElementType;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.gesture.GestureLibraries;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.Toast;
import at.ac.tuwien.media.io.file.FileIO;
import at.ac.tuwien.media.io.file.ImageIO;
import at.ac.tuwien.media.io.gesture.XGestureDetector;
import at.ac.tuwien.media.io.gesture.XBottomTopGestureListener;
import at.ac.tuwien.media.io.gesture.XMiddleGestureDetector;
import at.ac.tuwien.media.io.util.FileChooser;
import at.ac.tuwien.media.io.util.Preferences;
import at.ac.tuwien.media.util.Configuration;
import at.ac.tuwien.media.util.ImageListAdapter;
import at.ac.tuwien.media.util.Value;
import at.ac.tuwien.media.util.Value.EThumbnailPostion;
import at.ac.tuwien.media.util.exception.XException;

/**
 * The {@link MainActivity} class implements the main activity for the ProjectX-App.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class MainActivity extends Activity implements IMainActivity {
	private Point displayMetrics;
	private ImageIO imageIO;
	
	private GridView gvTop;
	private GridView gvMiddle;
	private GridView gvBottom;
	private List<ImageListAdapter> thumnailLists;
	private List<Bitmap> origThumnailList;
	
	private int thumbnailListStartIndex;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
		// initialize the main view
		setContentView(R.layout.activity_main);
		
		// create root folder if not exists
		new File(Value.ROOT_FOLDER).mkdirs();
		
		// calculate the display metrics
		calcDisplaySize();
	 	
		// prepare to load images
		thumnailLists = new LinkedList<ImageListAdapter>();
	    imageIO = new ImageIO();
	    
	    // display a loader while loading (... and resizing the thumbnails)
	 	new LoaderTask(this).execute();
	}
	
	private class LoaderTask extends AsyncTask<Void, Integer, Void> {
		private ProgressDialog pd;
		private Context context;
		
		public LoaderTask(Context context) {
			this.context = context;
		}
		
		@Override
		protected void onPreExecute() {
			// create a new progress dialog
			pd = new ProgressDialog(MainActivity.this);
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
			// load all thumbnails from the system
			try {
				origThumnailList = imageIO.loadThumbnails();
			} catch (XException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			initGVs();
			
			// add the originalImageList
			addImageList(origThumnailList);
			
			addImageList(origThumnailList);
			addImageList(origThumnailList);
			
			// set start index to first image list
			thumbnailListStartIndex = 0;
			// show image lists		
			updateThumbnailLists();
		    
			// ... and close the progress dialog
			pd.dismiss();
		}
	}
	
	private void updateThumbnailLists() {
		// how many lists can we show?
		if (thumnailLists.size() > thumbnailListStartIndex) {
			gvTop.setAdapter(thumnailLists.get(thumbnailListStartIndex));

			if (thumnailLists.size() > thumbnailListStartIndex + 1) {
				gvMiddle.setAdapter(thumnailLists.get(thumbnailListStartIndex + 1));

				if (thumnailLists.size() > thumbnailListStartIndex + 2) {
					gvBottom.setAdapter(thumnailLists.get(thumbnailListStartIndex + 2));

				}
			}
		}
	}
	
	private void addImageList(final List<Bitmap> newImageList) {
		final List<Bitmap> cpyList = new LinkedList<Bitmap>();
		
		// add two empty images to the start and the end of the list
		// (this is only for the view)
		cpyList.add(Value.EMPTY_BITMAP);
		cpyList.add(Value.EMPTY_BITMAP);
		
		// make a deep copy of the list
		for (Bitmap bm : newImageList) {
			cpyList.add(bm);
		}
		
		cpyList.add(Value.EMPTY_BITMAP);
		cpyList.add(Value.EMPTY_BITMAP);
		
		// create new image adapter, set list and save it
		final ImageListAdapter adapter = new ImageListAdapter(this);
	    adapter.getImageList().addAll(cpyList);
	    thumnailLists.add(adapter);
	}
	
	private void calcDisplaySize() {
		// get the display metrics
		final DisplayMetrics metrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		// set display width and height as a point wit a x and y value
		displayMetrics = new Point(metrics.widthPixels, metrics.heightPixels);
	}
	
	private void initGVs() {
	    gvTop = (GridView) findViewById(R.id.gridview_top);
	    gvTop.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// set the position of the image after the scroll has been performed
				if (scrollState == SCROLL_STATE_IDLE) {
					// calculate the index position of the middle image in the top row
					final int middleImagePos = gvTop.pointToPosition(600, (displayMetrics.x / 2) - (Value.THUMBNAIL_WIDTH / 2) - 10
							);
					
					// jump to image
					if (middleImagePos != -1) {
						gvTop.setSelection(middleImagePos - 1);
						
					// if no position was found: simply use first visible position
					} else {
						gvTop.setSelection(gvTop.getFirstVisiblePosition());
					}
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// this method intentionally left blank
			}
		});
	    
	    // init a new gesture detector
	 	final GestureDetector topGestureDetector = new GestureDetector(this, new XBottomTopGestureListener(this, Value.EThumbnailPostion.TOP));
	 	final OnTouchListener topGestureListener = new OnTouchListener() {
	 		@Override
	 		public boolean onTouch(View v, MotionEvent event) {
	 			return topGestureDetector.onTouchEvent(event);
	 		}
	 	};
	    gvTop.setOnTouchListener(topGestureListener);
	    
	    gvMiddle = (GridView) findViewById(R.id.gridview_middle);
	    gvMiddle.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// set the position of the image after the scroll has been performed
				if (scrollState == SCROLL_STATE_IDLE) {
					// calculate the index position of the middle image in the middle row
					final int middleImagePos = gvMiddle.pointToPosition(600, (displayMetrics.x / 2) + (Value.THUMBNAIL_WIDTH / 2));
					
					// jump to image
					if (middleImagePos != -1) {
						gvMiddle.setSelection(middleImagePos - 2);
						
					// if no position was found: simply use first visible position
					} else {
						gvMiddle.setSelection(gvMiddle.getFirstVisiblePosition());
					}
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// this method intentionally left blank
			}
		});
	    // init a new gesture detector
	    final GestureDetector middleGestureDetector = new GestureDetector(this, new XMiddleGestureDetector(this));
	    final OnTouchListener middleGestureListener = new OnTouchListener() {
	    	@Override
	    	public boolean onTouch(View v, MotionEvent event) {
	    		return middleGestureDetector.onTouchEvent(event);
	 	 	}
	 	 };
	 	gvMiddle.setOnTouchListener(middleGestureListener);
	    
	    gvBottom = (GridView) findViewById(R.id.gridview_bottom);
	    gvBottom.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// set the position of the image after the scroll has been performed
				if (scrollState == SCROLL_STATE_IDLE) {
					// calculate the index position of the bottom image in the bottom row
					final int middleImagePos = gvBottom.pointToPosition(600, (displayMetrics.x / 2) - (Value.THUMBNAIL_WIDTH / 2) - 10);
					
					// jump to image
					if (middleImagePos != -1) {
						gvBottom.setSelection(middleImagePos - 1);
						
					// if no position was found: simply use first visible position
					} else {
						gvBottom.setSelection(gvBottom.getFirstVisiblePosition());
					}
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// this method intentionally left blank
			}
		});
	    // init a new gesture detector
	 	final GestureDetector bottomGestureDetector = new GestureDetector(this, new XBottomTopGestureListener(this, Value.EThumbnailPostion.BOTTTOM));
	 	final OnTouchListener bottomGestureListener = new OnTouchListener() {
	 		@Override
	 		public boolean onTouch(View v, MotionEvent event) {
	 			return bottomGestureDetector.onTouchEvent(event);
	 		}
	 	};
	 	gvBottom.setOnTouchListener(bottomGestureListener);
	    
	}

	@Override
	public void deleteAllFiles() {
		FileIO.delete(new File(Value.ROOT_FOLDER));
			
		// exit the application
		finish();
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
	        case R.id.menu_search:
	        	new FileChooser(this, Configuration.getAsString(Value.CONFIG_IMAGE_FOLDER));
	        	
	            return true;
	 
	        case R.id.menu_settings:
	        	// Display the fragment as the main content.
	        	Preferences.setParent(this);
	            startActivity(new Intent(this, Preferences.class));
	        	
	            return true;
	 
	        default:
	            return false;
    	}
    }

	@Override
	public void restart() {
		Intent intent = getBaseContext().getPackageManager()
				.getLaunchIntentForPackage(getBaseContext().getPackageName());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		startActivity(intent);		
	}
	
	private void showMsg(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	

	@Override
	public void delete(EThumbnailPostion thumbnailPosition) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insert(EThumbnailPostion thumbnailPosition) {
		// save indexes
		final int currentTopPos = gvTop.getFirstVisiblePosition();
		final int currentMiddlePos = gvMiddle.getFirstVisiblePosition();
		final int currentBottomPos = gvBottom.getFirstVisiblePosition();

		switch (thumbnailPosition) {
		case TOP:
			removeEmptyBitmapFromListAtPostion(thumbnailListStartIndex + 1, currentMiddlePos + 3);
			insertBitmapFromListToList(thumbnailListStartIndex, currentTopPos + 2, currentMiddlePos + 3);
			break;
		case MIDDLE_LEFT:
			insertBitmapFromListToList(thumbnailListStartIndex + 1, currentMiddlePos + 3, currentBottomPos + 3);
			// jump to the right position			
			gvBottom.setSelection(currentBottomPos + 1);
			break;
		case MIDDLE_RIGHT:
			insertBitmapFromListToList(thumbnailListStartIndex + 1, currentMiddlePos + 2, currentBottomPos + 2);
			break;
		case BOTTTOM:
			// TODO for now do nothing ... maybe open a new list?
			break;
		default:
			// do nothing
			return;
		}
	}
	
	@Override
	public void prepareInsert(EThumbnailPostion thumbnailPosition) {		
		switch (thumbnailPosition) {
		case TOP:
			insertBitmapToList(Value.EMPTY_BITMAP, thumbnailListStartIndex + 1, gvMiddle.getFirstVisiblePosition() + 3);
			break;
		case MIDDLE_LEFT:
			break;
		case MIDDLE_RIGHT:
			break;
		case BOTTTOM:
			// TODO for now do nothing ... maybe open a new list?
			break;
		default:
			// do nothing
			return;
		}
	}
	
	@Override
	public void abortInsert(EThumbnailPostion thumbnailPosition) {
		// save indexes
		final int currentTopPos = gvTop.getFirstVisiblePosition();
		final int currentMiddlePos = gvMiddle.getFirstVisiblePosition();
		final int currentBottomPos = gvBottom.getFirstVisiblePosition();
					
		switch (thumbnailPosition) {
		case TOP:
			removeEmptyBitmapFromListAtPostion(thumbnailListStartIndex + 1, currentMiddlePos + 3);
			break;
		case MIDDLE_LEFT:
			break;
		case MIDDLE_RIGHT:
			break;
		case BOTTTOM:
			// TODO for now do nothing ... maybe open a new list?
			break;
		default:
			// do nothing
			return;
		}
		
		// update all thumbnail lists
		updateThumbnailLists();
		// jump to the last know indexes
		gvTop.setSelection(currentTopPos);
		gvMiddle.setSelection(currentMiddlePos);
		gvBottom.setSelection(currentBottomPos);
	}
	
	private void insertBitmapFromListToList(final int fromListIndex, final int fromListThumbnailIndex, final int toListThumbnailIndex) {
		if (thumnailLists.size() > (fromListIndex + 1) &&	thumnailLists.get(fromListIndex).getImageList().size() >= fromListThumbnailIndex) {
			insertBitmapToList(thumnailLists.get(fromListIndex).getImageList().get(fromListThumbnailIndex), fromListIndex + 1, toListThumbnailIndex);
		}
	}
	
	private void insertBitmapToList(final Bitmap bm, final int toListIndex, final int toListThumbnailIndex) {
		if (thumnailLists.size() > toListIndex) {
			// save indexes
			final int currentTopPos = gvTop.getFirstVisiblePosition();
			final int currentMiddlePos = gvMiddle.getFirstVisiblePosition();
			final int currentBottomPos = gvBottom.getFirstVisiblePosition();
			
			// insert thumbnail at position
			thumnailLists.get(toListIndex).getImageList().add(toListThumbnailIndex, bm);
			
			// update all thumbnail lists
			updateThumbnailLists();
			// jump to the last know indexes
			gvTop.setSelection(currentTopPos);
			gvMiddle.setSelection(currentMiddlePos);
			gvBottom.setSelection(currentBottomPos);
		}
	}
	
	private void removeBitmapFromList(final int listIndex, final int thumbnailIndex) {
		if (thumnailLists.size() > listIndex && thumnailLists.get(listIndex).getImageList().size() >= thumbnailIndex) {
			// remove thumbnail at position
			thumnailLists.get(listIndex).getImageList().remove(thumbnailIndex);
		}
	}
	
	private void removeEmptyBitmapFromListAtPostion(final int listIndex, final int thumbnailIndex) {
		if (thumnailLists.size() > listIndex && thumnailLists.get(listIndex).getImageList().size() >= thumbnailIndex) {
			if (thumnailLists.get(listIndex).getImageList().get(thumbnailIndex).equals(Value.EMPTY_BITMAP)) {
				// remove thumbnail at position
				removeBitmapFromList(listIndex, thumbnailIndex);
			}
		}
	}
}