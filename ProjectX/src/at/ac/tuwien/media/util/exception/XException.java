package at.ac.tuwien.media.util.exception;

/**
 * {@link XException} encapsulates a simple exception.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
@SuppressWarnings("serial")
public class XException extends Exception {
	
	// a simple exception with a message to display
	public XException (final String message) {
		super(message);
	}
	
	// a simple exception with a message to display and the cause
	public XException (final String message, final Throwable cause) {
		super(message, cause);
	}
}
