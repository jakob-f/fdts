package at.ac.tuwien.media.io.file;

import at.ac.tuwien.media.io.file.model.Dimension;

// class to define various thumbnail types
public enum EThumbnailType {
	// IMPORTANT: screen dimension on test device is: 1196 *720
	A ("A", new Dimension(524), 5, 5),
	B ("B", new Dimension(325), 3, 3),
	C ("C", new Dimension(233), 3, 3),
	D ("D", new Dimension(115, C.getDimension().getHeight()), 2, 2),
	E ("E", new Dimension(57, C.getDimension().getHeight()), 1, 1),
	F ("F", new Dimension(27, C.getDimension().getHeight()), 1, 1),
	G ("G", new Dimension(14, C.getDimension().getHeight()), 1, 0),
	H ("H", new Dimension(7, C.getDimension().getHeight()), 1, 0),
	I ("I", new Dimension(4, C.getDimension().getHeight()), 0, 0);
	
	private String name;
	private Dimension dimension;
	private int paddingLeft;
	private int paddingRight;
	
	private EThumbnailType(String name, Dimension dimension, int paddingLeft, int paddingRight) {
		this.name = name;
		this.dimension = dimension;
		this.paddingLeft = paddingLeft;
		this.paddingRight = paddingRight;
	}
	
	public String getName() {
		return name;
	}
	
	public Dimension getDimension() {
		return dimension;
	}

	public int getPaddingLeft() {
		return paddingLeft;
	}

	public int getPaddingRight() {
		return paddingRight;
	}
	
	public int getTotalWidth() {
		return paddingLeft + paddingRight + dimension.getWidth();
	}
}