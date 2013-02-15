package at.ac.tuwien.media.util;

import android.graphics.Point;

public enum ESwipe {
	SWIPE_RIGHT_ONE (new Swipe(ERectangles.THUMBNAIL_ONE, ERectangles.THUMBNAIL_TWO)),
	SWIPE_RIGHT_TWO (new Swipe(ERectangles.THUMBNAIL_TWO, ERectangles.THUMBNAIL_THREE)),	
	SWIPE_FAST_RIGHT (new Swipe(ERectangles.THUMBNAIL_ONE, ERectangles.THUMBNAIL_THREE)),
	SWIPE_LEFT_ONE (new Swipe(ERectangles.THUMBNAIL_TWO, ERectangles.THUMBNAIL_ONE)),
	SWIPE_LEFT_TWO (new Swipe(ERectangles.THUMBNAIL_THREE, ERectangles.THUMBNAIL_TWO)),
	SWIPE_FAST_LEFT (new Swipe(ERectangles.THUMBNAIL_THREE, ERectangles.THUMBNAIL_ONE)),
	SWIPE_UP (new Swipe(ERectangles.FRAME_BOTTOM, ERectangles.FRAME_TOP)),
	SWIPE_DOWN (new Swipe(ERectangles.FRAME_TOP, ERectangles.FRAME_BOTTOM)),
	SWIPE_NOOP (new Swipe(null, null));
	
	private Swipe swipe;
	
	private ESwipe(Swipe swipe) {
		this.swipe = swipe;
	}

	public Swipe getSwipe() {
		return swipe;
	}

	public void setSwipe(Swipe swipe) {
		this.swipe = swipe;
	}

	public static ESwipe getSwipeType(Point startPoint, Point endPoint) {
		Swipe compSwipe = new Swipe(ERectangles.getRectangleFromPoint(startPoint), ERectangles.getRectangleFromPoint(endPoint));
		
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