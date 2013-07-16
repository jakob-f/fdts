package at.ac.tuwien.media.io.gesture;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import at.ac.tuwien.media.IEthanol;
import at.ac.tuwien.media.io.gesture.model.Swipe;
import at.ac.tuwien.media.util.Value;
import at.ac.tuwien.media.util.Value.EDirection;

/**
 * {@link EthanolGestureDetector} class extends the {@link SimpleOnGestureListener} class
 * but also detects very slow motions.
 * It is able to detect and process swipes of type {@link Swipe} and taps.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class EthanolGestureDetector extends SimpleOnGestureListener {
	private IEthanol ethanol;
	private Point displaySize;
	private Point downEventPoint;
	private long downTapTime;
	private boolean isFIAR;
	
	public EthanolGestureDetector(final IEthanol ethanol, final Point displaySize) {
		this.ethanol = ethanol;
		this.displaySize = displaySize;
		
		downEventPoint = null;
		downTapTime = 0L;
	}

	@Override
	public boolean onDown(final MotionEvent me) {
		downEventPoint = eventCoordinatesInPercent(me);
		downTapTime = System.currentTimeMillis();
		
		System.out.println(ERectangleType.getRectangleFromPoint(downEventPoint));
		
		// the event is consumed
		return true;
	}
	
	public boolean onUp(final MotionEvent me) {
		// try to release
		if (isFIAR) {
			final ERectangleType eventRect = ERectangleType.getRectangleFromPoint(eventCoordinatesInPercent(me));
			
			// check if the we are in the upper or lower row
			ethanol.fixOrReleaseCurrentThumbnail(eventRect != ERectangleType.ROW_TOP && 
					eventRect != ERectangleType.ROW_BOTTOM);
			
			isFIAR = false;
		// else try to swipe
		} else {
			onSwipe(null, me);
		}
		
		// the event is consumed
		return true;
	}
	
	// the first parameter e1 is provided for future compatibility
	private boolean onSwipe(final MotionEvent me1, final MotionEvent me2) {
		// ignores e1 and uses the previous saved down event Point instead
		// the point of the up event is e2
		
		// only count the motion as a swipe if a the swipe timeout passed and nothing is fixed
		// this helps us to distinguish it from a tap
		if ((downTapTime + Value.TIMEOUT_IN_MILLIS_SWIPE) < System.currentTimeMillis()) {
    		// which type of swipe do we have?
    		// - make the right move
	    	switch (ESwipeType.getSwipeType(downEventPoint, eventCoordinatesInPercent(me2))) {
		    	case SWIPE_RIGHT_ONE:
				case SWIPE_RIGHT_TWO:
					ethanol.skipToThumbnail(EDirection.PREVIOUS, 1);
					break;
				case SWIPE_FAST_RIGHT:
					ethanol.skipToThumbnail(EDirection.PREVIOUS, 2);
	    			break;
	    		case SWIPE_LEFT_ONE:
	    		case SWIPE_LEFT_TWO:
	    			ethanol.skipToThumbnail(EDirection.FORWARD, 1);
	    			break;
	    		case SWIPE_FAST_LEFT:
	    			ethanol.skipToThumbnail(EDirection.FORWARD, 2);
	    			break;
		    	case SWIPE_UP_FULL:
		    		ethanol.skipToThumbnail(EDirection.FORWARD, Value.SWIPE_INTERVAL_FAST);
					break;
		    	case SWIPE_UP_HALF:
		    		ethanol.skipToThumbnail(EDirection.FORWARD, Value.SWIPE_INTERVAL_HALF);
					break;
		    	case SWIPE_UP_SELECT:
					ethanol.skipToThumbnailFromRow(ERectangleType.ROW_BOTTOM, downEventPoint.x);
					break;
	    		case SWIPE_DOWN_FULL:
	    			ethanol.skipToThumbnail(EDirection.PREVIOUS, Value.SWIPE_INTERVAL_FAST);
	    			break;
	    		case SWIPE_DOWN_HALF:
	    			ethanol.skipToThumbnail(EDirection.PREVIOUS, Value.SWIPE_INTERVAL_HALF);
	    			break;
	    		case SWIPE_DOWN_SELECT:
	    			ethanol.skipToThumbnailFromRow(ERectangleType.ROW_TOP, downEventPoint.x);
	    			break;
	    		default:
	    			break;
	    	}

	    	// the event is consumed
	    	return true;
		}
		
		// the event is not consumed
    	return false;
    }
	
	public boolean onMove(final MotionEvent me) {
		// only count the motion as a swipe if a the fiar timeout passed
		// this helps us to distinguish it from a tap or a normal swipe
		if ((downTapTime + Value.TIMEOUT_IN_MILLIS_FIAR) < System.currentTimeMillis()) {
			final Point eventPoint = eventCoordinatesInPercent(me);
			final ERectangleType eventRect = ERectangleType.getRectangleFromPoint(eventPoint);
			
			// check if the we are in the upper or lower row
			if (eventRect == ERectangleType.ROW_TOP || 
					eventRect == ERectangleType.ROW_BOTTOM) {
				// fix the current image if it was not fix before
				if (!isFIAR) {
					ethanol.fixOrReleaseCurrentThumbnail(false);
					
					isFIAR = true;
				}
				
				// move the images
				ethanol.skipToThumbnailFromRow(eventRect, eventPoint.x);

				return true;
			} else if (isFIAR) {
				ethanol.fixOrReleaseCurrentThumbnail(true);
				
				isFIAR = false;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean onFling(final MotionEvent me1, final MotionEvent me2, float velocityX, float velocityY) {
		// do not consume it
		return false;
	}
    
	@Override
	public boolean onSingleTapConfirmed(final MotionEvent me) {
		// do not consume it
		return false;
	}
    
	@Override
	public boolean onDoubleTap(final MotionEvent me) {
		// do not consume it
		return false;
	}
    
    private Point eventCoordinatesInPercent(final MotionEvent me) {
    	// convert the coordinates of the event to a value in percentage
    	// this makes it independable from the devices screen resolution
    	int x = (int) (me.getX() / (displaySize.x / 100.f));
    	int y = (int) (me.getY() / (displaySize.y / 100.f));
    	
    	// return a new point with the coordinates in percentage
    	return new Point(x, y);
    }
}
