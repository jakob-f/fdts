package at.ac.tuwien.media.io.file.model;

/**
 * The {@link Dimension} class is used to define a pictures width and height.
 * 
 * @author Jakob Frohnwieser (jakob.frohnwieser@gmx.at)
 */
public class Dimension {
	private int width;
	private int height;
	
	public Dimension() {}
	
	public Dimension(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	// convenience method to use with 16:9 format pictures
	public Dimension(int width) {
		this(width, (width / 16) * 9);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
