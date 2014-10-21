package at.ac.tuwien.media.master.transcoderui.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.transcoderui.util.Value;

public final class Configuration {
    private static Properties s_aProperties;

    private Configuration() {
    }

    // stores the properties file
    private static void _storeProperties() {
	if (s_aProperties == null)
	    throw new NullPointerException("properties");

	FileOutputStream aFOS = null;

	try {
	    aFOS = new FileOutputStream(Value.PROPERTIES_PATH);
	    s_aProperties.storeToXML(aFOS, "properties for transcoder ui");
	} catch (final Exception aException) {
	    aException.printStackTrace();
	} finally {
	    if (aFOS != null)
		try {
		    aFOS.close();
		} catch (final IOException aIOException) {
		    aIOException.printStackTrace();
		}
	}
    }

    /**
     * Writes a new properties file with default values
     *
     * @return <code>true</code> if critical values (i.e. values that need to
     *         restart the application) have changed, <code>false</code>
     *         otherwise
     * @throws MethanolException
     *             throw if the new configuration file cannot be accessed
     */
    private static void _resetProperties() {
	if (s_aProperties == null)
	    throw new NullPointerException("properties");

	// set default properties
	s_aProperties.setProperty(ConfigurationValue.FILEPATH_COPY.getKey(), Value.DEFAULT_EMPTY);
	s_aProperties.setProperty(ConfigurationValue.FILEPATH_METADATA.getKey(), Value.DEFAULT_FILEPATH);
	s_aProperties.setProperty(ConfigurationValue.FILEPATH_VIDEO.getKey(), Value.DEFAULT_FILEPATH);
	s_aProperties.setProperty(ConfigurationValue.IS_PASSWORD_SAVE.getKey(), String.valueOf(Value.DEFAULT_IS_SELECTED));
	s_aProperties.setProperty(ConfigurationValue.IS_SELECTED_COPY.getKey(), String.valueOf(Value.DEFAULT_IS_SELECTED));
	s_aProperties.setProperty(ConfigurationValue.IS_SELECTED_UPLOAD.getKey(), String.valueOf(Value.DEFAULT_IS_SELECTED));
	s_aProperties.setProperty(ConfigurationValue.LANGUAGE.getKey(), Locale.getDefault().getLanguage());
	s_aProperties.setProperty(ConfigurationValue.PASSWORD.getKey(), Value.DEFAULT_EMPTY);
	s_aProperties.setProperty(ConfigurationValue.SELECTED_PROJECT.getKey(), Value.DEFAULT_EMPTY);
	s_aProperties.setProperty(ConfigurationValue.SERVER_URL.getKey(), "http://localhost:8080/webapp/ws?wsdl");
	s_aProperties.setProperty(ConfigurationValue.USERNAME.getKey(), Value.DEFAULT_EMPTY);

	// write configuration filesystem
	_storeProperties();
    }

    // loads the properties file
    private static void _loadProperties() {
	s_aProperties = new Properties();

	final File aPropertiesFile = new File(Value.PROPERTIES_PATH);

	// properties file exists
	if (aPropertiesFile.exists()) {
	    FileInputStream aFIS = null;

	    try {
		aFIS = new FileInputStream(aPropertiesFile);
		s_aProperties.loadFromXML(aFIS);
	    } catch (final Exception aException) {
		aException.printStackTrace();
	    } finally {
		if (aFIS != null)
		    try {
			aFIS.close();
		    } catch (final IOException aIOException) {
			aIOException.printStackTrace();
		    }
	    }
	} else
	    // write new properties file
	    _resetProperties();
    }

    /**
     * Sets a configuration property with the given key and value
     *
     * @param aKey
     *            the configuration property to set
     * @param sValue
     *            the value of the property to set
     */
    public static void set(@Nonnull final ConfigurationValue aKey, @Nonnull final String sValue) {
	if (aKey == null)
	    throw new NullPointerException("key");
	if (sValue == null)
	    throw new NullPointerException("value");

	if (s_aProperties == null)
	    _loadProperties();

	// set new property and write to file
	s_aProperties.setProperty(aKey.getKey(), sValue);

	_storeProperties();
    }

    /**
     * Sets a configuration property with the given key and <code>boolean</code>
     * value
     *
     * @param aKey
     *            the configuration property to set
     * @param bValue
     *            the value of the property to set
     * @throws MethanolException
     *             thrown if no properties file was found
     */
    public static void set(@Nonnull final ConfigurationValue aKey, final boolean bValue) {
	set(aKey, String.valueOf(bValue));
    }

    /**
     * Gets a configuration property with the given name
     *
     * @param aKey
     *            the configuration property to get
     * @return the value of the property or <code>null</code>
     */
    @Nullable
    public static String getAsString(@Nonnull final ConfigurationValue aKey) {
	if (aKey == null)
	    throw new NullPointerException("key");

	if (s_aProperties == null)
	    _loadProperties();

	return s_aProperties.getProperty(aKey.getKey());
    }

    /**
     * Gets a configuration property with the given name as a
     * <code>boolean</code>
     *
     * @param aKey
     *            the configuration property to get
     * @return <code>true</code> iff the property was true or <code>false</code>
     *         otherwise
     */
    public static boolean getAsBoolean(@Nonnull final ConfigurationValue aKey) {
	final String sValue = getAsString(aKey);

	return StringUtils.isNotEmpty(sValue) ? sValue.equals("true") : false;
    }
}
