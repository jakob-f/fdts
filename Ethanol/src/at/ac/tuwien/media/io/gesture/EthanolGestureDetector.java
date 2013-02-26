package at.ac.tuwien.media.io.gesture;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import at.ac.tuwien.media.IImageSwipe;
import at.ac.tuwien.media.util.Values;
import at.ac.tuwien.media.util.Values.EDirection;
import at.ac.tuwien.media.util.Values.EProgram;

public class EthanolGestureDetector extends SimpleOnGestureListener {
	private IImageSwipe parent;
	private Point displaySize;
	
	public EthanolGestureDetector(IImageSwipe parent, Point displaySize) {
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
					parent.skipToImage(EDirection.PREVIOUS, 1);
					break;
				case SWIPE_FAST_RIGHT:
					parent.skipToImage(EDirection.PREVIOUS, 2);
	    			break;
	    		case SWIPE_LEFT_ONE:
	    		case SWIPE_LEFT_TWO:
	    			parent.skipToImage(EDirection.FORWARD, 1);
	    			break;
	    		case SWIPE_FAST_LEFT:
	    			parent.skipToImage(EDirection.FORWARD, 2);
	    			break;
		    	case SWIPE_UP_FULL:
		    		parent.skipToImage(EDirection.FORWARD, Values.SWIPE_INTERVAL_FAST);
					break;
		    	case SWIPE_UP_HALF:
		    		parent.skipToImage(EDirection.FORWARD, Values.SWIPE_INTERVAL_HALF);
					break;
		    	case SWIPE_UP_SELECT:
					parent.skipToImageFromRow(Values.HORIZONTAL_BOTTOM, startPoint.x);
					break;
	    		case SWIPE_DOWN_FULL:
	    			parent.skipToImage(EDirection.PREVIOUS, Values.SWIPE_INTERVAL_FAST);
	    			break;
	    		case SWIPE_DOWN_HALF:
	    			parent.skipToImage(EDirection.PREVIOUS, Values.SWIPE_INTERVAL_HALF);
	    			break;
	    		case SWIPE_DOWN_SELECT:
	    			parent.skipToImageFromRow(Values.HORIZONTAL_TOP, startPoint.x);
	    			break;
	    		case SWIPE_PROG_1:
	    			parent.startExternalProgram(EProgram.PROG_1);
	    			break;
	    		case SWIPE_PROG_2:
	    			parent.startExternalProgram(EProgram.PROG_2);
	    			break;
	    		default:
	    			break;
	    	}
    	}
    	
    	// always return false
    	return false;
    }
    
    @Override
	public boolean onDoubleTap(MotionEvent e) {
		// if the user tapped in the main picture fix or release it
		if (ERectangleType.THUMBNAIL_TWO.getRectangle().isPointInRectangle(calcPointInPercent(e))) {
			parent.fixOrReleaseImage();
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
