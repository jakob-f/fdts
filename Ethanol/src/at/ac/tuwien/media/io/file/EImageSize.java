package at.ac.tuwien.media.io.file;

import at.ac.tuwien.media.io.file.model.Dimension;

public enum EImageSize {
	// IMPORTANT: screen size is: 1196 *720
	A ("A", new Dimension(390)),
	B ("B", new Dimension(360)),
	C ("C", new Dimension(160)),
	D ("D", new Dimension(50)),
	E ("E", new Dimension(50)),
	F ("F", new Dimension(50));
	
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
