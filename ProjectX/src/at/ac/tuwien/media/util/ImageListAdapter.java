package at.ac.tuwien.media.util;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * The {@link ImageListAdapter} class handles {@link ImageView} inside a list view.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class ImageListAdapter extends BaseAdapter {
    private Context context;
    private List<Bitmap> imageList;

    public ImageListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return -1l;
    }

    @SuppressLint("NewApi")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	// try to use the old view
        ImageView iv = (ImageView) convertView;
        
        // check if view is converted
        // else create a new view
        if (iv == null) {
        	iv = new ImageView(context);
        	iv.setLayoutParams(new GridView.LayoutParams(Value.THUMBNAIL_WIDTH, Value.THUMBNAIL_WIDTH));
        	// roate image to fit screen rotation
			iv.setRotation(-90.0f);
        }

        // set the bitmap
        iv.setImageBitmap(imageList.get(position));
        
        return iv;
    }
    
    public List<Bitmap> getImageList() {
    	if (imageList == null) {
    		imageList = new ArrayList<Bitmap>();
    	}
    	
    	return imageList;
    }
}