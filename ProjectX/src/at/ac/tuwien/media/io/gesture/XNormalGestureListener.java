package at.ac.tuwien.media.io.gesture;

import android.view.MotionEvent;
import at.ac.tuwien.media.MainActivity;
import at.ac.tuwien.media.util.Value;

public class XNormalGestureListener extends XGestureDetector {
	
	public XNormalGestureListener(final MainActivity parent, final int indexNo) {
		super(parent, indexNo);
		
		final int centerVertical = Value.DISPLAY_WIDTH / 2;
		leftEdge = centerVertical - (Value.THUMBNAIL_WIDTH / 2);
		rightEdge = centerVertical + (Value.THUMBNAIL_WIDTH / 2);
		
		// set thumbnail position
		thumbnailPosition = Value.EThumbnailPostion.MIDDLE;
	}

	@Override
	protected boolean isValid(final MotionEvent me) {
		return leftEdge <= me.getY() && me.getY() <= rightEdge &&
				bottomEdge <= me.getX() && me.getX() <= topEdge;
	}
}