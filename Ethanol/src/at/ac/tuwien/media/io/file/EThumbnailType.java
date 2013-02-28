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
	G ("G", new Dimension(13, C.getDimension().getHeight()), 1, 0),
	H ("H", new Dimension(6, C.getDimension().getHeight()), 1, 0),
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
	
	public EThumbnailType getNextSmallerThumbnailType() {
		// return the next smaller thumbnail type
		switch (this) {
			case A:
				return EThumbnailType.B;
			case B:
				return EThumbnailType.C;
			case C:
				return EThumbnailType.D;
			case D:
				return EThumbnailType.E;
			case E:
				return EThumbnailType.F;
			case F:
				return EThumbnailType.G;
			case G:
				return EThumbnailType.H;
			case H:
			case I:
			default:
				return EThumbnailType.I;
		}
	}
	
	public EThumbnailType getNextBiggerThumbnailType() {
		// return the next bigger thumbnail type
		switch (this) {
			case A:
			case B:
				return EThumbnailType.A;
			case C:
				return EThumbnailType.B;
			case D:
				return EThumbnailType.C;
			case E:
				return EThumbnailType.D;
			case F:
				return EThumbnailType.E;
			case G:
				return EThumbnailType.F;
			case H:
				return EThumbnailType.G;
			case I:
				return EThumbnailType.H;
			default:
				return EThumbnailType.I;
		}
	}
}