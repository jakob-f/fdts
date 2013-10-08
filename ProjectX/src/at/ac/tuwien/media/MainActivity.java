package at.ac.tuwien.media;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import at.ac.tuwien.media.io.file.FileIO;
import at.ac.tuwien.media.io.file.ImageIO;
import at.ac.tuwien.media.io.util.FileChooser;
import at.ac.tuwien.media.io.util.Preferences;
import at.ac.tuwien.media.util.Configuration;
import at.ac.tuwien.media.util.GridViewAdapter;
import at.ac.tuwien.media.util.ImageListAdapter;
import at.ac.tuwien.media.util.Util;
import at.ac.tuwien.media.util.Value;
import at.ac.tuwien.media.util.Value.EThumbnailPostion;
import at.ac.tuwien.media.util.exception.XException;

/**
 * The {@link MainActivity} class implements the main activity for the ProjectX-App.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class MainActivity extends Activity implements IMainActivity {
	private ImageIO imageIO;
	
	private List<Bitmap> origThumnailList;
	private GridViewAdapter gvAdapter;
	private ListView lv;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    // calculate the display metrics (has to be done first!)
	    calcDisplaySize();
	    
		// initialize the main view
		setContentView(R.layout.activity_main);
		
		// create root folder if not exists
		new File(Value.ROOT_FOLDER).mkdirs();
		
		// create the grid view list adapter
		gvAdapter = new GridViewAdapter(this);
		
		// prepare to load images
	    imageIO = new ImageIO();
	    
	    // display a loader while loading (... and resizing the thumbnails)
	 	new LoaderTask().execute();
	}
	
	private class LoaderTask extends AsyncTask<Void, Integer, Void> {
		private ProgressDialog pd;
		
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
			// show lists
			initLists();
			
			// add the originalImageList
			addImageList(origThumnailList);
			showListNumber(0);
			
			// ... and close the progress dialog
			pd.dismiss();
		}
	}
	
	private void calcDisplaySize() {
		// get the display metrics
		final DisplayMetrics metrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		// set display width and height as a point wit a x and y value
		Util.setDisplayMetrics(new Point(metrics.widthPixels, metrics.heightPixels));
	}
	
	private void initLists() {
		// create the main scrollable list
		lv = (ListView) findViewById(R.id.listview_main);
		lv.setBackgroundColor(Color.TRANSPARENT);
		lv.setAdapter(gvAdapter);
		// snap the lists to the layout
		lv.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// set the position of the list after the scroll has been performed
				if (scrollState == SCROLL_STATE_IDLE) {
					gvAdapter.notifyDataSetChanged();
					showListNumber(lv.getFirstVisiblePosition());
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
	
	private void addImageList(final List<Bitmap> newImageList) {
		// create new image adapter, set list and save it
		final ImageListAdapter ilAdapter = new ImageListAdapter(this);
		final List<Bitmap> newList = ilAdapter.getImageList();
		
		// add two empty images to the start and the end of the list
		// (this is only for the view)
		newList.add(Value.EMPTY_BITMAP);
		newList.add(Value.EMPTY_BITMAP);
		
		if (newImageList != null) {
			// make a deep copy of the list
			for (Bitmap bm : newImageList) {
				newList.add(bm);
			}
		}
		
		newList.add(Value.EMPTY_BITMAP);
		newList.add(Value.EMPTY_BITMAP);
		
		// set the image list
		gvAdapter.getAdapterList().add(ilAdapter);
		// update the view
		gvAdapter.notifyDataSetChanged();
	}

	private void showListNumber(final int listIndex) {
		final int realListIndex = listIndex < 0 ? 0 : listIndex;
		
		// set the right background
		if (listIndex % 2 == 0) {
			((LinearLayout) findViewById(R.id.layout_main)).setBackgroundResource(R.layout.background_normal);
		} else {
			((LinearLayout) findViewById(R.id.layout_main)).setBackgroundResource(R.layout.background_shifted);
		}
		
		// show list with number
		lv.post(new Runnable() {
			@Override
			public void run() {
				lv.setSelection(realListIndex); // jump to first view
			}
		});
	}
	
	@Override
	public void delete(int listIndex, EThumbnailPostion thumbnailPosition) {
		// get current thumbnail index
		int thumbnailIndex = gvAdapter.getFirstVisiblePositionOfView(listIndex);
		
		// set the right values
		switch (thumbnailPosition) {
			case LEFT:
				thumbnailIndex += 3;
				break;
						
			case MIDDLE:
				thumbnailIndex += 2;
				break;
						
			case RIGHT:
				thumbnailIndex += 2;
				break;
				
			default:
				// do nothing
				return;
		}
		
		// delete the thumbnail
		deleteBitmapFromList(listIndex, thumbnailIndex);
	}

	@Override
	public void insert(final int fromListIndex, final Value.EThumbnailPostion fromListThumbnailPosition, final Value.EInsertListPosition insertListPosition) {
		// get current thumbnail indexes
		final int toListIndex = (insertListPosition == Value.EInsertListPosition.UP) ? fromListIndex - 1 : 
									(insertListPosition == Value.EInsertListPosition.DOWN) ? fromListIndex + 1 : fromListIndex;
		
		int fromListThumbnailIndex = gvAdapter.getFirstVisiblePositionOfView(fromListIndex);
		int toListThumbnailIndex = gvAdapter.getFirstVisiblePositionOfView(toListIndex);
		
		// set the right values
		switch (fromListThumbnailPosition) {
			case LEFT:
				fromListThumbnailIndex += 3;
				toListThumbnailIndex += 2;
				break;
				
			case MIDDLE:
				fromListThumbnailIndex += 2;
				toListThumbnailIndex += toListThumbnailIndex == 0 ? 2 : 3;
				break;
				
			case RIGHT:
				fromListThumbnailIndex += 2;
				toListThumbnailIndex += 2;
				break;
				
			default:
				// do nothing
				return;
		}
		
		// insert the thumbnail
		insertBitmapFromListToList(fromListIndex, fromListThumbnailIndex, toListIndex, toListThumbnailIndex);
	}
	
	private void insertBitmapFromListToList(final int fromListIndex, final int fromListThumbnailIndex, final int toListIndex, final int toListThumbnailIndex) {
		// create a new list if needed
		if (gvAdapter.getCount() <= toListIndex) {
			addImageList(null);
		}
		
		// insert the thumbnail to the specified list
		final Bitmap bm = gvAdapter.getAdapterList().get(fromListIndex).getImageList().get(fromListThumbnailIndex);
		insertBitmapToList(toListIndex, toListThumbnailIndex, bm);
	}
	
	private void insertBitmapToList(final int listIndex, final int thumbnailIndex, final Bitmap bm) {
		// it is not possible to insert into the first (i.e. original) list
		// TODO and to insert empty bitmaps (as a workaround) // do we need it??
		if (listIndex > 0 && !bm.equals(Value.EMPTY_BITMAP)) {
			// save current indexes
			gvAdapter.saveFirstVisiblePositions();
			
			if (listIndex < gvAdapter.getAdapterList().size() &&
					thumbnailIndex < gvAdapter.getAdapterList().get(listIndex).getImageList().size()) {
				// save the bitmap
				gvAdapter.getAdapterList().get(listIndex).getImageList().add(thumbnailIndex, bm);
				
				// now update the list
				gvAdapter.getAdapterList().get(listIndex).notifyDataSetChanged();
				if ((listIndex - 2) > lv.getFirstVisiblePosition() ||
						listIndex < lv.getFirstVisiblePosition()) {
					showListNumber(listIndex - 1);
				}
			}
		}
	}
	
	private void deleteBitmapFromList(final int listIndex, final int thumbnailIndex) {
		// it is not possible to delete from the first (i.e. original) list
		if (listIndex > 0) {
			// save current indexes
			gvAdapter.saveFirstVisiblePositions();
	
			// delete the thumbnail from the specified list
			final Bitmap bm = gvAdapter.getAdapterList().get(listIndex).getImageList().get(thumbnailIndex);
			// do not delete empty images
			if (!bm.equals(Value.EMPTY_BITMAP)) {
				gvAdapter.getAdapterList().get(listIndex).getImageList().remove(thumbnailIndex);
				
				// finally update the list
				gvAdapter.getAdapterList().get(listIndex).notifyDataSetChanged();
			}
		}
	}
}