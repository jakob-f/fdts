package at.ac.tuwien.media.io.gesture;

import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import at.ac.tuwien.media.MainActivity;
import at.ac.tuwien.media.util.Configuration;
import at.ac.tuwien.media.util.Value;

public abstract class XGestureDetector extends SimpleOnGestureListener {
	protected MainActivity parent;
	protected Value.EThumbnailPostion thumbnailPosition;
	protected int indexNo;
	
	protected int leftEdge;
	protected int rightEdge;
	protected int topEdge;
	protected int bottomEdge;
	
	public XGestureDetector(final MainActivity parent, final int indexNo) {
		this.parent = parent;
		this.indexNo = indexNo;
		
		topEdge = (Value.DISPLAY_WIDTH / 2) + (Value.THUMBNAIL_HEIGHT / 2);
		bottomEdge = (Value.DISPLAY_WIDTH / 2) - (Value.THUMBNAIL_HEIGHT / 2);
	}
	
	@Override
	public boolean onDoubleTap(MotionEvent me) {
		if (Configuration.getAsBoolean(Value.CONFIG_TAP) &&
				isValid(me)) {
			insertDelete(true);
			
			// the event is consumed
			return true;
		}
		
		// the event is not consumed
		return false;
	}
	
	@Override
	public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
		// the start point of the fling must be valid
		if (Configuration.getAsBoolean(Value.CONFIG_SWIPE) &&
				isValid(me1)) {
			// only count the motion as a swipe if a the swipe timeout passed and nothing is fixed
			// this helps us to distinguish it from a tap
			// also the fling has to occur inside the x-axis of an image and must exceed its boundaries on the y-axis
			if ((me2.getDownTime() + Value.TIMEOUT_IN_MILLIS_FLING) < System.currentTimeMillis() &&
					Math.abs(me1.getY() - me2.getY()) <= Value.THUMBNAIL_WIDTH &&
					Math.abs(me1.getX() - me2.getX()) >= (Value.THUMBNAIL_HEIGHT / 2)) {
				
				// downward or upward fling?
				insertDelete(me1.getX() > me2.getX());
				
				// the event is consumed
				return false;
			}
		}
		
		// the event is not consumed
		return false;
	}
	
	@Override
	public boolean onSingleTapConfirmed(MotionEvent me) {
		if (Configuration.getAsBoolean(Value.CONFIG_TAP) &&
				isValid(me)) {
			insertDelete(false);
			
			// the event is consumed
			return true;
		}
		
		// the event is not consumed
		return false;
	}
	
	private void insertDelete(final boolean isDelete) {
		// insert current thumbnail in the next list
		if (!isDelete || (isDelete && Configuration.getAsBoolean(Value.CONFIG_INSERT_DELETE))) {
			final Value.EInsertListPosition insertListPosition = isDelete ? Value.EInsertListPosition.UP :
																				Value.EInsertListPosition.DOWN;
			parent.insert(indexNo, thumbnailPosition, insertListPosition);
		}
		
		// delete current thumbnail in the list
		if (isDelete || (!isDelete && Configuration.getAsBoolean(Value.CONFIG_INSERT_DELETE))) {
			parent.delete(indexNo, thumbnailPosition);
		}
	}
	
	protected abstract boolean isValid(final MotionEvent me);
}