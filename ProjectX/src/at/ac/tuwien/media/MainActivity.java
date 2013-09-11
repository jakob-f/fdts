package at.ac.tuwien.media;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import at.ac.tuwien.media.io.util.FileChooser;
import at.ac.tuwien.media.io.util.Preferences;
import at.ac.tuwien.media.util.Configuration;
import at.ac.tuwien.media.util.ImageListAdapter;
import at.ac.tuwien.media.util.Value;
import at.ac.tuwien.media.util.exception.XException;

/**
 * The {@link MainActivity} class implements the main activity for the ProjectX-App.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class MainActivity extends Activity implements IMainActivity {
	// gesture detection
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	
	private DisplayMetrics displayMetrics;
	private ImageIO imageIO;
	
	private GridView gvTop;
	private GridView gvMiddle;
	private GridView gvBottom;
	private List<ImageListAdapter> imageLists;
	private List<Bitmap> origImageList;
	
	private int imageListStartIndex;
	private int firstImageGvTop;
	private int firstImageGvMiddle;
	private int firstImageGvBottom;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
		// initialize the main view
		setContentView(R.layout.activity_main);
		
		// create root folder if not exists
		new File(Value.ROOT_FOLDER).mkdirs();
		
		// initialize the gesture detection
		initGestureDetection();
		
		// get the display size
		displayMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
	 	
		// prepare to load images
		imageLists = new LinkedList<ImageListAdapter>();
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
				origImageList = imageIO.loadThumbnails();
			} catch (XException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			initGVs();
			
			// add the originalImageList
			addImageList(origImageList);
			
			addImageList(origImageList);
			addImageList(origImageList);
			
			// set start index to first image list
			imageListStartIndex = 0;
			// show image lists		
			updateImageLists();
		    
			// ... and close the progress dialog
			pd.dismiss();
		}
	}
	
	private void updateImageLists() {
		System.out.println(imageLists.size());
		
		// how many lists can we show?
		if (imageLists.size() > imageListStartIndex) {
			gvTop.setAdapter(imageLists.get(imageListStartIndex));

			if (imageLists.size() > imageListStartIndex + 1) {
				gvMiddle.setAdapter(imageLists.get(imageListStartIndex + 1));

				if (imageLists.size() > imageListStartIndex + 2) {
					gvBottom.setAdapter(imageLists.get(imageListStartIndex + 2));

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
	    imageLists.add(adapter);
	}
	
	private void initGestureDetection() {
		// init new gesture detector
		gestureDetector = new GestureDetector(this, new XGestureDetector(this));
		gestureListener = new OnTouchListener() {
			@Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
	}
	
	private void initGVs() {
	    gvTop = (GridView) findViewById(R.id.gridview_top);
	    gvTop.setOnTouchListener(gestureListener);
	    gvTop.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// set the position of the image after the scroll has been performed
				if (scrollState == SCROLL_STATE_IDLE) {
					// calculate the index position of the middle image in the top row
					final int middleImagePos = gvTop.pointToPosition(500, (displayMetrics.widthPixels / 2) - (Value.THUMBNAIL_WIDTH / 2) - 10
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
	    
	    gvMiddle = (GridView) findViewById(R.id.gridview_middle);
	    gvMiddle.setOnTouchListener(gestureListener);
	    gvMiddle.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// set the position of the image after the scroll has been performed
				if (scrollState == SCROLL_STATE_IDLE) {
					// calculate the index position of the middle image in the middle row
					final int middleImagePos = gvMiddle.pointToPosition(500, (displayMetrics.widthPixels / 2) + (Value.THUMBNAIL_WIDTH / 2));
					
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
	    
	    gvBottom = (GridView) findViewById(R.id.gridview_bottom);
	    gvBottom.setOnTouchListener(gestureListener);
	    gvBottom.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// set the position of the image after the scroll has been performed
				if (scrollState == SCROLL_STATE_IDLE) {
					// calculate the index position of the bottom image in the bottom row
					final int middleImagePos = gvBottom.pointToPosition(500, (displayMetrics.widthPixels / 2) - (Value.THUMBNAIL_WIDTH / 2) - 10);
					
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
}