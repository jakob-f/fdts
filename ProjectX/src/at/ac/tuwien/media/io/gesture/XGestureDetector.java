package at.ac.tuwien.media.io.gesture;

import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import at.ac.tuwien.media.MainActivity;
import at.ac.tuwien.media.util.Value;

public abstract class XGestureDetector extends SimpleOnGestureListener {
	protected MainActivity parent;
	protected Value.EThumbnailPostion thumbnailPostion;
	protected int leftEdge;
	protected int rightEdge;
	protected int topEdge;
	protected int bottomEdge;
	
	public XGestureDetector(final MainActivity parent, final Value.EThumbnailPostion thumbnailPostion) {
		this.parent = parent;
		this.thumbnailPostion = thumbnailPostion;
		
		final int centerHorizontal = 600; // TODO calculate this programatically with the screen size
		topEdge = centerHorizontal + (Value.THUMBNAIL_HEIGHT / 2);
		bottomEdge = centerHorizontal - (Value.THUMBNAIL_HEIGHT / 2);
	}
	
	@Override
	public boolean onDoubleTap(MotionEvent me) {
		if (isValid(me)) {
			parent.abortInsert(thumbnailPostion);
			
			// event is consumed
			return true;
		}
		
		// event is not consumed
		return false;
	}
	
	@Override
	public boolean onDown(MotionEvent me) {
		if (isValid(me)) {
			parent.prepareInsert(thumbnailPostion);
			
			// event is consumed
			return true;
		}

		// event is not consumed
		return false;
	}
	
	@Override
	public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
		// the start point of the fling must be valid
		if (isValid(me1)) {
			// only count the motion as a swipe if a the swipe timeout passed and nothing is fixed
			// this helps us to distinguish it from a tap
			// also the fling has to occur inside the x-axis of an image and must exceed its boundaries on the y-axis
			if ((me2.getDownTime() + Value.TIMEOUT_IN_MILLIS_FLING) < System.currentTimeMillis() &&
					Math.abs(me1.getY() - me2.getY()) <= Value.THUMBNAIL_WIDTH &&
					Math.abs(me1.getX() - me2.getX()) >= (Value.THUMBNAIL_HEIGHT / 2)) {
				
				// insert current thumbnail in the next list
				parent.insert(thumbnailPostion);
			} else {
				parent.abortInsert(thumbnailPostion);
			}
		
			// event is consumed
			return true;
		}
		
		// event is not consumed
		return false;
	}
	
	@Override
	public void onLongPress(MotionEvent me) {
		if (isValid(me)) {
			parent.abortInsert(thumbnailPostion);
		}
	}
	
	@Override
	public boolean onSingleTapUp(MotionEvent me) {
		if (isValid(me)) {
			parent.abortInsert(thumbnailPostion);
			
			// event is consumed
			return true;
		}
		
		// event is not consumed
		return false;
	}
	
	protected abstract boolean isValid(final MotionEvent me);
}