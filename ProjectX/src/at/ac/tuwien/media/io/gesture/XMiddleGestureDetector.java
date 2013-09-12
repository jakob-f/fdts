package at.ac.tuwien.media.io.gesture;

import android.view.MotionEvent;
import at.ac.tuwien.media.MainActivity;
import at.ac.tuwien.media.util.Value;

public class XMiddleGestureDetector extends XGestureDetector {
	private int centerVertical;
	
	public XMiddleGestureDetector(final MainActivity parent) {
		super(parent, null);
		
		centerVertical = 1438 / 2; // TODO calculate this programatically with the screen size
		leftEdge = centerVertical - Value.THUMBNAIL_WIDTH;
		rightEdge = centerVertical + Value.THUMBNAIL_WIDTH;
	}

	@Override
	protected boolean isValid(final MotionEvent me) {
		if(leftEdge <= me.getY() && me.getY() <= rightEdge &&
				bottomEdge <= me.getX() && me.getX() <= topEdge) {
			
			// do we have a left or right image
			if (me.getY() <= centerVertical) {
				thumbnailPostion = Value.EThumbnailPostion.MIDDLE_RIGHT;
			} else {
				thumbnailPostion = Value.EThumbnailPostion.MIDDLE_LEFT;
			}
			
			return true;
		}
		
		return false;
	}

}
