package at.ac.tuwien.media.util.exception;

/**
 * {@link EthanolException} encapsulates an exception type for Ethanol.
 * @author Jakob Frohnwieser (jakob.frohnwieser@gmx.at)
 */
@SuppressWarnings("serial")
public class EthanolException extends Exception {
	
	// a simple exception with a message to display
	public EthanolException (String message) {
		super(message);
	}
}
