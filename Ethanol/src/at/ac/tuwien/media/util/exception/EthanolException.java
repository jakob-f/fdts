package at.ac.tuwien.media.util.exception;

/**
 * {@link EthanolException} encapsulates an exception type for Ethanol.
 * @author jakob.frohnwieser@gmx.at
 */
@SuppressWarnings("serial")
public class EthanolException extends Exception {
	
	// a simple exception with a message to display
	public EthanolException (final String message) {
		super(message);
	}
	
	// a simple exception with a message to display and the cause
	public EthanolException (final String message, final Throwable cause) {
		super(message, cause);
	}
}
