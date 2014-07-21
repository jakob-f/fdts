package at.ac.tuwien.media.methanol.io.gesture;

import android.graphics.Point;
import android.os.Handler;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import at.ac.tuwien.media.methanol.IMethanol;
import at.ac.tuwien.media.methanol.io.gesture.model.ERectangleType;
import at.ac.tuwien.media.methanol.io.gesture.model.ESwipeType;
import at.ac.tuwien.media.methanol.io.gesture.model.Swipe;
import at.ac.tuwien.media.methanol.util.Configuration;
import at.ac.tuwien.media.methanol.util.Value;
import at.ac.tuwien.media.methanol.util.Value.EDirection;

/**
 * {@link MethanolGestureDetector} class extends the {@link SimpleOnGestureListener} class.
 * but also detects very slow motions.
 * It is able to detect and process swipes of type {@link Swipe} and taps.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class MethanolGestureDetector extends SimpleOnGestureListener {
	private static IMethanol methanol;
	private static Point displaySize;
	private Point downEventPoint;
	private boolean isFIAR;
	
	private long timeout;
    private final Handler handler;
    private EDirection slideDirection;
	
	public MethanolGestureDetector(final IMethanol methanol, final Point displaySize) {
		MethanolGestureDetector.methanol = methanol;
		MethanolGestureDetector.displaySize = displaySize;
		
		downEventPoint = null;
		
		handler = new Handler();
	}

	@Override
	public boolean onDown(final MotionEvent me) {
		downEventPoint = eventCoordinatesInPercent(me);
		
		// the event is consumed
		return true;
	}
	
	public boolean onUp(final MotionEvent me) {
		// try to release
		if (isFIAR) {
			final ERectangleType eventRect = ERectangleType.getRectangleFromPoint(eventCoordinatesInPercent(me));
			
			// check if the we are in the upper or lower row (or slide line)
			if (eventRect != ERectangleType.ROW_TOP && eventRect != ERectangleType.ROW_BOTTOM) {
				methanol.resetFIAR();
			}
			
			// release
			methanol.fixOrReleaseCurrentThumbnail();
			
			// stop slider (if running)
			handler.removeCallbacks(handlerTask);
			
			// close FIAR
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
		// ignores me1 and uses the previous saved down event Point instead
		// the point of the up event is me2
		
		// only count the motion as a swipe if a the swipe timeout passed and nothing is fixed
		// this helps us to distinguish it from a tap
		if ((me2.getDownTime() + Value.TIMEOUT_IN_MILLIS_SWIPE) < System.currentTimeMillis()) {
    		// which type of swipe do we have?
    		// - make the right move
			final Point upEventPoint = eventCoordinatesInPercent(me2);
	    	switch (ESwipeType.getSwipeType(downEventPoint, upEventPoint)) {
	    		case SWIPE_SIMPLE:
	    			// check min distance
		    		if (Configuration.getAsBoolean(Value.CONFIG_SWIPE_SIMPLE) &&	
		    				Math.abs(downEventPoint.x - upEventPoint.x) > Value.SWIPE_MIN_DISTANCE) {
		    			// get direction and perform swipe
		    			final EDirection direction = downEventPoint.x < upEventPoint.x ?
		    													EDirection.PREVIOUS
		    													: EDirection.FORWARD;
		    			methanol.skipToThumbnail(direction, 1);
		    		}
		    		break;
		    	case SWIPE_RIGHT_ONE:
				case SWIPE_RIGHT_TWO:
					methanol.skipToThumbnail(EDirection.PREVIOUS, 1);
					break;
				case SWIPE_RIGHT_FAST:
					methanol.skipToThumbnail(EDirection.PREVIOUS, 2);
	    			break;
	    		case SWIPE_LEFT_ONE:
	    		case SWIPE_LEFT_TWO:
	    			methanol.skipToThumbnail(EDirection.FORWARD, 1);
	    			break;
	    		case SWIPE_LEFT_FAST:
	    			methanol.skipToThumbnail(EDirection.FORWARD, 2);
	    			break;
		    	case SWIPE_UP_FULL:
		    		if (Configuration.getAsBoolean(Value.CONFIG_SWIPE_VERTICAL)) {
		    			methanol.skipToThumbnail(EDirection.FORWARD, Value.SWIPE_INTERVAL_FAST);
		    		}
					break;
		    	case SWIPE_UP_HALF:
		    		if (Configuration.getAsBoolean(Value.CONFIG_SWIPE_VERTICAL)) {
		    			methanol.skipToThumbnail(EDirection.FORWARD, Value.SWIPE_INTERVAL_HALF);
		    		}
					break;
		    	case SWIPE_UP_SELECT:
		    		if (Configuration.getAsBoolean(Value.CONFIG_SWIPE_SELECT)) {
		    			methanol.skipToThumbnail(ERectangleType.ROW_BOTTOM, downEventPoint.x);
		    		}
					break;
	    		case SWIPE_DOWN_FULL:
	    			if (Configuration.getAsBoolean(Value.CONFIG_SWIPE_VERTICAL)) {
	    				methanol.skipToThumbnail(EDirection.PREVIOUS, Value.SWIPE_INTERVAL_FAST);
	    			}
	    			break;
	    		case SWIPE_DOWN_HALF:
	    			if (Configuration.getAsBoolean(Value.CONFIG_SWIPE_VERTICAL)) {
	    				methanol.skipToThumbnail(EDirection.PREVIOUS, Value.SWIPE_INTERVAL_HALF);
	    			}
	    			break;
	    		case SWIPE_DOWN_SELECT:
	    			if (Configuration.getAsBoolean(Value.CONFIG_SWIPE_SELECT)) {
	    				methanol.skipToThumbnail(ERectangleType.ROW_TOP, downEventPoint.x);
	    			}
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
		final ERectangleType downEventRect = ERectangleType.getRectangleFromPoint(downEventPoint);
		final Point  eventPoint = eventCoordinatesInPercent(me);
		final ERectangleType eventRect = ERectangleType.getRectangleFromPoint(eventPoint);
		
		// do we have a scroll swipe?
		// i.e. the swipe has to start in the top or bottom row (and must continue there)
		// and it must be enable in the configuration
		if (Configuration.getAsBoolean(Value.CONFIG_SWIPE_SCROLL)
				&& (downEventRect == ERectangleType.ROW_TOP || downEventRect == ERectangleType.ROW_BOTTOM)
				&& eventRect != null && eventRect.equals(downEventRect)
				&& (Math.abs(eventPoint.x - downEventPoint.x) > 0)) {
			// scroll to thumbnail (save current time)	
			methanol.scrollToThumbnail(eventRect, downEventPoint.x, eventPoint.x);
			// set new down point for next scroll interval
			downEventPoint = eventPoint;
			
		// only count the motion as a swipe if we are already in the FIAR mode
		// this must have been activated by a long press event on the center thumbnail
		} else if (isFIAR) {
			// check if we are in the bottom line (slide swipe must be enabled)
			if ((eventPoint.y >= Value.HORIZONTAL_BOTTOM_LINE)
					&& Configuration.getAsBoolean(Value.CONFIG_SWIPE_SLIDE)) {
				// show the slider
				methanol.showSlider(true, downEventPoint.x / 100.0f);
				
				// calculate timeout
				final long newTimeout = calculateTimeout(eventPoint.x, downEventPoint.x);
				
				// only start new slider thread task if timeout value has changed
				if (newTimeout != timeout) {
					// set timeout
					timeout = newTimeout;
					
					// calculate slide direction
					slideDirection = (eventPoint.x - downEventPoint.x) < 0 ?
			    			 EDirection.PREVIOUS
			    			 : EDirection.FORWARD;
					
					// restart handler task
					restartHandlerTask();
				}
					
				return true;
					
			// check if we are in the upper or lower row
			} else if (eventRect == ERectangleType.ROW_TOP || eventRect == ERectangleType.ROW_BOTTOM) {
				// stop handler task (if running)
				handler.removeCallbacks(handlerTask);
				// move the images
				methanol.skipToThumbnail(eventRect, eventPoint.x);
				
				return true;
			
			// we are in the center row
			} else {
				// stop handler task (if running)
				handler.removeCallbacks(handlerTask);
				timeout = -1;
				
				// reset FIAR to the original (fixed) image
				methanol.resetFIAR();
			}
		}
		
		// the event was not consumed
		return false;
	}
	
	// method is replaced by onSwipe()
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
		// if the double tap happens in the center thumbnail show it in a whole screen single view
		if (ERectangleType.getRectangleFromPoint(eventCoordinatesInPercent(me)) == ERectangleType.THUMBNAIL_TWO) {
			methanol.showCurrentThumbnail();
			
			// the event was consumed
			return true;
		}
		
		// the event was not consumed
		return false;
	}
	
	@Override
	public void onLongPress(MotionEvent me) {
		// if the double tap happens in the center thumbnail try to activate FIAR mode
		if (ERectangleType.getRectangleFromPoint(eventCoordinatesInPercent(me)) == ERectangleType.THUMBNAIL_TWO) {
			if (!isFIAR) {
				methanol.fixOrReleaseCurrentThumbnail();
				
				isFIAR = true;
			}
		} else {
			super.onLongPress(me);
		}
	}
    
	private long calculateTimeout(int a, int b) {
		final int diff = Math.abs(a - b);
		
		return (diff > 50) ? 125
				: (diff > 40) ? 250
				: (diff > 30) ? 500
						: (diff > 20) ? 750
								: (diff > 10) ? 1000
										: 2000;
	}
	
	private Point eventCoordinatesInPercent(final MotionEvent me) {
		// convert the coordinates of the event to a value in percentage
		// this makes it independable from the devices screen resolution
		int x = (int) (me.getX() / (displaySize.x / 100.f));
		int y = (int) (me.getY() / (displaySize.y / 100.f));
	
		// return a new point with the coordinates in percentage
		return new Point(x, y);
	}

	private final Runnable handlerTask = new Runnable() {
		@Override
		public void run() {
			// skip to the next thumbnail and wait
			methanol.skipToThumbnail(slideDirection, 1);
			handler.postDelayed(handlerTask, Math.abs(timeout));
		}
	};
	
	private void restartHandlerTask() {
		// stop old handler task (if running)
		handler.removeCallbacks(handlerTask);
		
		// start new handler task
		handlerTask.run();
	}
}