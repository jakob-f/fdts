package at.ac.tuwien.media;

import at.ac.tuwien.media.util.Values.EDirection;
import at.ac.tuwien.media.util.Values.EProgram;

public interface IImageSwipe {
	public void skipToImage(EDirection direction, int interval);
	
	public void skipToImageFromRow(int row, int percent);
	
	public void fixOrReleaseImage();
	
	public void startExternalProgram(EProgram program);
}
