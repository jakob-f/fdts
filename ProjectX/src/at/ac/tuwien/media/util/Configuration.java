package at.ac.tuwien.media.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import at.ac.tuwien.media.util.exception.XException;

/**
 * The {@link Configuration} class is used to read and persist various preferences used in the application.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class Configuration {
	private final static String CONFIG_FILE_PATH = Value.ROOT_FOLDER + Value.CONFIG_FILENAME + ".xml";
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
		} catch (XException xe) {
			xe.printStackTrace();
			
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
	 * @throws XException thrown if no properties file was found
	 */
	public static void set(final String name, final String value) throws XException {
		if (configurations == null) {
			readConfigurations();
		}
		
		// set new property and write to file
		configurations.setProperty(name, value);
		
		writeConfigurations();
	}
	
	/**
	 * Sets a configuration property with the given name and <code>boolean</code> value
	 * 
	 * @param name the name of the property to set
	 * @param value the value of the property to set
	 * @throws XException thrown if no properties file was found
	 */
	public static void set(final String name, final boolean value) throws XException {
		if (configurations == null) {
			readConfigurations();
		}
		
		// set new property and write to file
		configurations.setProperty(name, String.valueOf(value));
		
		writeConfigurations();
	}
	
	/**
	 * Writes a new configuration file with default values
	 * 
	 * @return <code>true</code> if critical values (i.e. values that need to restart the application) have changed,
	 * 			<code>false</code> otherwise
	 * @throws XException throw if the new configuration file cannot be accessed
	 */
	public static boolean resetConfigurationFile() throws XException {
		// check if critical values have changed
		// also check if configuration file already exists
		final boolean needRestart = configurations.getProperty(Value.CONFIG_IMAGE_FOLDER) != null ? 
				(!getAsString(Value.CONFIG_IMAGE_FOLDER).equals(Value.CONFIG_DEFAULT_VALUE_IMAGE_FOLDER) ||
						getAsBoolean(Value.CONFIG_RESET) != Value.CONFIG_DEFAULT_VALUE_RESET ||
						getAsBoolean(Value.CONFIG_ROTATE_IMAGES) != Value.CONFIG_DEFAULT_VALUE_ROTATE_IMAGES ||
						getAsBoolean(Value.CONFIG_CROP_IMAGES) != Value.CONFIG_DEFAULT_VALUE_CROP_IMAGES)
				: false;

		// set default properties
		configurations.setProperty(Value.CONFIG_CROP_IMAGES, String.valueOf(Value.CONFIG_DEFAULT_VALUE_CROP_IMAGES));
		configurations.setProperty(Value.CONFIG_INSERT_DELETE, String.valueOf(Value.CONFIG_DEFAULT_VALUE_INSERT_DELETE));
		configurations.setProperty(Value.CONFIG_IMAGE_FOLDER, Value.CONFIG_DEFAULT_VALUE_IMAGE_FOLDER);
		configurations.setProperty(Value.CONFIG_RESET, String.valueOf(Value.CONFIG_DEFAULT_VALUE_RESET));
		configurations.setProperty(Value.CONFIG_ROTATE_IMAGES, String.valueOf(Value.CONFIG_DEFAULT_VALUE_ROTATE_IMAGES));
		configurations.setProperty(Value.CONFIG_SWIPE, String.valueOf(Value.CONFIG_DEFAULT_VALUE_SWIPE));
		configurations.setProperty(Value.CONFIG_TAP, String.valueOf(Value.CONFIG_DEFAULT_VALUE_TAP));
		
		// write configuration file to sd card
		writeConfigurations();
		
		return needRestart;
	}
	
	// reads the configuration file
	private static void readConfigurations() throws XException {
		try {
			configurations = new Properties();
			configurations.loadFromXML(new FileInputStream(CONFIG_FILE_PATH));
		
		// configuration file was not found	
		} catch (IOException ioe) {
			// write a new configuration file
			resetConfigurationFile();				
		}
	}

	// writes the configuration file
	private static void writeConfigurations() throws XException {
		try {
			configurations.storeToXML(new FileOutputStream(CONFIG_FILE_PATH), Value.CONFIG_COMMENT);
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}