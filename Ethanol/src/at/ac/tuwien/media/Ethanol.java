package at.ac.tuwien.media;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.GestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import at.ac.tuwien.media.io.ImageIO;
import at.ac.tuwien.media.util.EImageSize;

public class Ethanol extends Activity {
	private final static String VIDEO_NAME = "images";

	private List<String> images;
	private int currentImageNo = -1;
	private ImageIO imageIO;
	
	private TextView topText;
	private TextView bottomText;
	private ImageView leftPic;
	private ImageView centerPic;
	private ImageView rightPic;

    private GestureDetector gestureDetector;
    private View.OnTouchListener gestureListener;
    
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ethanol);
				
		// Gesture detection
//        gestureDetector = new GestureDetector(new MyGestureDetector());
//        gestureListener = new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                return gestureDetector.onTouchEvent(event);
//            }
//        };
        
        // add view items
        initViews();
      
		// load images
        try {
	        imageIO = new ImageIO();
        	images = imageIO.loadImages(VIDEO_NAME);
			
			nextImage();
			nextImage();
        } catch (Exception ex) {
        	topText.setText(ex.getMessage());
        }
	}
	
	private void initViews() {
		
		topText = (TextView) findViewById(R.id.text_top);
        topText.setOnTouchListener(gestureListener);
        
        bottomText = (TextView) findViewById(R.id.text_bottom);
        bottomText.setOnTouchListener(gestureListener);
        
    	leftPic = (ImageView) findViewById(R.id.leftPic);
    	centerPic = (ImageView) findViewById(R.id.centerPic);
    	rightPic = (ImageView) findViewById(R.id.rightPic);
	}
	
//	
//	class MyGestureDetector extends SimpleOnGestureListener {
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            try {
//                if (Math.abs(e1.getY() - e2.getY()) > Values.SWIPE_MAX_OFF_PATH)
//                    return false;
//                // right to left swipe
//                if(e1.getX() - e2.getX() > Values.SWIPE_MIN_DISTANCE && Math.abs(velocityX) > Values.SWIPE_THRESHOLD_VELOCITY) {
//                	nextImage();
//                	
//                    Toast.makeText(Ethanol.this, "Left Swipe", Toast.LENGTH_SHORT).show();
//                }  else if (e2.getX() - e1.getX() > Values.SWIPE_MIN_DISTANCE && Math.abs(velocityX) > Values.SWIPE_THRESHOLD_VELOCITY) {
//                	prevImage();
//                	
//                    Toast.makeText(Ethanol.this, "Right Swipe", Toast.LENGTH_SHORT).show();
//                }
//            } catch (Exception e) {
//                // nothing
//            }
//            return false;
//        }
//
//    }
	
	
	private void nextImage() {
		if (currentImageNo < (images.size() - 1)) {
			currentImageNo ++;
		}
		updateImage();
	}
	
	private void prevImage() {
		if (currentImageNo > 0) {
			currentImageNo --;
		}
		updateImage();
	}
	
	private void updateImage() {
		Bitmap leftImage;
		Bitmap centerImage;
		Bitmap rightImage;
		
		// there is no image in the right side
		if (currentImageNo > (images.size() - 2)) {
			leftImage = imageIO.getImage(images.get(currentImageNo - 1), EImageSize.B);
			centerImage = imageIO.getImage(images.get(currentImageNo), EImageSize.A);
			rightImage = null;	
			
		// no image in the left side
		} else if (currentImageNo <= 0) {
			currentImageNo = 0;
			
			leftImage = null;
			centerImage = imageIO.getImage(images.get(currentImageNo), EImageSize.A);
			rightImage = imageIO.getImage(images.get(currentImageNo + 1), EImageSize.B);
			
		// all images can be displayed
		} else {
			leftImage = imageIO.getImage(images.get(currentImageNo - 1), EImageSize.B);
			centerImage = imageIO.getImage(images.get(currentImageNo), EImageSize.A);
			rightImage = imageIO.getImage(images.get(currentImageNo + 1), EImageSize.B);
		}
		
		// show text and images
		topText.setText((currentImageNo + 1) + " / " + images.size());
		bottomText.setText(images.get(currentImageNo));
		leftPic.setImageBitmap(leftImage);
		centerPic.setImageBitmap(centerImage);
		rightPic.setImageBitmap(rightImage);
	}
}