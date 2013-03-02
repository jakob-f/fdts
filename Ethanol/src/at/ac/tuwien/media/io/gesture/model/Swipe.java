package at.ac.tuwien.media.io.gesture.model;

import at.ac.tuwien.media.io.gesture.ERectangleType;

/**
 * The {@link Swipe} class has a start rectangle of type {@link Rectangle} and an end rectangle.
 * A swipe gesture always starts in the first one and ends in the second one.
 * 
 * @author Jakob Frohnwieser (jakob.frohnwieser@gmx.at)
 */
public class Swipe {
	private ERectangleType start;
	private ERectangleType end;
	
	public Swipe(ERectangleType start, ERectangleType end) {
		this.start = start;
		this.end = end;
	}
	
	public ERectangleType getStart() {
		return start;
	}
	
	public ERectangleType getEnd() {
		return end;
	}

	// we need our own equals method
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Swipe) {
			// return true if booth swipes have the same start end rectangle
			return ((start == ((Swipe) obj).getStart()) && (end == ((Swipe) obj).getEnd()));
		}
		
		return false;
	}
}