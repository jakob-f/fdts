package at.ac.tuwien.media.io.file.model;

/**
 * The {@link Dimension} class is used to define a pictures width and height.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class Dimension {
	private int width;
	private int height;
	
	public Dimension(final int width, final int height) {
		this.width = width;
		this.height = height;
	}
	
	public Dimension(final float width, final float height) {
		this.width = (int) Math.floor(width);
		this.height = (int) Math.floor(height);
	}
	
	// convenience method to use with 16:9 landscape format pictures
	public Dimension(final int width) {
		this(width, (int) ((width / 16.0f) * 9.0f));
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	@Override
	public String toString() {
		return "Dimension [width=" + width + ", height=" + height + "]";
	}
}