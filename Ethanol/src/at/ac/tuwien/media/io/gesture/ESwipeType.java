package at.ac.tuwien.media.io.gesture;

import android.graphics.Point;
import at.ac.tuwien.media.io.gesture.model.Swipe;

//class with fixed values for swipes
public enum ESwipeType {
	SWIPE_RIGHT_ONE (new Swipe(ERectangleType.THUMBNAIL_ONE, ERectangleType.THUMBNAIL_TWO)),
	SWIPE_RIGHT_TWO (new Swipe(ERectangleType.THUMBNAIL_TWO, ERectangleType.THUMBNAIL_THREE)),	
	SWIPE_FAST_RIGHT (new Swipe(ERectangleType.THUMBNAIL_ONE, ERectangleType.THUMBNAIL_THREE)),
	SWIPE_LEFT_ONE (new Swipe(ERectangleType.THUMBNAIL_TWO, ERectangleType.THUMBNAIL_ONE)),
	SWIPE_LEFT_TWO (new Swipe(ERectangleType.THUMBNAIL_THREE, ERectangleType.THUMBNAIL_TWO)),
	SWIPE_FAST_LEFT (new Swipe(ERectangleType.THUMBNAIL_THREE, ERectangleType.THUMBNAIL_ONE)),
	SWIPE_UP_FULL (new Swipe(ERectangleType.ROW_FULL_BOTTOM, ERectangleType.ROW_FULL_TOP)),
	SWIPE_UP_HALF (new Swipe(ERectangleType.ROW_FULL_BOTTOM, ERectangleType.THUMBNAIL_TWO)),
	SWIPE_DOWN_FULL (new Swipe(ERectangleType.ROW_FULL_TOP, ERectangleType.ROW_FULL_BOTTOM)),
	SWIPE_DOWN_HALF (new Swipe(ERectangleType.ROW_FULL_TOP, ERectangleType.THUMBNAIL_TWO)),
	SWIPE_NOOP (new Swipe(null, null));
	
	private Swipe swipe;
	
	private ESwipeType(Swipe swipe) {
		this.swipe = swipe;
	}

	public Swipe getSwipe() {
		return swipe;
	}

	public static ESwipeType getSwipeType(Point startPoint, Point endPoint) {
		// calculate the swipe
		Swipe compSwipe = new Swipe(ERectangleType.getRectangleFromPoint(startPoint), ERectangleType.getRectangleFromPoint(endPoint));
		
		// determine which swipe we have
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
		} else if (compSwipe.equals(SWIPE_UP_FULL.getSwipe())) {
			return SWIPE_UP_FULL;
		} else if (compSwipe.equals(SWIPE_UP_HALF.getSwipe())) {
			return SWIPE_UP_HALF;
		} else if (compSwipe.equals(SWIPE_DOWN_FULL.getSwipe())) {
			return SWIPE_DOWN_FULL;
		} else if (compSwipe.equals(SWIPE_DOWN_HALF.getSwipe())) {
			return SWIPE_DOWN_HALF;
		}
		
		// return an empty swipe if couldn't a suitable one
		return SWIPE_NOOP;
	}
}