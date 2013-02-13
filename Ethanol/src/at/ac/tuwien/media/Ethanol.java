package at.ac.tuwien.media;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import at.ac.tuwien.media.io.ImageIO;
import at.ac.tuwien.media.util.EImageSize;
import at.ac.tuwien.media.util.Values;

public class Ethanol extends Activity implements OnGesturePerformedListener {
	private final static String VIDEO_NAME = "images";
	
	private GestureLibrary mLibrary;

	private List<String> images;
	private int currentImageNo = -1;
	private ImageIO imageIO;
	
	private TextView topText;
	private TextView bottomText;
	private ImageView leftPic;
	private ImageView centerPic;
	private ImageView rightPic;   
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ethanol);

		// init gesture recognition
		initGestures();
        
        // add view items
        initViews();
      
		// load images
        try {
	        imageIO = new ImageIO();
        	images = imageIO.loadImages(VIDEO_NAME);
			
			nextImage();
        } catch (Exception ex) {
        	topText.setText(ex.getMessage());
        }
	}
	
	private void initGestures() {
		mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
		if (!mLibrary.load()) {
			finish();
		}

		GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
		gestures.addOnGesturePerformedListener(this);
	}
	
	private void initViews() {
		topText = (TextView) findViewById(R.id.text_top);
        bottomText = (TextView) findViewById(R.id.text_bottom);
        
    	leftPic = (ImageView) findViewById(R.id.leftPic);
    	centerPic = (ImageView) findViewById(R.id.centerPic);
    	rightPic = (ImageView) findViewById(R.id.rightPic);
	}
	
	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		ArrayList<Prediction> predictions = mLibrary.recognize(gesture);

		if (predictions.size() > 0 && predictions.get(0).score > 1.0) {
			String result = predictions.get(0).name;

			if (result.equalsIgnoreCase(Values.SWIPE_LEFT)) {
//				Toast.makeText(this, "left_swipe", Toast.LENGTH_SHORT).show();
				nextImage();
			} else if (result.equalsIgnoreCase(Values.SWIPE_RIGHT)) {
//				Toast.makeText(this, "right_swipe", Toast.LENGTH_SHORT).show();
				prevImage();
			}
//			else if (result.equalsIgnoreCase(Values.SWIPE_UP)) {
//				Toast.makeText(this, "up_swipe", Toast.LENGTH_SHORT).show();
//				nextImage(Values.FAST_SWIPE_INTERVAL);
//			} else if (result.equalsIgnoreCase(Values.SWIPE_DOWN)) {
//				Toast.makeText(this, "down_swipe", Toast.LENGTH_SHORT).show();
//				prevImage(Values.FAST_SWIPE_INTERVAL);
//			} else {
//				Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
//			}
		}
	}
	
	private void nextImage() {
		nextImage(1);
	}
	
	private void nextImage(int interval) {
		currentImageNo += interval;
		
		updateImage();
	}
	
	private void prevImage() {
		prevImage(1);
	}
	
	private void prevImage(int interval) {
		currentImageNo -= interval;
		
		updateImage();
	}
	
	private void updateImage() {
		Bitmap leftImage;
		Bitmap centerImage;
		Bitmap rightImage;
		
		// there is no image in the right side
		if (currentImageNo > (images.size() - 2)) {
			// set image number to the last available picture
			currentImageNo = (images.size() - 1);
					
			leftImage = imageIO.getImage(images.get(currentImageNo - 1), EImageSize.B);
			centerImage = imageIO.getImage(images.get(currentImageNo), EImageSize.A);
			rightImage = null;	
			
		// no image in the left side
		} else if (currentImageNo <= 0) {
			// set image number to the first available picture
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
		
		// show texts and images
		topText.setText((currentImageNo + 1) + " / " + images.size());
		bottomText.setText(images.get(currentImageNo));
		
		leftPic.setImageBitmap(leftImage);
		centerPic.setImageBitmap(centerImage);
		rightPic.setImageBitmap(rightImage);
	}
}