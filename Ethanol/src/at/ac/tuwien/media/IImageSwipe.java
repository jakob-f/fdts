package at.ac.tuwien.media;

public interface IImageSwipe {
	public void nextImage(int interval);
	
	public void prevImage(int interval);
	
	public void jumpToImage(int row, int percent);
}
