package at.ac.tuwien.media.util;

import android.graphics.Point;

public enum ERectangles {
	THUMBNAIL_ONE (new Rectangle(new Point(Values.PICTURE_START_BORDER, Values.FRAME_ONE_BORDER), new Point(Values.PICTURE_ONE_BORDER, Values.FRAME_TWO_BORDER))),
	THUMBNAIL_TWO (new Rectangle(new Point(Values.PICTURE_ONE_BORDER, Values.FRAME_ONE_BORDER), new Point(Values.PICTURE_TWO_BORDER, Values.FRAME_TWO_BORDER))),
	THUMBNAIL_THREE (new Rectangle(new Point(Values.PICTURE_TWO_BORDER, Values.FRAME_ONE_BORDER), new Point(Values.PICTURE_THREE_BORDER, Values.FRAME_TWO_BORDER))),
	FRAME_TOP (new Rectangle(new Point(Values.PICTURE_START_BORDER, Values.FRAME_START_BORDER), new Point(Values.PICTURE_THREE_BORDER, Values.FRAME_ONE_BORDER))),
	FRAME_BOTTOM (new Rectangle(new Point(Values.PICTURE_START_BORDER, Values.FRAME_TWO_BORDER), new Point(Values.PICTURE_THREE_BORDER, Values.FRAME_THREE_BORDER)));
	
	private Rectangle rectangle;

	private ERectangles(Rectangle rectangle) {
		this.rectangle = rectangle;
	}

	public Rectangle getRectangle() {
		return rectangle;
	}
	
	public static ERectangles getRectangleFromPoint (Point p) {
		if (THUMBNAIL_ONE.getRectangle().isPointInRectangle(p)) {
			return THUMBNAIL_ONE;
		} else if (THUMBNAIL_TWO.getRectangle().isPointInRectangle(p)) {
			return THUMBNAIL_TWO;
		} else if (THUMBNAIL_THREE.getRectangle().isPointInRectangle(p)) {
			return THUMBNAIL_THREE;
		} else if (FRAME_TOP.getRectangle().isPointInRectangle(p)) {
			return FRAME_TOP;
		} else if (FRAME_BOTTOM.getRectangle().isPointInRectangle(p)) {
			return FRAME_BOTTOM;
		}
		
		return null;
	}
}
	
