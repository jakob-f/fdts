package at.ac.tuwien.media.util;

public enum EImageSize {
	A ("A", new Dimension(208, 120)),
	B ("B", new Dimension(176, 100)),
	C ("C", new Dimension(160, 9)),
	D ("D", new Dimension(50, 5)),
	E ("E", new Dimension(50, 5)),
	F ("F", new Dimension(50, 5));
	
	private String name;
	private Dimension dimension;
	
	private EImageSize(String name, Dimension dimension) {
		this.name = name;
		this.dimension = dimension;
	}
	
	public String getName() {
		return name;
	}
	
	public Dimension getDimension() {
		return dimension;
	}
}
