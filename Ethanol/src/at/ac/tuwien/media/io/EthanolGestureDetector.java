package at.ac.tuwien.media.io;

import android.content.Context;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Toast;
import at.ac.tuwien.media.IImageSwipe;
import at.ac.tuwien.media.util.Values;

public class EthanolGestureDetector extends SimpleOnGestureListener {
	private IImageSwipe parent;
	
	public EthanolGestureDetector(IImageSwipe parent) {
		this.parent = parent;
	}
	
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > Values.SWIPE_MAX_OFF_PATH) {
            	if(e1.getY() - e2.getY() > Values.SWIPE_MIN_DISTANCE && Math.abs(velocityX) > Values.SWIPE_THRESHOLD_VELOCITY) {
	            	parent.nextImage(Values.FAST_SWIPE_INTERVAL);
	            	
	                Toast.makeText((Context) parent, "Up Swipe", Toast.LENGTH_SHORT).show();
	            }  else if (e2.getY() - e1.getY() > Values.SWIPE_MIN_DISTANCE && Math.abs(velocityX) > Values.SWIPE_THRESHOLD_VELOCITY) {
	            	parent.prevImage(Values.FAST_SWIPE_INTERVAL);
	            	
	                Toast.makeText((Context) parent, "Down Swipe", Toast.LENGTH_SHORT).show();
	            }            	
            } else {
	            if(e1.getX() - e2.getX() > Values.SWIPE_MIN_DISTANCE && Math.abs(velocityX) > Values.SWIPE_THRESHOLD_VELOCITY) {
	            	parent.nextImage(1);
	            	
	                Toast.makeText((Context) parent, "Left Swipe", Toast.LENGTH_SHORT).show();
	            }  else if (e2.getX() - e1.getX() > Values.SWIPE_MIN_DISTANCE && Math.abs(velocityX) > Values.SWIPE_THRESHOLD_VELOCITY) {
	            	parent.prevImage(1);
	            	
	                Toast.makeText((Context) parent, "Right Swipe", Toast.LENGTH_SHORT).show();
	            }
            }
        } catch (Exception e) {
            // nothing
        }
        
        return false;
    }

}
