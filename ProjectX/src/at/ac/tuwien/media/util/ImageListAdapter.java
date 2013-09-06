package at.ac.tuwien.media.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageListAdapter extends BaseAdapter {
    private final Context context;
    private final List<Bitmap> imageList;

    public ImageListAdapter(Context context) {
        this.context = context;
        imageList = new ArrayList<Bitmap>();
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ImageView iv;
        // check if view is converted
        // create a new view
        if (convertView == null) {
        	iv = new ImageView(context);
        	iv.setLayoutParams(new GridView.LayoutParams(Value.THUMBNAIL_WIDTH, Value.THUMBNAIL_WIDTH));
        	// roate image to fit screen rotation
			iv.setRotation(-90.0f);

        // use the old view
        } else {
        	iv = (ImageView) convertView;
        }

        // set the bitmap
        iv.setImageBitmap(imageList.get(position));
        
        return iv;
    }
    
    public List<Bitmap> getImageList() {
    	return imageList;
    }
}