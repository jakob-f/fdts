package at.ac.tuwien.media.io.gesture;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import at.ac.tuwien.media.IEthanol;
import at.ac.tuwien.media.io.gesture.model.Swipe;
import at.ac.tuwien.media.util.Values;
import at.ac.tuwien.media.util.Values.EDirection;

/**
 * {@link EthanolGestureDetector} class extends the {@link SimpleOnGestureListener} class.
 * It is able to detect and process swipes of type {@link Swipe} and double taps.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class EthanolGestureDetector extends SimpleOnGestureListener {
	private IEthanol parent;
	private Point displaySize;
	
	public EthanolGestureDetector(IEthanol parent, Point displaySize) {
		this.parent = parent;
		this.displaySize = displaySize;
	}
	
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    	// was the swipe fast enough? if not do nothing.
    	if (Math.abs(velocityX) > Values.SWIPE_THRESHOLD_VELOCITY
    			&& Math.abs(velocityY) > Values.SWIPE_THRESHOLD_VELOCITY) {
    		
    		// calculate the start point of the gesture - maybe we'll need it later
    		Point startPoint = calcPointInPercent(e1);
    		
    		// which type of swipe do we have?
    		// the make the right move
	    	switch (ESwipeType.getSwipeType(startPoint, calcPointInPercent(e2))) {
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
					parent.skipToThumbnailFromRow(Values.HORIZONTAL_BOTTOM, startPoint.x);
					break;
	    		case SWIPE_DOWN_FULL:
	    			parent.skipToThumbnail(EDirection.PREVIOUS, Values.SWIPE_INTERVAL_FAST);
	    			break;
	    		case SWIPE_DOWN_HALF:
	    			parent.skipToThumbnail(EDirection.PREVIOUS, Values.SWIPE_INTERVAL_HALF);
	    			break;
	    		case SWIPE_DOWN_SELECT:
	    			parent.skipToThumbnailFromRow(Values.HORIZONTAL_TOP, startPoint.x);
	    			break;
	    		default:
	    			break;
	    	}
    	}
    	
    	// always return false
    	return false;
    }
    
    @Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// if the user tapped in the main picture fix or release it
		if (ERectangleType.THUMBNAIL_TWO.getRectangle().isPointInRectangle(calcPointInPercent(e))) {
			parent.fixOrReleaseCurrentThumbnail();
		}
		
		// always return false
		return false;
	}
    
    @Override
	public boolean onDoubleTap(MotionEvent e) {
		// if the user double tapped in the main picture start a program
		if (ERectangleType.THUMBNAIL_TWO.getRectangle().isPointInRectangle(calcPointInPercent(e))) {
			parent.startExternalProgram();
		}
		
		// always return false
		return false;
	}
    
    private Point calcPointInPercent(MotionEvent me) {
    	// convert the coordinates of the event to a value in percentage
    	// this makes it independable from the devices screen resolution
    	int x = (int) (me.getX() / (displaySize.x / 100.f));
    	int y = (int) (me.getY() / (displaySize.y / 100.f));
    	
    	// return a new point with the coordinates in percentage
    	return new Point(x, y);
    }
}
