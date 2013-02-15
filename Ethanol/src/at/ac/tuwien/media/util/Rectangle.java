package at.ac.tuwien.media.util;

import android.graphics.Point;

public class Rectangle {
	private Point a;
	// x and y of b have to be greater than x and y of a
	private Point b;
	
	public Rectangle(Point a, Point b) {
		this.a = a;
		this.b = b;
	}
	
    public boolean isPointInRectangle(Point p) {
    	if (p.x >= a.x && p.x < b.x
    			&& p.y >= a.y && p.y < b.y) {
    		
    		return true;
    	}
    	
    	return false;
    }
}
