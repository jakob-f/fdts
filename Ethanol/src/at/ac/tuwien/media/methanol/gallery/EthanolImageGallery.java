package at.ac.tuwien.media.methanol.gallery;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import at.ac.tuwien.media.methanol.IEthanol;
import at.ac.tuwien.media.methanol.R;
import at.ac.tuwien.media.methanol.io.file.bitmap.BitmapIO;
import at.ac.tuwien.media.methanol.io.file.model.Dimension;
import at.ac.tuwien.media.methanol.util.Configuration;
import at.ac.tuwien.media.methanol.util.Value;

/**
 * The {@link EthanolImageGallery} is a simple image gallery which shows images in full size.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class EthanolImageGallery extends Activity implements OnClickListener {
	private static IEthanol ethanol;
	private static List<File> imageFiles;
	private static Dimension displayDimension;
	
	private GestureDetector gestureDetector;
    private View.OnTouchListener gestureListener;
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set layout
		setContentView(R.layout.ethanol_image_gallery);

		// add the image gallery
		final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
		viewPager.setAdapter(new ImageAdapter(this));
		// set the position to start with
		viewPager.setCurrentItem(getIntent().getExtras().getInt("position"));
		
		// add the gesture detector
        gestureDetector = new GestureDetector(this, new ImageGalleryGestureDetector());
        gestureListener = new View.OnTouchListener() {
        	@Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
	}		
	
	public static void setParent(final IEthanol parent) {
		EthanolImageGallery.ethanol = parent;
	}
	
	public static void setImageList(final List<File> imageList) {
		EthanolImageGallery.imageFiles = imageList;
	}
	
	public static void setDisplayDimension(final Dimension displayDimension) {
		EthanolImageGallery.displayDimension = displayDimension;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		if (Configuration.getAsBoolean(Value.CONFIG_PREVIEW_BACK)) {
			jumpToCurrentImage();
		}
	}
	
	@Override
	public void onClick(View arg0) {
		// this method intentionally left blank
	}
	
	private void jumpToCurrentImage() {
		// skip to the last picture shown
		final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
		ethanol.skipToThumbnail(viewPager.getCurrentItem());	
	}
	
	// Image Adapter Class with image gallery
	private class ImageAdapter extends PagerAdapter {
		private final Context parent;

		ImageAdapter(final Context parent) {
			this.parent = parent;
		}

		@Override
		public int getCount() {
			return imageFiles.size();
		}
	
		@Override
		public boolean isViewFromObject(final View view, final Object object) {
			return view == ((ImageView) object);
		}
		
		@Override
		public Object instantiateItem(final ViewGroup container, final int position) {
			// create a new Image View
			final ImageView iv = new ImageView(parent);
			// set values
			iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			// set drawable image
			final Bitmap bm = BitmapIO.read(imageFiles.get(position), displayDimension);
			iv.setImageBitmap(bm);
			// add the view to the layout
			((ViewPager) container).addView(iv);
			
			// add the gesture detector to the image view
			iv.setOnClickListener(EthanolImageGallery.this);
			iv.setOnTouchListener(gestureListener);
			
			return iv;
		}
	
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((ImageView) object);
		}
	}

	// image gallery gesture detector
	private class ImageGalleryGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			// skip to the last picture shown
			jumpToCurrentImage();
			
			// finish the activity
			finish();
			
			// the event is consumed
			return true;
		}
	}
}