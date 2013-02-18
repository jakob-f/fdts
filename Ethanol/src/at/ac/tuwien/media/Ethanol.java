package at.ac.tuwien.media;

import java.io.File;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import at.ac.tuwien.media.io.file.EImageSize;
import at.ac.tuwien.media.io.file.ImageIO;
import at.ac.tuwien.media.io.gesture.EthanolGestureDetector;

public class Ethanol extends Activity implements IImageSwipe {
	private final static String VIDEO_NAME = "images";
	private Point displaySize;
	private GestureDetector gestureDetector;

	private ImageIO imageIO;
	private ImageView[] imageViews;
	private int imageRowPadding = 3;
	
	private int currentThumbnailNo = -1;
	private List<File> thumbnailFiles;
	private List<Bitmap> thumbnailsD;
	private List<Bitmap> thumbnailsE;
	private List<Bitmap> thumbnailsF;
	private List<Bitmap> thumbnailsG;
	private List<Bitmap> thumbnailsH;
	private List<Bitmap> thumbnailsI;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ethanol);
		
		// load thumbnails
        loadThumbnails();
        
        // Gesture detection
        initGestureDetection();
        
        // add view items
        initViews();
        
        // load first image
        nextImage(1);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}
	
	private void loadThumbnails() {
		try {
			// load images from sdCard
			// create thumbnails if needed
        	imageIO = new ImageIO();
        	thumbnailFiles = imageIO.loadThumbnails(VIDEO_NAME);

        	// since they are the biggest files, thumbnai sizes A - C
        	// are loaded directly
        	// for performance issues thumbnail sizes D - I are cached
        	thumbnailsD = imageIO.getThumbnailList(EImageSize.D);
        	thumbnailsE = imageIO.getThumbnailList(EImageSize.E);
        	thumbnailsF = imageIO.getThumbnailList(EImageSize.F);
        	thumbnailsG = imageIO.getThumbnailList(EImageSize.G);
        	thumbnailsH = imageIO.getThumbnailList(EImageSize.H);
        	thumbnailsI = imageIO.getThumbnailList(EImageSize.I);
        } catch (Exception ex) {
        }
	}
	
	private void initGestureDetection() {
		// get the display size
		Display display = getWindowManager().getDefaultDisplay();
		displaySize = new Point();
		display.getSize(displaySize);
		
		// init new gesture detector
		gestureDetector = new GestureDetector(this, new EthanolGestureDetector(this, displaySize));
	}
	
	private void initViews() {
		// create an imageView for every thumbnail:
    	imageViews = new ImageView[thumbnailFiles.size()];
    	
    	for (int i = 0; i < imageViews.length; i++) {
    		imageViews[i] = addViews(i);
    	}
	}
	
	private ImageView addViews(int viewId) {
		// create a new image view
		ImageView iv = new ImageView(this);
		iv.setId(viewId);
		iv.setPadding(imageRowPadding, 0, imageRowPadding, 0);
		
		return iv;
	}
	
	private void addImageViewToLayout(int layoutId, ImageView iv, Bitmap bm) {
		// add the thumbnail to the image view
		iv.setImageBitmap(bm);
		// add the image view to the layout
		LinearLayout ll = (LinearLayout) findViewById(layoutId);
		ll.addView(iv);
	}
	
	@Override
	public void nextImage(int interval) {
		currentThumbnailNo += interval;
		
		updateImageView();
	}
	
	@Override
	public void prevImage(int interval) {
		currentThumbnailNo -= interval;
		
		updateImageView();
	}
		
	public void updateImageView() {
		// check image boundaries
		if (currentThumbnailNo < 0) {
			currentThumbnailNo = 0;
		} else if (currentThumbnailNo > (thumbnailFiles.size() - 1)) {
			currentThumbnailNo = (thumbnailFiles.size() - 1);
		}
		
		// first remove all image views
		((LinearLayout) findViewById(R.id.top_row)).removeAllViews();
		((LinearLayout) findViewById(R.id.main_section)).removeAllViews();
		((LinearLayout) findViewById(R.id.bottom_row)).removeAllViews();
		
		// now reset the image views with the right thumbnails
		int layoutId;
		EImageSize thumbnailSize;
		
		// get the right parameters
		for (int i = 0; i < thumbnailFiles.size(); i++) {
			// 1) already view thumbnails on the left side (top row)
			if (i < (currentThumbnailNo - 1)) {
				layoutId = R.id.top_row;
		
				thumbnailSize = EImageSize.D;
				
			// 2) thumbnails in the main section
			} else if (i < (currentThumbnailNo + 2)) {
				layoutId = R.id.main_section;
				
				if (i == currentThumbnailNo) {
					thumbnailSize = EImageSize.A;
				} else {
					thumbnailSize = EImageSize.B;
				}
				
			// 3) upcoming thumbnails on the right side (bottom row)
			} else {
				layoutId = R.id.bottom_row;
				
				thumbnailSize = getSizeForThumbnail((thumbnailFiles.size() - (currentThumbnailNo + 2)), i);
			}
			
			// finally add the image to the view
			addImageViewToLayout(layoutId, imageViews[i], getBitmapWithSize(i, thumbnailSize));
		}
	}
	
	private EImageSize getSizeForThumbnail(int thumbnailsToDisplayLeft, int thumbnail) {
		int startThumbnail = currentThumbnailNo + 2;
		
		// max display images
		if (thumbnailsToDisplayLeft <= 5) {
			return EImageSize.C;
			
		} else if (thumbnailsToDisplayLeft == 6) {
			if (thumbnail < (startThumbnail + 3)) {
				return EImageSize.C;
			} else {
				return EImageSize.D;
			}
		} else if (thumbnailsToDisplayLeft == 7) {
			if (thumbnail < (startThumbnail + 2)) {
				return EImageSize.C;
			} else if (thumbnail < (startThumbnail + 4)) {
				return EImageSize.D;
			} else {
				return EImageSize.E;
			}
			
		} else if (thumbnailsToDisplayLeft == 8) {
			if (thumbnail < (startThumbnail + 1)) {
				return EImageSize.C;
			} else if (thumbnail < (startThumbnail + 3)) {
				return EImageSize.D;
			} else if (thumbnail < (startThumbnail + 6)) {
				return EImageSize.E;
			} else {
				return EImageSize.F;
			}
			
		} else if (thumbnailsToDisplayLeft == 9) {
			if (thumbnail < (startThumbnail + 1)) {
				return EImageSize.C;
			} else if (thumbnail < (startThumbnail + 3)) {
				return EImageSize.D;
			} else if (thumbnail < (startThumbnail + 6)) {
				return EImageSize.E;
			} else if (thumbnail < (startThumbnail + 7)) {
				return EImageSize.F;
			} else {
				return EImageSize.G;
			}
			
		} else if (thumbnailsToDisplayLeft == 10) {
			if (thumbnail < (startThumbnail + 1)) {
				return EImageSize.C;
			} else if (thumbnail < (startThumbnail + 3)) {
				return EImageSize.D;
			} else if (thumbnail < (startThumbnail + 6)) {
				return EImageSize.E;
			} else if (thumbnail < (startThumbnail + 7)) {
				return EImageSize.F;
			} else if (thumbnail < (startThumbnail + 8)) {
				return EImageSize.G;
			} else {
				return EImageSize.H;
			}
			
			//TODO
		} else if (thumbnailsToDisplayLeft == 11) {
			if (thumbnail < (startThumbnail + 1)) {
				return EImageSize.C;
			} else if (thumbnail < (startThumbnail + 2)) {
				return EImageSize.D;
			} else if (thumbnail < (startThumbnail + 6)) {
				return EImageSize.E;
			} else if (thumbnail < (startThumbnail + 7)) {
				return EImageSize.F;
			} else if (thumbnail < (startThumbnail + 8)) {
				return EImageSize.G;
			} else {
				return EImageSize.H;
			}
			
			
		} else if (thumbnailsToDisplayLeft == 11) {
			if (thumbnail < (startThumbnail + 1)) {
				return EImageSize.C;
			} else if (thumbnail < (startThumbnail + 2)) {
				return EImageSize.D;
			} else if (thumbnail < (startThumbnail + 6)) {
				return EImageSize.E;
			} else if (thumbnail < (startThumbnail + 7)) {
				return EImageSize.F;
			} else if (thumbnail < (startThumbnail + 8)) {
				return EImageSize.G;
			} else {
				return EImageSize.H;
			}
			
			
		} else if (thumbnailsToDisplayLeft <= 50) {
			if (thumbnail < (startThumbnail + 1)) {
				return EImageSize.C;
			} else if (thumbnail < (startThumbnail + 2)) {
				return EImageSize.D;
			} else if (thumbnail < (startThumbnail + 3)) {
				return EImageSize.E;
			} else if (thumbnail < (startThumbnail + 4)) {
				return EImageSize.F;
			} else if (thumbnail < (startThumbnail + 5)) {
				return EImageSize.G;
			} else {
				return EImageSize.H;
			}
			
		// 
		} else if (thumbnailsToDisplayLeft <= 65) {
			if (thumbnail < (startThumbnail + 1)) {
				return EImageSize.C;
			} else if (thumbnail < (startThumbnail + 2)) {
				return EImageSize.D;
			} else if (thumbnail < (startThumbnail + 3)) {
				return EImageSize.E;
			} else if (thumbnail < (startThumbnail + 4)) {
				return EImageSize.F;
			} else if (thumbnail < (startThumbnail + 8)) {
				return EImageSize.G;
			} else if (thumbnail < (startThumbnail + 12)) {
				return EImageSize.H;
			} else {
				return EImageSize.I;
			}
			
		// if there are more than 65 pictures left to display
		} else {
			if (thumbnail == (currentThumbnailNo + 2)) {
				return EImageSize.D;
			} else if (thumbnail == (currentThumbnailNo + 4)) {
				return EImageSize.E;
			} else if (thumbnail < (currentThumbnailNo + 6)) {
				return EImageSize.F;
			} else if (thumbnail < (currentThumbnailNo + 8)) {
				return EImageSize.G;
			} else if (thumbnail < (currentThumbnailNo + 10)) {
				return EImageSize.H;
			} else {
				return EImageSize.I;
			}
		}
	}
	
	
	private Bitmap getBitmapWithSize(int thumbnail, EImageSize thumbnailSize) {
		switch (thumbnailSize) {
		case A:
			return imageIO.getThumbnail(thumbnailFiles.get(thumbnail).getName(), EImageSize.A);
		case B:
			return imageIO.getThumbnail(thumbnailFiles.get(thumbnail).getName(), EImageSize.B);
		case C:
			return imageIO.getThumbnail(thumbnailFiles.get(thumbnail).getName(), EImageSize.C);
		case D:
			return thumbnailsD.get(thumbnail);
		case E:
			return thumbnailsE.get(thumbnail);
		case F:
			return thumbnailsF.get(thumbnail);
		case G:
			return thumbnailsG.get(thumbnail);
		case H:
			return thumbnailsH.get(thumbnail);
		case I:
			return thumbnailsI.get(thumbnail);
		default:
			return null;
		}
	}
}