package at.ac.tuwien.media.ethanol.io.gesture.model;


/**
 * The {@link Swipe} class has a start rectangle of type {@link Rectangle} and an end rectangle.
 * A swipe gesture always starts in the first one and ends in the second one.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class Swipe {
	private final ERectangleType start;
	private final ERectangleType end;
	
	public Swipe(final ERectangleType start, final ERectangleType end) {
		this.start = start;
		this.end = end;
	}
	
	public ERectangleType getStart() {
		return start;
	}
	
	public ERectangleType getEnd() {
		return end;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
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
		Swipe other = (Swipe) obj;
		if (end != other.end)
			return false;
		if (start != other.start)
			return false;
		return true;
	}
}