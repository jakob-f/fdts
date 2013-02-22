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
	ROW_FULL_BOTTOM (new Rectangle(new Point(Values.VERTICAL_LEFT, Values.HORIZONTAL_SECOND_FRAME_EDGE), new Point(Values.VERTICAL_RIGHT, Values.HORIZONTAL_BOTTOM))),
	ROW_10_PERCENT (new Rectangle(new Point(Values.VERTICAL_LEFT, Values.HORIZONTAL_TOP), new Point(Values.VERTICAL_10_PERCENT, Values.HORIZONTAL_BOTTOM))),
	ROW_20_PERCENT (new Rectangle(new Point(Values.VERTICAL_10_PERCENT, Values.HORIZONTAL_TOP), new Point(Values.VERTICAL_20_PERCENT, Values.HORIZONTAL_BOTTOM))),
	ROW_30_PERCENT (new Rectangle(new Point(Values.VERTICAL_20_PERCENT, Values.HORIZONTAL_TOP), new Point(Values.VERTICAL_30_PERCENT, Values.HORIZONTAL_BOTTOM))),
	ROW_40_PERCENT (new Rectangle(new Point(Values.VERTICAL_30_PERCENT, Values.HORIZONTAL_TOP), new Point(Values.VERTICAL_40_PERCENT, Values.HORIZONTAL_BOTTOM))),
	ROW_50_PERCENT (new Rectangle(new Point(Values.VERTICAL_40_PERCENT, Values.HORIZONTAL_TOP), new Point(Values.VERTICAL_50_PERCENT, Values.HORIZONTAL_BOTTOM))),
	ROW_60_PERCENT (new Rectangle(new Point(Values.VERTICAL_50_PERCENT, Values.HORIZONTAL_TOP), new Point(Values.VERTICAL_60_PERCENT, Values.HORIZONTAL_BOTTOM))),
	ROW_70_PERCENT (new Rectangle(new Point(Values.VERTICAL_60_PERCENT, Values.HORIZONTAL_TOP), new Point(Values.VERTICAL_70_PERCENT, Values.HORIZONTAL_BOTTOM))),
	ROW_80_PERCENT (new Rectangle(new Point(Values.VERTICAL_70_PERCENT, Values.HORIZONTAL_TOP), new Point(Values.VERTICAL_80_PERCENT, Values.HORIZONTAL_BOTTOM))),
	ROW_90_PERCENT (new Rectangle(new Point(Values.VERTICAL_80_PERCENT, Values.HORIZONTAL_TOP), new Point(Values.VERTICAL_90_PERCENT, Values.HORIZONTAL_BOTTOM))),
	ROW_100_PERCENT (new Rectangle(new Point(Values.VERTICAL_90_PERCENT, Values.HORIZONTAL_TOP), new Point(Values.VERTICAL_RIGHT, Values.HORIZONTAL_BOTTOM)));	
	
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
	
	public static ERectangleType getRectangleInRowPercentegeFromPoint (Point p) {
		// return the row percentage rectangle in which the point lies
		if (ROW_10_PERCENT.getRectangle().isPointInRectangle(p)) {
			return ROW_10_PERCENT;
		} else if (ROW_20_PERCENT.getRectangle().isPointInRectangle(p)) {
			return ROW_20_PERCENT;
		} else if (ROW_30_PERCENT.getRectangle().isPointInRectangle(p)) {
			return ROW_30_PERCENT;
		} else if (ROW_40_PERCENT.getRectangle().isPointInRectangle(p)) {
			return ROW_40_PERCENT;
		} else if (ROW_50_PERCENT.getRectangle().isPointInRectangle(p)) {
			return ROW_50_PERCENT;
		} else if (ROW_60_PERCENT.getRectangle().isPointInRectangle(p)) {
			return ROW_60_PERCENT;
		} else if (ROW_70_PERCENT.getRectangle().isPointInRectangle(p)) {
			return ROW_70_PERCENT;
		} else if (ROW_80_PERCENT.getRectangle().isPointInRectangle(p)) {
			return ROW_80_PERCENT;
		} else if (ROW_90_PERCENT.getRectangle().isPointInRectangle(p)) {
			return ROW_90_PERCENT;
		} else if (ROW_100_PERCENT.getRectangle().isPointInRectangle(p)) {
			return ROW_100_PERCENT;
		}
		
		// return null if the point was not found
		// this should not happen here since we cover the whole screen
		return null;
	}
}
	
