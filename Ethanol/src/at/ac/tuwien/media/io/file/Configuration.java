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
	private final static String CONFIG_FILE_PATH = Value.ROOT_FOLDER + Value.CONFIG_FILE + ".xml";
	private static Properties properties;
	
	/**
	 * Gets a configuration property with the given name
	 * 
	 * @param name the name of the property to get
	 * @return the value of the property or <code>null</code>
	 */
	public static String get(final String name) {
		try {
			if (properties == null) {
				readProperties();
			}
			
			return properties.getProperty(name);
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
		if (properties == null) {
			readProperties();
		}
		
		// set new property and write to file
		properties.setProperty(name, value);
		writeProperties();
	}
	
	/**
	 * Gets a configuration property with the given name as a <code>boolean</code>
	 * 
	 * @param name the name of the property to get
	 * @return <code>true</code> iff the property was true or <code>false</code> otherwise
	 */
	public static boolean getAsBoolean(final String name) {
		return get(name) != null ? get(name).equals("true") : false;
	}
	
	// reads the properties file
	private static void readProperties() throws EthanolException {
		try {
			properties = new Properties();
			properties.loadFromXML(new FileInputStream(CONFIG_FILE_PATH));
			
			EthanolLogger.addDebugMessage("Read properies file");
		} catch (IOException ioe) {
			// properties file was not found			
			// give a debug message
			EthanolLogger.addDebugMessage("Properties file was not found, wirte a new one");
			
			// write a new properties file
			resetPropertiesFile();				
		}
	}
	
	// write a new properties file with default values
	private static void resetPropertiesFile() throws EthanolException {
		// set default properties
		properties.setProperty(Value.CONFIG_DEBUG, Value.CONFIG_DEFAULT_VALUE_DEBUG);
		properties.setProperty(Value.CONFIG_IMAGE_FOLDER, Value.CONFIG_DEFAULT_VALUE_IMAGE_FOLDER);
		properties.setProperty(Value.CONFIG_RESET, Value.CONFIG_DEFAULT_VALUE_RESET);
		properties.setProperty(Value.CONFIG_ROTATE_IMAGES, Value.CONFIG_DEFAULT_VALUE_ROTATE_IMAGES);
		properties.setProperty(Value.CONFIG_WARP_IMAGES, Value.CONFIG_DEFAULT_VALUE_WARP_IMAGES);

		// write properties file to sd card
		writeProperties();
	}

	// writes the properties file
	private static void writeProperties() throws EthanolException {
		try {
			properties.storeToXML(new FileOutputStream(CONFIG_FILE_PATH), Value.CONFIG_COMMENT);
			
			EthanolLogger.addDebugMessage("Wrote properies file");
		} catch (IOException ioe) {
			throw new EthanolException("cannot write configuration file", ioe);
		}
	}
}