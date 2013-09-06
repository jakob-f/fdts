package at.ac.tuwien.media.io.gesture;

import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import at.ac.tuwien.media.MainActivity;

public class XGestureDetector extends SimpleOnGestureListener {
	private MainActivity parent;
	
	public XGestureDetector(final MainActivity parent) {
		this.parent = parent;
	}
	
	@Override
	public boolean onDoubleTap(final MotionEvent me) {
		
		System.out.println("!!!!");
		
		// the event was consumed
		return true;
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO disable fling here
		return false;
	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO disable fling here
		return false;
	}
}
