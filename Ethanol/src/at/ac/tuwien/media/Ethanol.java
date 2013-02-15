package at.ac.tuwien.media;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import at.ac.tuwien.media.io.EthanolGestureDetector;
import at.ac.tuwien.media.io.ImageIO;
import at.ac.tuwien.media.util.EImageSize;

public class Ethanol extends Activity implements IImageSwipe {
	private final static String VIDEO_NAME = "images";
    private GestureDetector gestureDetector;
    
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
		
		// Gesture detection
		initGestureDetection();
        
        // add view items
        initViews();
      
		// load images
        try {
	        imageIO = new ImageIO();
        	images = imageIO.loadImages(VIDEO_NAME);
			
			nextImage(1);
        } catch (Exception ex) {
        	topText.setText(ex.getMessage());
        }
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}
	
	private void initGestureDetection() {
		// get the display size
		Display display = getWindowManager().getDefaultDisplay();
		Point displaySize = new Point();
		display.getSize(displaySize);
		
		// init new gesture detector
		gestureDetector = new GestureDetector(this, new EthanolGestureDetector(this, displaySize));
	}
	
	private void initViews() {
		topText = (TextView) findViewById(R.id.text_top);
        bottomText = (TextView) findViewById(R.id.text_bottom);
        
    	leftPic = (ImageView) findViewById(R.id.leftPic);
    	centerPic = (ImageView) findViewById(R.id.centerPic);
    	rightPic = (ImageView) findViewById(R.id.rightPic);
	}
	
	@Override
	public void nextImage(int interval) {
		currentImageNo += interval;
		
		updateImage();
	}
	
	@Override
	public void prevImage(int interval) {
		currentImageNo -= interval;
		
		updateImage();
	}
	
	public void updateImage() {
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
	
	private void setImageIfPossible() {
		//TODO implement me!
	}
}