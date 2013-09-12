package at.ac.tuwien.media.io.gesture;

import android.view.MotionEvent;
import at.ac.tuwien.media.MainActivity;
import at.ac.tuwien.media.util.Value;

public class XBottomTopGestureListener extends XGestureDetector {
	
	public XBottomTopGestureListener(final MainActivity parent, final Value.EThumbnailPostion thumbnailPostion) {
		super(parent, thumbnailPostion);
		
		final int centerVertical = 1188 / 2; // TODO calculate this programatically with the screen size
		leftEdge = centerVertical - (Value.THUMBNAIL_WIDTH / 2);
		rightEdge = centerVertical + (Value.THUMBNAIL_WIDTH / 2);
	}

	@Override
	protected boolean isValid(final MotionEvent me) {
		return leftEdge <= me.getY() && me.getY() <= rightEdge &&
				bottomEdge <= me.getX() && me.getX() <= topEdge;
	}
}