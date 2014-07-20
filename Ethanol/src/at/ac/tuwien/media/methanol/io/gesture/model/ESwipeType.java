package at.ac.tuwien.media.methanol.io.gesture.model;

import android.graphics.Point;

/**
 * The {@link ESwipeType} enum contains predefined start and end {@link ERectangleType} types for swipes.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public enum ESwipeType {
	SWIPE_SIMPLE (new Swipe(ERectangleType.THUMBNAIL_TWO, ERectangleType.THUMBNAIL_TWO)),
	SWIPE_RIGHT_ONE (new Swipe(ERectangleType.THUMBNAIL_ONE, ERectangleType.THUMBNAIL_TWO)),
	SWIPE_RIGHT_TWO (new Swipe(ERectangleType.THUMBNAIL_TWO, ERectangleType.THUMBNAIL_THREE)),	
	SWIPE_RIGHT_FAST (new Swipe(ERectangleType.THUMBNAIL_ONE, ERectangleType.THUMBNAIL_THREE)),
	SWIPE_LEFT_ONE (new Swipe(ERectangleType.THUMBNAIL_TWO, ERectangleType.THUMBNAIL_ONE)),
	SWIPE_LEFT_TWO (new Swipe(ERectangleType.THUMBNAIL_THREE, ERectangleType.THUMBNAIL_TWO)),
	SWIPE_LEFT_FAST (new Swipe(ERectangleType.THUMBNAIL_THREE, ERectangleType.THUMBNAIL_ONE)),
	SWIPE_UP_FULL (new Swipe(ERectangleType.ROW_BOTTOM, ERectangleType.ROW_TOP)),
	SWIPE_UP_HALF (new Swipe(ERectangleType.THUMBNAIL_ONE, ERectangleType.ROW_TOP)),
	SWIPE_UP_HALF_2 (new Swipe(ERectangleType.THUMBNAIL_TWO, ERectangleType.ROW_TOP)),
	SWIPE_UP_HALF_3 (new Swipe(ERectangleType.THUMBNAIL_THREE, ERectangleType.ROW_TOP)),
	SWIPE_UP_SELECT (new Swipe(ERectangleType.ROW_BOTTOM, ERectangleType.THUMBNAIL_TWO)),
	SWIPE_DOWN_FULL (new Swipe(ERectangleType.ROW_TOP, ERectangleType.ROW_BOTTOM)),
	SWIPE_DOWN_HALF (new Swipe(ERectangleType.THUMBNAIL_ONE, ERectangleType.ROW_BOTTOM)),
	SWIPE_DOWN_HALF_2 (new Swipe(ERectangleType.THUMBNAIL_TWO, ERectangleType.ROW_BOTTOM)),
	SWIPE_DOWN_HALF_3 (new Swipe(ERectangleType.THUMBNAIL_THREE, ERectangleType.ROW_BOTTOM)),
	SWIPE_DOWN_SELECT (new Swipe(ERectangleType.ROW_TOP, ERectangleType.THUMBNAIL_TWO)),
	SWIPE_NOOP (new Swipe(null, null));
	
	private Swipe swipe;
	
	private ESwipeType(final Swipe swipe) {
		this.swipe = swipe;
	}

	public Swipe getSwipe() {
		return swipe;
	}

	public static ESwipeType getSwipeType(final Point startPoint, final Point endPoint) {
		// calculate the swipe
		Swipe compSwipe = new Swipe(ERectangleType.getRectangleFromPoint(startPoint), ERectangleType.getRectangleFromPoint(endPoint));
		
		// determine which swipe we have
		if (compSwipe.equals(SWIPE_SIMPLE.getSwipe())) {
			return SWIPE_SIMPLE;
		} else if (compSwipe.equals(SWIPE_RIGHT_ONE.getSwipe())) {
			return SWIPE_RIGHT_ONE;
		} else if (compSwipe.equals(SWIPE_RIGHT_TWO.getSwipe())) {
			return SWIPE_RIGHT_TWO;
		} else if (compSwipe.equals(SWIPE_RIGHT_FAST.getSwipe())) {
			return SWIPE_RIGHT_FAST;
		} else if (compSwipe.equals(SWIPE_LEFT_ONE.getSwipe())) {
			return SWIPE_LEFT_ONE;
		} else if (compSwipe.equals(SWIPE_LEFT_TWO.getSwipe())) {
			return SWIPE_LEFT_TWO;
		} else if (compSwipe.equals(SWIPE_LEFT_FAST.getSwipe())) {
			return SWIPE_LEFT_FAST;
		} else if (compSwipe.equals(SWIPE_UP_FULL.getSwipe())) {
			return SWIPE_UP_FULL;
		} else if (compSwipe.equals(SWIPE_UP_HALF.getSwipe()) 
				|| compSwipe.equals(SWIPE_UP_HALF_2.getSwipe())
				|| compSwipe.equals(SWIPE_UP_HALF_3.getSwipe())) {
			return SWIPE_UP_HALF;
		} else if (compSwipe.equals(SWIPE_UP_SELECT.getSwipe())) {
			return SWIPE_UP_SELECT;
		} else if (compSwipe.equals(SWIPE_DOWN_FULL.getSwipe())) {
			return SWIPE_DOWN_FULL;
		} else if (compSwipe.equals(SWIPE_DOWN_HALF.getSwipe())
				|| compSwipe.equals(SWIPE_DOWN_HALF_2.getSwipe())
				|| compSwipe.equals(SWIPE_DOWN_HALF_3.getSwipe())) {
			return SWIPE_DOWN_HALF;
		} else if (compSwipe.equals(SWIPE_DOWN_SELECT.getSwipe())) {
			return SWIPE_DOWN_SELECT;
		}
		
		// return an empty swipe if we couldn't find a suitable one
		return SWIPE_NOOP;
	}
}