package at.ac.tuwien.media.ethanol.io.file.model;

import at.ac.tuwien.media.ethanol.util.Value;

/**
 * The {@link EThumbnailType} enum class defines various thumbnail types and sets their {@link Dimension}.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public enum EThumbnailType {
	// IMPORTANT: the usable screen dimension on the test device is: 1196 x 720px
	A ("A", new Dimension(524), 5, Value.THUMBNAIL_HIGHLIGHT_PADDING, 5, Value.THUMBNAIL_HIGHLIGHT_PADDING),
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
	private int paddingTop;
	private int paddingRight;
	private int paddingBottom;
	
	private EThumbnailType(final String name, final Dimension dimension,
			int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
		this.name = name;
		this.dimension = dimension;
		this.paddingLeft = paddingLeft;
		this.paddingTop = paddingTop;
		this.paddingRight = paddingRight;
		this.paddingBottom = paddingBottom;
	}
	
	// sets only padding left and right
	private EThumbnailType(final String name, final Dimension dimension, final int paddingLeft, final int paddingRight) {
		this(name, dimension, paddingLeft, 0, paddingRight, 0);
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

	public int getPaddingTop() {
		return paddingTop;
	}

	public int getPaddingRight() {
		return paddingRight;
	}

	public int getPaddingBottom() {
		return paddingBottom;
	}

	public int getTotalWidth() {
		return paddingLeft + dimension.getWidth() + paddingRight;
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