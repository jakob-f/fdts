package at.ac.tuwien.media.io.gesture;

import android.graphics.Point;
import at.ac.tuwien.media.io.gesture.model.Rectangle;
import at.ac.tuwien.media.util.Values;

// class with fixed values for rectangles
public enum ERectangleType {
	THUMBNAIL_ONE (new Rectangle(new Point(Values.VERTICAL_LEFT, Values.HORIZONTAL_FIRST_FRAME_EDGE), new Point(Values.VERTICAL_FIRST_PICTURE_EDGE, Values.HORIZONTAL_SECOND_FRAME_EDGE))),
	THUMBNAIL_TWO (new Rectangle(new Point(Values.VERTICAL_FIRST_PICTURE_EDGE, Values.HORIZONTAL_FIRST_FRAME_EDGE), new Point(Values.VERTICAL_SECOND_PICTURE_EDGE, Values.HORIZONTAL_SECOND_FRAME_EDGE))),
	THUMBNAIL_THREE (new Rectangle(new Point(Values.VERTICAL_SECOND_PICTURE_EDGE, Values.HORIZONTAL_FIRST_FRAME_EDGE), new Point(Values.VERTICAL_RIGHT, Values.HORIZONTAL_SECOND_FRAME_EDGE))),
	ROW_FULL_TOP (new Rectangle(new Point(Values.VERTICAL_LEFT, Values.HORIZONTAL_TOP), new Point(Values.VERTICAL_RIGHT, Values.HORIZONTAL_FIRST_FRAME_EDGE))),
	ROW_FULL_BOTTOM (new Rectangle(new Point(Values.VERTICAL_LEFT, Values.HORIZONTAL_SECOND_FRAME_EDGE), new Point(Values.VERTICAL_RIGHT, Values.HORIZONTAL_BOTTOM)));	
	
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
		} else if (ROW_FULL_TOP.getRectangle().isPointInRectangle(p)) {
			return ROW_FULL_TOP;
		} else if (ROW_FULL_BOTTOM.getRectangle().isPointInRectangle(p)) {
			return ROW_FULL_BOTTOM;
		}
		
		// return null if the point was not found
		return null;
	}
}