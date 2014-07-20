package at.ac.tuwien.media.methanol.io.file.model;

/**
 * The {@link Dimension} class is used to define a pictures width and height.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class Dimension {
	private final int width;
	private final int height;
	
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + width;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Dimension other = (Dimension) obj;
		if (height != other.height)
			return false;
		if (width != other.width)
			return false;
		return true;
	}
}