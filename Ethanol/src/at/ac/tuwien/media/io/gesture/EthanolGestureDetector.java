package at.ac.tuwien.media.io.gesture;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import at.ac.tuwien.media.IImageSwipe;
import at.ac.tuwien.media.util.Values;

public class EthanolGestureDetector extends SimpleOnGestureListener {
	private IImageSwipe parent;
	private Point displaySize;
	
	public EthanolGestureDetector(IImageSwipe parent, Point displaySize) {
		this.parent = parent;
		this.displaySize = displaySize;
	}
	
	@Override
	public boolean onDown(MotionEvent me) {
	
		return super.onDown(me);
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
					parent.prevImage(1);
					break;
				case SWIPE_FAST_RIGHT:
	    			parent.prevImage(2);
	    			break;
	    		case SWIPE_LEFT_ONE:
	    		case SWIPE_LEFT_TWO:
	    			parent.nextImage(1);
	    			break;
	    		case SWIPE_FAST_LEFT:
	    			parent.nextImage(2);
	    			break;
		    	case SWIPE_UP_FULL:
					parent.nextImage(Values.FAST_SWIPE_INTERVAL);    			
					break;
		    	case SWIPE_UP_HALF:
					parent.jumpToImage(Values.HORIZONTAL_BOTTOM, getRowPercentage(startPoint));    			
					break;
	    		case SWIPE_DOWN_FULL:
	    			parent.prevImage(Values.FAST_SWIPE_INTERVAL);    			
	    			break;
	    		case SWIPE_DOWN_HALF:
	    			parent.jumpToImage(Values.HORIZONTAL_TOP, getRowPercentage(startPoint));    			
	    			break;
	    		default:
	    			break;
	    	}
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
    
    private int getRowPercentage(Point p) {
    	// get the percentage row rectangle
    	ERectangleType rect = ERectangleType.getRectangleInRowPercentegeFromPoint(p);
    	
    	// return the x value of the right edge with gives us the percentage
    	return rect.getRectangle().getB().x;
    }
}
