package at.ac.tuwien.media.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * {@link XListView} extends {@link ListView} to intercept touch events.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class XListView extends ListView {
	private static int leftBoundary;
	private static int rightBoundary;
	
	public XListView(Context context) {
		super(context);
		
		init();
	}

	public XListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		init();
	}

	public XListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init();
	}
	
	private void init() {
		XListView.leftBoundary = (Value.DISPLAY_WIDTH / 2) - (Value.THUMBNAIL_WIDTH + Value.THUMBNAIL_PADDING);
		XListView.rightBoundary = (Value.DISPLAY_WIDTH / 2) + (Value.THUMBNAIL_WIDTH + Value.THUMBNAIL_PADDING);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent me) {
		// disable the scroll in the center to enable flings in the gridview
		return !((me.getAction() == MotionEvent.ACTION_DOWN ||
				me.getAction() == MotionEvent.ACTION_UP ||
				me.getAction() == MotionEvent.ACTION_MOVE) &&
				(leftBoundary <= me.getX() && me.getX() <= rightBoundary));
	}
}
