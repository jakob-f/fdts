package at.ac.tuwien.media.io.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import at.ac.tuwien.media.util.EthanolLogger;
import at.ac.tuwien.media.util.Value;
import at.ac.tuwien.media.util.exception.EthanolException;

/**
 * The {@link Configuration} class...
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class Configuration {
	private final static String CONFIG_FILE_PATH = Value.ETHANOL_ROOT_FOLDER + Value.CONFIG_FILE + ".xml";
	private static Properties configurations;
	
	/**
	 * Gets a configuration property with the given name
	 * 
	 * @param name the name of the property to get
	 * @return the value of the property or <code>null</code>
	 */
	public static String getAsString(final String name) {
		try {
			if (configurations == null) {
				readConfigurations();
			}
			
			return configurations.getProperty(name);
		} catch (EthanolException ee) {
			ee.printStackTrace();
			
			return null;
		}
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
	 * Gets a configuration property with the given name as a <code>boolean</code>
	 * 
	 * @param name the name of the property to get
	 * @return <code>true</code> iff the property was true or <code>false</code> otherwise
	 */
	public static boolean getAsBoolean(final String name) {
		return getAsString(name) != null ? getAsString(name).equals("true") : false;
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
		final boolean needRestart = !getAsString(Value.CONFIG_IMAGE_FOLDER).equals(Value.CONFIG_DEFAULT_VALUE_IMAGE_FOLDER) ||
				getAsBoolean(Value.CONFIG_RESET) != Value.CONFIG_DEFAULT_VALUE_RESET ||
				getAsBoolean(Value.CONFIG_ROTATE_IMAGES) != Value.CONFIG_DEFAULT_VALUE_ROTATE_IMAGES ||
				getAsBoolean(Value.CONFIG_WARP_IMAGES) != Value.CONFIG_DEFAULT_VALUE_WARP_IMAGES;
		
		// set default properties
		configurations.setProperty(Value.CONFIG_DEBUG, String.valueOf(Value.CONFIG_DEFAULT_VALUE_DEBUG));
		configurations.setProperty(Value.CONFIG_IMAGE_FOLDER, Value.CONFIG_DEFAULT_VALUE_IMAGE_FOLDER);
		configurations.setProperty(Value.CONFIG_RESET, String.valueOf(Value.CONFIG_DEFAULT_VALUE_RESET));
		configurations.setProperty(Value.CONFIG_ROTATE_IMAGES, String.valueOf(Value.CONFIG_DEFAULT_VALUE_ROTATE_IMAGES));
		configurations.setProperty(Value.CONFIG_WARP_IMAGES, String.valueOf(Value.CONFIG_DEFAULT_VALUE_WARP_IMAGES));
		
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
			throw new EthanolException("cannot write configuration file", ioe);
		}
	}
}