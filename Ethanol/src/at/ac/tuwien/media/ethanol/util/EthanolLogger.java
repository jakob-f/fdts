package at.ac.tuwien.media.ethanol.util;

import android.app.Activity;
import android.widget.Toast;
import at.ac.tuwien.media.R;

/**
 * {@link EthanolLogger} acts as a simple logger which displays log messages in a {@link Toast}.
 * In order to display the message the {@link Value} DEBUG has to be set to <code>true</code>.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class EthanolLogger {
	private static Activity parent;
	// a placeholder for various debug messages
	private static String debugMessage = "";
	// saves the start time of an operation
	private static long opStartTime;
	
	public EthanolLogger() {}
	
	public static void setParent(final Activity parent) {
		EthanolLogger.parent = parent;
	}
	
	public static void setOpStartTime(final long opStartTime) {
		EthanolLogger.opStartTime = opStartTime;
	}

	public static void saveCurrentTime() {
		setOpStartTime(System.currentTimeMillis());
	}
	
	public static void addDebugMessageWithOpTime(final String msg) {
		addDebugMessage(msg + " " + (System.currentTimeMillis() - opStartTime) + "ms");
	}
	
	public static void addDebugMessage(final String msg) {
		// add a debug message with a line break if needed
		if (!debugMessage.isEmpty()) {
			debugMessage += "\n";
		}
		
		debugMessage += msg;
	}
	
	public static void displayDebugMessage(final String msg) {
		addDebugMessage(msg);
		displayDebugMessage();
	}
	
	public static void displayDebugMessage() {
		// displays a debug message if wished
		if (Configuration.getAsBoolean(Value.CONFIG_DEBUG)) {
			if (debugMessage.isEmpty()) {
				debugMessage = parent.getResources().getString(R.string.default_debug_message);
			}
			
			// displays a Toast on the screen
			Toast.makeText(parent, debugMessage, Value.DEBUG_DISPLAY_LENGHT).show();
		}
		
		// delete the debug message
		debugMessage = "";
	}
}
