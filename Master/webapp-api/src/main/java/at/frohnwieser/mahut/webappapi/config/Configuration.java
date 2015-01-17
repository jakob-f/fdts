package at.frohnwieser.mahut.webappapi.config;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.frohnwieser.mahut.commons.AbstractConfiguration;
import at.frohnwieser.mahut.commons.IHasKey;
import at.frohnwieser.mahut.webappapi.config.Configuration.EField;
import at.frohnwieser.mahut.webappapi.util.Value;

public final class Configuration extends AbstractConfiguration<EField> {
    private static Configuration m_aInstance = new Configuration();

    public enum EField implements
	    IHasKey {
	DATA_PATH("datapath"),
	DATA_PATH_ASSETS(""),
	DATA_PATH_META(""),
	DATA_PATH_ONTOLOGY(""),
	DB_PASSWORD("db.password"),
	@Deprecated
	ONTOLOGY_NAME("ontology.name"),
	MAIL_HOST("mail.host"),
	MAIL_PASSWORD("mail.password"),
	MAIL_PORT("mail.port"),
	MAIL_TO_ADDRESS("mail.to.address"),
	MAIL_USERNAME("mail.username");

	private final String f_sKey;

	private EField(@Nonnull final String sKey) {
	    f_sKey = sKey;
	}

	@Override
	public String getKey() {
	    return f_sKey;
	}
    }

    private Configuration() {
	super(Value.FILEPATH_PROPERTIES);
    }

    public static Configuration getInstance() {
	return m_aInstance;
    }

    @Override
    @Nullable
    public String getAsString(@Nonnull final EField aKey) {
	if (aKey == null)
	    throw new NullPointerException("key");

	if (aKey == EField.DATA_PATH_ASSETS)
	    return super.getAsString(EField.DATA_PATH) + File.separator + Value.DATA_FOLDER_ASSETS;
	else if (aKey == EField.DATA_PATH_META)
	    return super.getAsString(EField.DATA_PATH) + File.separator + Value.DATA_FOLDER_META;
	else if (aKey == EField.DATA_PATH_ONTOLOGY)
	    return getAsString(EField.DATA_PATH_META) + File.separator + Value.DATA_FOLDER_ONTOLOGY + File.separator + super.getAsString(EField.ONTOLOGY_NAME);

	return super.getAsString(aKey);
    }

    // Writes a new properties file with default values
    @Override
    protected void _resetProperties() {
	if (m_aProperties == null)
	    throw new NullPointerException("properties");

	// set default properties
	m_aProperties.setProperty(EField.DATA_PATH.getKey(), Value.DATA_PATH_DEFAULT);
	m_aProperties.setProperty(EField.DB_PASSWORD.getKey(), Value.DB_PASSWORD_DEFAULT);
	m_aProperties.setProperty(EField.MAIL_HOST.getKey(), "");
	m_aProperties.setProperty(EField.MAIL_PASSWORD.getKey(), "");
	m_aProperties.setProperty(EField.MAIL_PORT.getKey(), "");
	m_aProperties.setProperty(EField.MAIL_TO_ADDRESS.getKey(), "");
	m_aProperties.setProperty(EField.MAIL_USERNAME.getKey(), "");
	m_aProperties.setProperty(EField.ONTOLOGY_NAME.getKey(), Value.ONTOLOGY_NAME_DEFAULT);

	super._resetProperties();
    }
}
