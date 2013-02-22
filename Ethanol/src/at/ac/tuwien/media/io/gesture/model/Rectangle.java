package at.ac.tuwien.media.io.gesture.model;

import android.graphics.Point;

// a rectangle consists of two edges named a and b
// which define the upper right edge and the lower left edge
// the other edges can be inferred
public class Rectangle {
	private Point a;
	private Point b;
	
	public Rectangle(Point a, Point b) {
		// x and y of b have to be greater than x and y of a
		if (a.x < b.x || a.y < b.y) {
			System.err.println("Warning setting " + getClass() + ": The coordinates of Point b have to be greater than a!");
		}
		
		this.a = a;
		this.b = b;
	}
	
    public Point getA() {
		return a;
	}

	public Point getB() {
		return b;
	}

	public boolean isPointInRectangle(Point p) {
		// return true if the point lies within the rectangle
    	return (p.x >= a.x && p.x < b.x && p.y >= a.y && p.y < b.y);
    }
}
