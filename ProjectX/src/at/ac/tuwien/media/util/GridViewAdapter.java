package at.ac.tuwien.media.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.AbsListView.OnScrollListener;
import at.ac.tuwien.media.MainActivity;
import at.ac.tuwien.media.io.gesture.XNormalGestureListener;
import at.ac.tuwien.media.io.gesture.XShiftedGestureDetector;

/**
 * The {@link GridViewAdapter} class handles {@link GridView} inside a list view.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
@SuppressLint("NewApi")
public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private List<ImageListAdapter> iaList;
    private Map<Integer, GridView> gvMapping;
    private SparseIntArray selectionMapping;

    public GridViewAdapter(Context context) {
        this.context = context;
        iaList = new LinkedList<ImageListAdapter>();
        gvMapping = new TreeMap<Integer, GridView>();
        selectionMapping = new SparseIntArray();
    }

    @Override
    public int getCount() {
        return iaList.size();
    }

    @Override
    public Object getItem(final int position) {
        return iaList.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return -1l;
    }

    @Override
	public View getView(final int position, View convertView,	final ViewGroup parent) {
		// do not check if view is converted
		// always create a new view
		final GridView gv = new GridView(context);
		// set view attributes
		gv.setBackgroundColor(Color.TRANSPARENT);
		gv.setFadingEdgeLength(0);
		gv.setGravity(Gravity.CENTER);
		gv.setHorizontalSpacing(0);
		gv.setNumColumns(1);
		gv.setRotation(90.0f);
		gv.setScrollContainer(false);
		gv.setSelector(new StateListDrawable());
		gv.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		gv.setVerticalSpacing(Value.THUMBNAIL_PADDING);
		// set image list adapter and save it
		gv.setAdapter(iaList.get(position));
		// init a new gesture detector
		final GestureDetector gestureDetector;
		// set values for normal and shifted rows
    	if ((position % 2) == 0) {
    		gv.setLayoutParams(new GridView.LayoutParams(LayoutParams.MATCH_PARENT, Value.DISPLAY_WIDTH));
    		gestureDetector = new GestureDetector(context, new XNormalGestureListener((MainActivity) context, position));
    	} else {
    		gv.setLayoutParams(new GridView.LayoutParams(LayoutParams.MATCH_PARENT, (Value.DISPLAY_WIDTH + Value.THUMBNAIL_WIDTH + Value.THUMBNAIL_PADDING)));
    		gestureDetector = new GestureDetector(context, new XShiftedGestureDetector((MainActivity) context, position));
    	}
    	// set the touch listener
		final OnTouchListener gestureListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent me) {
				return gestureDetector.onTouchEvent(me);
			}
		};
		gv.setOnTouchListener(gestureListener);
    	// set a scroll listener which snaps to the image positions
		gv.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// set the position of the image after the scroll has been performed
				if (scrollState == SCROLL_STATE_IDLE) {
					// calculate the index position of the middle image in the top row
					final int middleImagePos = gv.pointToPosition(400, ((int) (Value.DISPLAY_WIDTH - Value.THUMBNAIL_WIDTH) / 2));
					
					int x;
					// jump to image
					if (middleImagePos != -1) {
						gv.setSelection(middleImagePos - 1);
							x = middleImagePos - 1;
					// if no position was found: simply use first visible position
					} else {
						gv.setSelection(gv.getFirstVisiblePosition());
						x = gv.getFirstVisiblePosition();
					}
					
					// remember all first positions
					selectionMapping.put(position, x);
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// this method intentionally left blank
			}
		});
		// set the selection
		if (selectionMapping.indexOfKey(position) < 0) {
			selectionMapping.put(position, gv.getCount() - 1);
		}
		gv.setSelection(selectionMapping.get(position));
		
		// finally save a reference to the gridview
		gvMapping.put(position, gv);

		// create a new linear layout that wraps the gridview
		// always override the converted view
		convertView = new LinearLayout(context);
		convertView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, Value.DISPLAY_HEIGHT / 3));
		convertView.setBackgroundColor(Color.TRANSPARENT);
		((LinearLayout) convertView).setOrientation(LinearLayout.HORIZONTAL);
		((LinearLayout) convertView).setGravity(Gravity.CENTER);
		((LinearLayout) convertView).addView(gv);
		
		// finally return new view
        return convertView;
    }
    
    public List<ImageListAdapter> getAdapterList() {
    	return iaList;
    }
    
    public int getFirstVisiblePositionOfView(final int viewIndex) {
    	if (gvMapping.containsKey(viewIndex)) {
    		return gvMapping.get(viewIndex).getFirstVisiblePosition();
    	}
    	
    	return 0;
    }
}