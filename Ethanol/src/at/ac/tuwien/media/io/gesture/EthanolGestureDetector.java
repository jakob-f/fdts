package at.ac.tuwien.media.io.gesture;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import at.ac.tuwien.media.IEthanol;
import at.ac.tuwien.media.io.gesture.model.Swipe;
import at.ac.tuwien.media.util.Values;
import at.ac.tuwien.media.util.Values.EDirection;

/**
 * {@link EthanolGestureDetector} class is extends the {@link SimpleOnGestureListener} class
 * but also detects very slow movements.
 * It is able to detect and process swipes of type {@link Swipe} and taps.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class EthanolGestureDetector extends SimpleOnGestureListener {
	private IEthanol parent;
	private Point displaySize;
	private Point downEventPoint;
	private long downTapTime;
	
	public EthanolGestureDetector(IEthanol parent, Point displaySize) {
		this.parent = parent;
		this.displaySize = displaySize;
		
		downEventPoint = null;
		downTapTime = 0L;
	}

	public void setDownEvent(MotionEvent me) {
		downEventPoint = eventCoordinatesInPercent(me);
		downTapTime = System.currentTimeMillis();
	}

	// the first parameter e1 is provided for future compatibility
	public boolean onSwipe(MotionEvent e1, MotionEvent e2) {
		// ignores e1 and uses the previous saved down event Point instead
		// the point of the up event is e2
		
		// only count the motion as a swipe if a the swipe timeout passed
		// this helps us to distinguish it from a tap
		if ((downTapTime + Values.SWIPE_TIMEOUT_IN_MILLIS) < System.currentTimeMillis()) {
    		// which type of swipe do we have?
    		// - make the right move
	    	switch (ESwipeType.getSwipeType(downEventPoint, eventCoordinatesInPercent(e2))) {
		    	case SWIPE_RIGHT_ONE:
				case SWIPE_RIGHT_TWO:
					parent.skipToThumbnail(EDirection.PREVIOUS, 1);
					break;
				case SWIPE_FAST_RIGHT:
					parent.skipToThumbnail(EDirection.PREVIOUS, 2);
	    			break;
	    		case SWIPE_LEFT_ONE:
	    		case SWIPE_LEFT_TWO:
	    			parent.skipToThumbnail(EDirection.FORWARD, 1);
	    			break;
	    		case SWIPE_FAST_LEFT:
	    			parent.skipToThumbnail(EDirection.FORWARD, 2);
	    			break;
		    	case SWIPE_UP_FULL:
		    		parent.skipToThumbnail(EDirection.FORWARD, Values.SWIPE_INTERVAL_FAST);
					break;
		    	case SWIPE_UP_HALF:
		    		parent.skipToThumbnail(EDirection.FORWARD, Values.SWIPE_INTERVAL_HALF);
					break;
		    	case SWIPE_UP_SELECT:
					parent.skipToThumbnailFromRow(Values.HORIZONTAL_BOTTOM, downEventPoint.x);
					break;
	    		case SWIPE_DOWN_FULL:
	    			parent.skipToThumbnail(EDirection.PREVIOUS, Values.SWIPE_INTERVAL_FAST);
	    			break;
	    		case SWIPE_DOWN_HALF:
	    			parent.skipToThumbnail(EDirection.PREVIOUS, Values.SWIPE_INTERVAL_HALF);
	    			break;
	    		case SWIPE_DOWN_SELECT:
	    			parent.skipToThumbnailFromRow(Values.HORIZONTAL_TOP, downEventPoint.x);
	    			break;
	    		default:
	    			break;
	    	}

	    	// the event is consumed
	    	return true;
		}
		
		// the event is not consumed
    	return true;
    }
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// do not consume it
		return false;
	}
    
	@Override
	public boolean onSingleTapConfirmed(MotionEvent me) {
		// if the user tapped in the main picture fix or release it
		if (ERectangleType.THUMBNAIL_TWO.getRectangle().isPointInRectangle(eventCoordinatesInPercent(me))) {
			parent.fixOrReleaseCurrentThumbnail();
		}
		
		// the event is consumed
		return true;
	}
    
	@Override
	public boolean onDoubleTap(MotionEvent me) {
		// if the user double tapped in the main picture start a program
		if (ERectangleType.THUMBNAIL_TWO.getRectangle().isPointInRectangle(eventCoordinatesInPercent(me))) {
			parent.startExternalProgram();
		}
		
		// the event is consumed
		return true;
	}
    
    private Point eventCoordinatesInPercent(MotionEvent me) {
    	// convert the coordinates of the event to a value in percentage
    	// this makes it independable from the devices screen resolution
    	int x = (int) (me.getX() / (displaySize.x / 100.f));
    	int y = (int) (me.getY() / (displaySize.y / 100.f));
    	
    	// return a new point with the coordinates in percentage
    	return new Point(x, y);
    }
}
