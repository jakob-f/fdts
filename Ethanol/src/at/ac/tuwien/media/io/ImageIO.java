package at.ac.tuwien.media.io;

import java.util.List;

import android.graphics.Bitmap;
import at.ac.tuwien.media.exception.EthanolException;

public interface ImageIO {
	
	void readAndResizeImages(String directoryName) throws EthanolException;
	
	Bitmap getImage();

	List<Bitmap> getImages();

}
