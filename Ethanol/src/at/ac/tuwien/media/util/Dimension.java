package at.ac.tuwien.media.util;

public class Dimension {
	private int width;
	private int height;
	
	public Dimension() {}
	
	public Dimension(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	// to use with 16:9 format pictures
	public Dimension(int width) {
		this(width, (width / 16) * 9);
	}
	
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}	
}
