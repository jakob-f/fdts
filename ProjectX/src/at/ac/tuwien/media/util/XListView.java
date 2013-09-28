package at.ac.tuwien.media.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class XListView extends ListView {

	public XListView(Context context) {
		super(context);
	}

	public XListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public XListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
//	@Override
//	public boolean onTouchEvent(MotionEvent me) {
//	//	return super.onTouchEvent(me);
//		
//		return dispatchTouchEvent(me);
//	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
//	    switch (event.getAction())
//	    {
//	        case MotionEvent.ACTION_DOWN:
//	        {
//	            //Record the location of the ACTION_DOWN
//	            //Either in a GestureDetector or in a variable
//	            break;
//	        }
//	        case MotionEvent.ACTION_MOVE:
//	        {
//	            //Potentially start consuming events here as you may
//	            //have moved to far for a click or scroll
//	            //Also scroll the screen as necessary
//	            break;
//	        }
//	        case MotionEvent.ACTION_UP:
//	        {
//	            //Consume if necessary and perform the fling / swipe action
//	            //if it has been determined to be a fling / swipe
//	            break;
//	        }
//	    }
	    return super.dispatchTouchEvent(event);
	}
}
