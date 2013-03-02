package at.ac.tuwien.media.io.gesture;

import android.graphics.Point;
import at.ac.tuwien.media.io.gesture.model.Rectangle;
import at.ac.tuwien.media.util.Values;

/**
 * The enum {@link ERectangleType} defines fixed values for rectangles in the screen for swipe detection.
 * 
 * @author Jakob Frohnwieser (jakob.frohnwieser@gmx.at)
 */
public enum ERectangleType {
	THUMBNAIL_ONE (new Rectangle(new Point(Values.VERTICAL_LEFT, Values.HORIZONTAL_TOP_ROW_EDGE), new Point(Values.VERTICAL_FIRST_PICTURE_EDGE, Values.HORIZONTAL_MAIN_SECTION_EDGE))),
	THUMBNAIL_TWO (new Rectangle(new Point(Values.VERTICAL_FIRST_PICTURE_EDGE, Values.HORIZONTAL_TOP_ROW_EDGE), new Point(Values.VERTICAL_SECOND_PICTURE_EDGE, Values.HORIZONTAL_MAIN_SECTION_EDGE))),
	THUMBNAIL_THREE (new Rectangle(new Point(Values.VERTICAL_SECOND_PICTURE_EDGE, Values.HORIZONTAL_TOP_ROW_EDGE), new Point(Values.VERTICAL_RIGHT, Values.HORIZONTAL_MAIN_SECTION_EDGE))),
	VERTICAL_10_PERCENT (new Rectangle(new Point(Values.VERTICAL_LEFT, Values.HORIZONTAL_TOP), new Point(Values.VERTICAL_LEFT_10_PERCENT, Values.HORIZONTAL_BOTTOM))),
	VERTICAL_90_PERCENT (new Rectangle(new Point(Values.VERTICAL_LEFT_90_PERCENT, Values.HORIZONTAL_TOP), new Point(Values.VERTICAL_RIGHT, Values.HORIZONTAL_BOTTOM))),
	ROW_TOP (new Rectangle(new Point(Values.VERTICAL_LEFT, Values.HORIZONTAL_TOP), new Point(Values.VERTICAL_RIGHT, Values.HORIZONTAL_TOP_ROW_EDGE))),
	ROW_BOTTOM (new Rectangle(new Point(Values.VERTICAL_LEFT, Values.HORIZONTAL_MAIN_SECTION_EDGE), new Point(Values.VERTICAL_RIGHT, Values.HORIZONTAL_BOTTOM)));
	
	private Rectangle rectangle;

	private ERectangleType(Rectangle rectangle) {
		this.rectangle = rectangle;
	}

	public Rectangle getRectangle() {
		return rectangle;
	}
	
	public static ERectangleType getRectangleFromPoint (Point p) {
		// return the rectangle in which the point lies
		if (VERTICAL_10_PERCENT.getRectangle().isPointInRectangle(p)) {
			return VERTICAL_10_PERCENT;
		} else 
			if (THUMBNAIL_ONE.getRectangle().isPointInRectangle(p)) {
			return THUMBNAIL_ONE;
		} else if (THUMBNAIL_TWO.getRectangle().isPointInRectangle(p)) {
			return THUMBNAIL_TWO;
		} else if (VERTICAL_90_PERCENT.getRectangle().isPointInRectangle(p)) {
			return VERTICAL_90_PERCENT;
		} else if (THUMBNAIL_THREE.getRectangle().isPointInRectangle(p)) {
			return THUMBNAIL_THREE;
		} else if (ROW_TOP.getRectangle().isPointInRectangle(p)) {
			return ROW_TOP;
		} else if (ROW_BOTTOM.getRectangle().isPointInRectangle(p)) {
			return ROW_BOTTOM;
		}
		
		// return null if the point was not found
		return null;
	}
}