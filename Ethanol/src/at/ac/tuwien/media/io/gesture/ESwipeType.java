package at.ac.tuwien.media.io.gesture;

import android.graphics.Point;
import at.ac.tuwien.media.io.gesture.model.Swipe;

public enum ESwipeType {
	SWIPE_RIGHT_ONE (new Swipe(ERectangleType.THUMBNAIL_ONE, ERectangleType.THUMBNAIL_TWO)),
	SWIPE_RIGHT_TWO (new Swipe(ERectangleType.THUMBNAIL_TWO, ERectangleType.THUMBNAIL_THREE)),	
	SWIPE_FAST_RIGHT (new Swipe(ERectangleType.THUMBNAIL_ONE, ERectangleType.THUMBNAIL_THREE)),
	SWIPE_LEFT_ONE (new Swipe(ERectangleType.THUMBNAIL_TWO, ERectangleType.THUMBNAIL_ONE)),
	SWIPE_LEFT_TWO (new Swipe(ERectangleType.THUMBNAIL_THREE, ERectangleType.THUMBNAIL_TWO)),
	SWIPE_FAST_LEFT (new Swipe(ERectangleType.THUMBNAIL_THREE, ERectangleType.THUMBNAIL_ONE)),
	SWIPE_UP (new Swipe(ERectangleType.FRAME_BOTTOM, ERectangleType.FRAME_TOP)),
	SWIPE_DOWN (new Swipe(ERectangleType.FRAME_TOP, ERectangleType.FRAME_BOTTOM)),
	SWIPE_NOOP (new Swipe(null, null));
	
	private Swipe swipe;
	
	private ESwipeType(Swipe swipe) {
		this.swipe = swipe;
	}

	public Swipe getSwipe() {
		return swipe;
	}

	public void setSwipe(Swipe swipe) {
		this.swipe = swipe;
	}

	public static ESwipeType getSwipeType(Point startPoint, Point endPoint) {
		Swipe compSwipe = new Swipe(ERectangleType.getRectangleFromPoint(startPoint), ERectangleType.getRectangleFromPoint(endPoint));
		
		if (compSwipe.equals(SWIPE_RIGHT_ONE.getSwipe())) {
			return SWIPE_RIGHT_ONE;
		} else if (compSwipe.equals(SWIPE_RIGHT_TWO.getSwipe())) {
			return SWIPE_RIGHT_TWO;
		} else if (compSwipe.equals(SWIPE_FAST_RIGHT.getSwipe())) {
			return SWIPE_FAST_RIGHT;
		} else if (compSwipe.equals(SWIPE_LEFT_ONE.getSwipe())) {
			return SWIPE_LEFT_ONE;
		} else if (compSwipe.equals(SWIPE_LEFT_TWO.getSwipe())) {
			return SWIPE_LEFT_TWO;
		} else if (compSwipe.equals(SWIPE_FAST_LEFT.getSwipe())) {
			return SWIPE_FAST_LEFT;
		} else if (compSwipe.equals(SWIPE_UP.getSwipe())) {
			return SWIPE_UP;
		} else if (compSwipe.equals(SWIPE_DOWN.getSwipe())) {
			return SWIPE_DOWN;
		}
		
		return SWIPE_NOOP;
	}
}