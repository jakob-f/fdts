package at.frohnwieser.mahut.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractConfiguration<E extends IHasKey> {
    private static final String ENCRYPTION_PREFIX = "ENC(";
    private static final String ENCRYPTION_SUFFIX = ")";
    private final String m_sPropertiesFilepath;
    protected Properties m_aProperties;

    protected AbstractConfiguration(@Nonnull final String sPropertiesFilepath) {
	if (StringUtils.isEmpty(sPropertiesFilepath))
	    throw new NullPointerException("properties filepath");

	m_sPropertiesFilepath = sPropertiesFilepath;
    }

    // stores the properties file
    private void _storeProperties() {
	if (m_aProperties == null)
	    throw new NullPointerException("properties");

	FileOutputStream aFOS = null;

	try {
	    // Utils.getDirectorySave(Value.FILEPATH_MAHUT);
	    aFOS = new FileOutputStream(m_sPropertiesFilepath);
	    m_aProperties.storeToXML(aFOS, "properties mahut");
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

    // Writes a new properties file with default values
    protected void _resetProperties() {
	// write configuration filesystem
	_storeProperties();
    };

    // loads the properties file
    private void _loadProperties() {
	m_aProperties = new Properties();
	final File aPropertiesFile = new File(m_sPropertiesFilepath);

	// properties file exists
	if (aPropertiesFile.exists()) {
	    FileInputStream aFIS = null;

	    try {
		aFIS = new FileInputStream(aPropertiesFile);
		m_aProperties.loadFromXML(aFIS);
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
    public void set(@Nonnull final E aKey, @Nonnull final String sValue) {
	if (aKey == null)
	    throw new NullPointerException("key");
	if (sValue == null)
	    throw new NullPointerException("value");

	if (m_aProperties == null)
	    _loadProperties();

	// set new property and write to file
	m_aProperties.setProperty(aKey.getKey(), sValue);

	_storeProperties();
    }

    /**
     * Sets a configuration property encrypted with the given key and value
     *
     * @param aKey
     *            the configuration property to set
     * @param sValue
     *            the value of the property to set
     */
    public void setEncrypted(@Nonnull final E aKey, @Nonnull final String sValue) {
	if (aKey == null)
	    throw new NullPointerException("key");
	if (sValue == null)
	    throw new NullPointerException("value");

	// set new encrypted property and write to file
	final byte[] aEncrypted = EncryptionUtils.encrypt(sValue, EncryptionUtils.getPassword());
	if (aEncrypted != null)
	    set(aKey, ENCRYPTION_PREFIX + Base64.getEncoder().encodeToString(aEncrypted) + ENCRYPTION_SUFFIX);
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
    public void set(@Nonnull final E aKey, final boolean bValue) {
	set(aKey, String.valueOf(bValue));
    }

    /**
     * Sets a configuration property with the given key and <code>long</code>
     * value
     *
     * @param aKey
     *            the configuration property to set
     * @param nValue
     *            the value of the property to set
     * @throws MethanolException
     *             thrown if no properties file was found
     */
    public void set(@Nonnull final E aKey, final long nValue) {
	set(aKey, String.valueOf(nValue));
    }

    @Nullable
    private String _getDecrypted(@Nullable final String sValue) {
	if (StringUtils.isNotEmpty(sValue) && sValue.startsWith(ENCRYPTION_PREFIX) && sValue.endsWith(ENCRYPTION_SUFFIX)) {
	    final String sEncrypted = sValue.substring(ENCRYPTION_PREFIX.length(), sValue.length() - ENCRYPTION_SUFFIX.length());
	    final byte[] aProperty = EncryptionUtils.decrypt(Base64.getDecoder().decode(sEncrypted), EncryptionUtils.getPassword());

	    if (aProperty != null)
		return new String(aProperty);
	}

	return null;
    }

    /**
     * Gets a configuration property with the given name
     *
     * @param aKey
     *            the configuration property to get
     * @return the value of the property or <code>null</code>
     */
    @Nullable
    public String getAsString(@Nonnull final E aKey) {
	if (aKey == null)
	    throw new NullPointerException("key");

	if (m_aProperties == null)
	    _loadProperties();

	final String sValue = m_aProperties.getProperty(aKey.getKey());

	if (sValue.matches(ENCRYPTION_PREFIX + ".*" + ENCRYPTION_SUFFIX))
	    return _getDecrypted(sValue);

	return sValue;
    }

    @Nonnull
    public String getAsStringOrEmpty(@Nonnull final E aKey) {
	final String sProperty = getAsString(aKey);

	return sProperty != null ? sProperty : "";
    }

    @Nonnull
    public long getAsLong(@Nonnull final E aKey) {
	try {
	    final String sProperty = getAsString(aKey);

	    if (StringUtils.isNotEmpty(sProperty))
		return Long.valueOf(sProperty);
	} catch (final NumberFormatException aNFException) {
	}

	return -1;
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
    public boolean getAsBoolean(@Nonnull final E aKey) {
	final String sValue = getAsString(aKey);

	return StringUtils.isNotEmpty(sValue) ? sValue.equals("true") : false;
    }
}
