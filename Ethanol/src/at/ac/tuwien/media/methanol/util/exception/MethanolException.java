package at.ac.tuwien.media.methanol.util.exception;

/**
 * {@link MethanolException} encapsulates an exception type for Methanol.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
@SuppressWarnings("serial")
public class MethanolException extends Exception {
	
	// a simple exception with a message to display
	public MethanolException (final String message) {
		super(message);
	}
	
	// a simple exception with a message to display and the cause
	public MethanolException (final String message, final Throwable cause) {
		super(message, cause);
	}
}
