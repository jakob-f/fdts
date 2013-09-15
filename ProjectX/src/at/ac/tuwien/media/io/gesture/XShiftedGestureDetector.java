package at.ac.tuwien.media.io.gesture;

import android.view.MotionEvent;
import at.ac.tuwien.media.MainActivity;
import at.ac.tuwien.media.util.Value;

public class XShiftedGestureDetector extends XGestureDetector {
	private int centerVertical;
	
	public XShiftedGestureDetector(final MainActivity parent, final int indexNo) {
		super(parent, indexNo);
		
		centerVertical = (Value.DISPLAY_WIDTH + 242) / 2;
		leftEdge = centerVertical - Value.THUMBNAIL_WIDTH;
		rightEdge = centerVertical + Value.THUMBNAIL_WIDTH;
	}

	@Override
	protected boolean isValid(final MotionEvent me) {
		if(leftEdge <= me.getY() && me.getY() <= rightEdge &&
				bottomEdge <= me.getX() && me.getX() <= topEdge) {
			
			// do we have a left or right image position?
			if (me.getY() >= centerVertical) {
				thumbnailPosition = Value.EThumbnailPostion.LEFT;
			} else {
				thumbnailPosition = Value.EThumbnailPostion.RIGHT;
			}
			
			return true;
		}

		return false;
	}
}