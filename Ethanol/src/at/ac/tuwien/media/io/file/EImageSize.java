package at.ac.tuwien.media.io.file;

import at.ac.tuwien.media.io.file.model.Dimension;

public enum EImageSize {
	// IMPORTANT: screen size is: 1196 *720
	A ("A", new Dimension(480)),
	B ("B", new Dimension(320)),
	C ("C", new Dimension(214, 120)),
	D ("D", new Dimension(172, 120)),
	E ("E", new Dimension(127, 120)),
	F ("F", new Dimension(104, 120)),
	G ("G", new Dimension(49, 120)),
	H ("H", new Dimension(22, 120)),
	I ("I", new Dimension(2, 120));
	
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
