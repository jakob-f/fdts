package at.ac.tuwien.media.methanol.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import at.ac.tuwien.media.methanol.util.exception.EthanolException;

/**
 * The {@link Configuration} class is used to read and persist various preferences used in the application.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class Configuration {
	private final static String CONFIG_FILE_PATH = Value.ETHANOL_ROOT_FOLDER + Value.CONFIG_FILENAME + ".xml";
	private static Properties configurations;
	
	/**
	 * Gets a configuration property with the given name
	 * 
	 * @param key the name of the property to get
	 * @return the value of the property or <code>null</code>
	 */
	public static String getAsString(final String key) {
		try {
			if (configurations == null) {
				readConfigurations();
			}
			
			return configurations.getProperty(key);
		} catch (EthanolException ee) {
			ee.printStackTrace();
			
			return null;
		}
	}
	
	/**
	 * Gets a configuration property with the given name as a <code>boolean</code>
	 * 
	 * @param key the name of the property to get
	 * @return <code>true</code> iff the property was true or <code>false</code> otherwise
	 */
	public static boolean getAsBoolean(final String key) {
		final String value = getAsString(key);
		
		return value != null ? value.equals("true") : false;
	}
	
	/**
	 * Sets a configuration property with the given name and value
	 * 
	 * @param name the name of the property to set
	 * @param value the value of the property to set
	 * @throws EthanolException thrown if no properties file was found
	 */
	public static void set(final String name, final String value) throws EthanolException {
		if (configurations == null) {
			readConfigurations();
		}
		
		// set new property and write to file
		configurations.setProperty(name, value);
		EthanolLogger.addDebugMessage("Set property '" + name + "', value '" + value + "'");
		
		writeConfigurations();
	}
	
	/**
	 * Sets a configuration property with the given name and <code>boolean</code> value
	 * 
	 * @param name the name of the property to set
	 * @param value the value of the property to set
	 * @throws EthanolException thrown if no properties file was found
	 */
	public static void set(final String name, final boolean value) throws EthanolException {
		if (configurations == null) {
			readConfigurations();
		}
		
		// set new property and write to file
		configurations.setProperty(name, String.valueOf(value));
		EthanolLogger.addDebugMessage("Set property '" + name + "', value '" + value + "'");
		
		writeConfigurations();
	}
	
	/**
	 * Writes a new configuration file with default values
	 * 
	 * @return <code>true</code> if critical values (i.e. values that need to restart the application) have changed,
	 * 			<code>false</code> otherwise
	 * @throws EthanolException throw if the new configuration file cannot be accessed
	 */
	public static boolean resetConfigurationFile() throws EthanolException {
		// check if critical values have changed
		// also check if configuration file already exists
		final boolean needRestart = configurations.getProperty(Value.CONFIG_IMAGE_FOLDER) != null ? 
				(!getAsString(Value.CONFIG_IMAGE_FOLDER).equals(Value.CONFIG_DEFAULT_VALUE_IMAGE_FOLDER) ||
						getAsBoolean(Value.CONFIG_RESET) != Value.CONFIG_DEFAULT_VALUE_RESET ||
						getAsBoolean(Value.CONFIG_ROTATE_IMAGES) != Value.CONFIG_DEFAULT_VALUE_ROTATE_IMAGES ||
						getAsBoolean(Value.CONFIG_CROP_IMAGES) != Value.CONFIG_DEFAULT_VALUE_CROP_IMAGES)
				: false;

		// set default properties
		configurations.setProperty(Value.CONFIG_AUTOSAVE, String.valueOf(Value.CONFIG_DEFAULT_VALUE_AUTOSAVE));
		configurations.setProperty(Value.CONFIG_CROP_IMAGES, String.valueOf(Value.CONFIG_DEFAULT_VALUE_CROP_IMAGES));
		configurations.setProperty(Value.CONFIG_DEBUG, String.valueOf(Value.CONFIG_DEFAULT_VALUE_DEBUG));
		configurations.setProperty(Value.CONFIG_IMAGE_FOLDER, Value.CONFIG_DEFAULT_VALUE_IMAGE_FOLDER);
		configurations.setProperty(Value.CONFIG_JUMP_BACK, String.valueOf(Value.CONFIG_DEFAULT_VALUE_JUMP_BACK));
		configurations.setProperty(Value.CONFIG_PREVIEW_BACK, String.valueOf(Value.CONFIG_DEFAULT_VALUE_PREVIEW_BACK));
		configurations.setProperty(Value.CONFIG_RESET, String.valueOf(Value.CONFIG_DEFAULT_VALUE_RESET));
		configurations.setProperty(Value.CONFIG_ROTATE_IMAGES, String.valueOf(Value.CONFIG_DEFAULT_VALUE_ROTATE_IMAGES));
		configurations.setProperty(Value.CONFIG_SWIPE_SCROLL, String.valueOf(Value.CONFIG_DEFAULT_VALUE_SWIPE_SCROLL));
		configurations.setProperty(Value.CONFIG_SWIPE_SELECT, String.valueOf(Value.CONFIG_DEFAULT_VALUE_SWIPE_SELECT));
		configurations.setProperty(Value.CONFIG_SWIPE_SIMPLE, String.valueOf(Value.CONFIG_DEFAULT_VALUE_SWIPE_SIMPLE));
		configurations.setProperty(Value.CONFIG_SWIPE_SLIDE, String.valueOf(Value.CONFIG_DEFAULT_VALUE_SWIPE_SLIDE));
		configurations.setProperty(Value.CONFIG_SWIPE_VERTICAL, String.valueOf(Value.CONFIG_DEFAULT_VALUE_SWIPE_VERTICAL));
		
		// write configuration file to sd card
		writeConfigurations();
		
		return needRestart;
	}
	
	// reads the configuration file
	private static void readConfigurations() throws EthanolException {
		try {
			configurations = new Properties();
			configurations.loadFromXML(new FileInputStream(CONFIG_FILE_PATH));
			
			EthanolLogger.addDebugMessage("Read configuration file");
		} catch (IOException ioe) {
			// configuration file was not found			
			// give a debug message
			EthanolLogger.addDebugMessage("Configuration file was not found, wirte a new one");
			
			// write a new configuration file
			resetConfigurationFile();				
		}
	}

	// writes the configuration file
	private static void writeConfigurations() throws EthanolException {
		try {
			configurations.storeToXML(new FileOutputStream(CONFIG_FILE_PATH), Value.CONFIG_COMMENT);
			
			EthanolLogger.addDebugMessage("Wrote configuration file");
		} catch (IOException ioe) {
			ioe.printStackTrace();
			
			throw new EthanolException("cannot write configuration file", ioe);
		}
	}
}