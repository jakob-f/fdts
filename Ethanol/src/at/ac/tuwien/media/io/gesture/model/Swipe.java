package at.ac.tuwien.media.io.gesture.model;

import at.ac.tuwien.media.io.gesture.ERectangleType;

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
	
	public void setStart(ERectangleType start) {
		this.start = start;
	}
	
	public ERectangleType getEnd() {
		return end;
	}
	
	public void setEnd(ERectangleType end) {
		this.end = end;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Swipe) {
			if (start == ((Swipe) obj).getStart()
					&& end == ((Swipe) obj).getEnd()) {
				return true;
			}
		}
		
		return false;
	}
}