package at.ac.tuwien.media;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import at.ac.tuwien.media.io.impl.ImageIOImpl;
import at.ac.tuwien.media.util.EImageSize;
import at.ac.tuwien.media.util.Values;

public class Ethanol extends Activity {
	private List<Bitmap> images;
	private ImageView jpgView;
	
	private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    private View.OnTouchListener gestureListener;
    private int currentImage = -1;
    private TextView displaytext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ethanol);
				
		// Gesture detection
        gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
		
        try {
        	jpgView = (ImageView) findViewById(R.id.imageView);
	        displaytext = (TextView) findViewById(R.id.text_content);
			//display images
			//-----
			ImageIOImpl imageIO = new ImageIOImpl();
//			imageIO.readAndResizeImages("images");
			
//			images = new ArrayList<Bitmap>();
//			images.add(imageIO.getImage("0002.jpg", EImageSize.A));
			
			
			jpgView.setImageBitmap(imageIO.getImageFromDirectory(new File("/sdcard/images/previewA/"), "0060.jpg"));
			
			
			displaytext.setText(imageIO.getImageFromDirectory(new File("/sdcard/images/previewA/"), "0060.jpg").getWidth() + " " + imageIO.getImageFromDirectory(new File("/sdcard/images/previewA/"), "0001.jpg").getHeight());
			
			
			jpgView.setOnTouchListener(gestureListener);
			
//			nextImage();
			//-----
        } catch (Exception ex) {
        	displaytext.setText(ex.getMessage());
        }
	}
	
	
	class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	nextImage();
                	
                    Toast.makeText(Ethanol.this, "Left Swipe", Toast.LENGTH_SHORT).show();
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	prevImage();
                	
                    Toast.makeText(Ethanol.this, "Right Swipe", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

    }
	
	
	
	
	private void nextImage() {
		if (currentImage < (images.size() - 1)) {
			currentImage ++;
		}
		
		updateImage(images.get(currentImage));
	}
	
	private void prevImage() {
		if (currentImage > 0) {
			currentImage --;
		}
		
		updateImage(images.get(currentImage));
	}
	
	private void updateImage(Bitmap image) {
		displaytext.setText((currentImage + 1) + " / " + images.size());
		
		jpgView.setImageBitmap(image);
	}
}