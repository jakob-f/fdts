package at.ac.tuwien.media.io.gesture;

import android.graphics.Point;
import at.ac.tuwien.media.io.gesture.model.Rectangle;
import at.ac.tuwien.media.util.Value;

/**
 * The enum {@link ERectangleType} defines fixed values for rectangles in the screen for swipe detection.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public enum ERectangleType {
	THUMBNAIL_ONE (new Rectangle(new Point(Value.VERTICAL_LEFT, Value.HORIZONTAL_TOP_ROW_EDGE), new Point(Value.VERTICAL_FIRST_PICTURE_EDGE, Value.HORIZONTAL_MAIN_SECTION_EDGE))),
	THUMBNAIL_TWO (new Rectangle(new Point(Value.VERTICAL_FIRST_PICTURE_EDGE, Value.HORIZONTAL_TOP_ROW_EDGE), new Point(Value.VERTICAL_SECOND_PICTURE_EDGE, Value.HORIZONTAL_MAIN_SECTION_EDGE))),
	THUMBNAIL_THREE (new Rectangle(new Point(Value.VERTICAL_SECOND_PICTURE_EDGE, Value.HORIZONTAL_TOP_ROW_EDGE), new Point(Value.VERTICAL_RIGHT, Value.HORIZONTAL_MAIN_SECTION_EDGE))),
	ROW_TOP (new Rectangle(new Point(Value.VERTICAL_LEFT, Value.HORIZONTAL_TOP), new Point(Value.VERTICAL_RIGHT, Value.HORIZONTAL_TOP_ROW_EDGE))),
	ROW_BOTTOM (new Rectangle(new Point(Value.VERTICAL_LEFT, Value.HORIZONTAL_MAIN_SECTION_EDGE), new Point(Value.VERTICAL_RIGHT, Value.HORIZONTAL_BOTTOM)));
	
	private Rectangle rectangle;

	private ERectangleType(Rectangle rectangle) {
		this.rectangle = rectangle;
	}

	public Rectangle getRectangle() {
		return rectangle;
	}
	
	public static ERectangleType getRectangleFromPoint (Point p) {
		// return the rectangle in which the point lies
		if (THUMBNAIL_ONE.getRectangle().isPointInRectangle(p)) {
			return THUMBNAIL_ONE;
		} else if (THUMBNAIL_TWO.getRectangle().isPointInRectangle(p)) {
			return THUMBNAIL_TWO;
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