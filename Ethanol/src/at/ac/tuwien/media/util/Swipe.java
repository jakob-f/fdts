package at.ac.tuwien.media.util;

public class Swipe {
	private ERectangles start;
	private ERectangles end;
	
	public Swipe(ERectangles start, ERectangles end) {
		this.start = start;
		this.end = end;
	}
	
	public ERectangles getStart() {
		return start;
	}
	
	public void setStart(ERectangles start) {
		this.start = start;
	}
	
	public ERectangles getEnd() {
		return end;
	}
	
	public void setEnd(ERectangles end) {
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