package at.ac.tuwien.media;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class EthanolImageGallery extends Activity {
	private static List<File> imageFiles;
	
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
	}
	
	public static void setImageList(final List<File> imageList) {
		EthanolImageGallery.imageFiles = imageList;
	}

	// Image Adapter Class with image gallery
	private class ImageAdapter extends PagerAdapter {
		private Context parent;

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
		public Object instantiateItem(final ViewGroup container, int position) {	
			// create a new Image View
			final ImageView iv = new ImageView(parent);
			// set values
			iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			// set drawable image
			final Bitmap bm = BitmapFactory.decodeFile(imageFiles.get(position).getAbsolutePath());
			iv.setImageBitmap(bm);
			// add the view to the layout
			((ViewPager) container).addView(iv);
			
			return iv;
		}
	
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((ImageView) object);
		}
	}
}