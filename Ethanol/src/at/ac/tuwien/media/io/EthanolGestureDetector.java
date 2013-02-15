package at.ac.tuwien.media.io;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import at.ac.tuwien.media.IImageSwipe;
import at.ac.tuwien.media.util.ESwipe;
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
    	// was the swipe fast enough?
    	if (Math.abs(velocityX) > Values.SWIPE_THRESHOLD_VELOCITY) {
        	ESwipe swipeType = ESwipe.getSwipeType(calcPointInPercent(e1), calcPointInPercent(e2));
        	
	    	switch (swipeType) {
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
		    	case SWIPE_UP:
					parent.nextImage(Values.FAST_SWIPE_INTERVAL);    			
					break;
	    		case SWIPE_DOWN:
	    			parent.prevImage(Values.FAST_SWIPE_INTERVAL);    			
	    			break;
	    		default:
	    			break;
	    	}
    	}
    	
    	return false;
    }
    
    private Point calcPointInPercent(MotionEvent me) {
    	int x = (int) (me.getX() / (displaySize.x / 100.f));
    	int y = (int) (me.getY() / (displaySize.y / 100.f));
    	
    	return new Point(x, y);
    }
}
