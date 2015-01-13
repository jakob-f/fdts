package at.frohnwieser.mahut.webappapi.config;

import javax.annotation.Nonnull;

import at.frohnwieser.mahut.commons.AbstractConfiguration;
import at.frohnwieser.mahut.commons.IHasKey;
import at.frohnwieser.mahut.webappapi.config.Configuration.EField;
import at.frohnwieser.mahut.webappapi.util.Value;

public final class Configuration extends AbstractConfiguration<EField> {
    private static Configuration m_aInstance = new Configuration();

    public enum EField implements
	    IHasKey {

	DATA_PATH_ASSETS("datapath.assets"),
	DATA_PATH_META("datapath.meta"),
	DATA_PATH_ROOT("datapath.root"),
	DB_PATH("db.path"),
	DB_PASSWORD("db.password"),
	SET_FOLDER_META_CONTENT("setfolder.metacontent"),
	SET_FOLDER_THUMBNAILS("setfolder.thumbnails");

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

    // Writes a new properties file with default values
    @Override
    protected void _resetProperties() {
	if (m_aProperties == null)
	    throw new NullPointerException("properties");

	// set default properties
	m_aProperties.setProperty(EField.DATA_PATH_ASSETS.getKey(), Value.DATA_PATH_ASSETS_DEFAULT);
	m_aProperties.setProperty(EField.DATA_PATH_META.getKey(), Value.DATA_PATH_META_DEFAULT);
	m_aProperties.setProperty(EField.DATA_PATH_ROOT.getKey(), Value.DATA_PATH_ROOT_DEFAULT);
	m_aProperties.setProperty(EField.DB_PATH.getKey(), Value.DB_PATH_DEFAULT);
	m_aProperties.setProperty(EField.DB_PASSWORD.getKey(), Value.DB_PASSWORD_DEFAULT);
	m_aProperties.setProperty(EField.SET_FOLDER_META_CONTENT.getKey(), Value.SET_FOLDER_META_CONTENT_DEFAULT);
	m_aProperties.setProperty(EField.SET_FOLDER_THUMBNAILS.getKey(), Value.SET_FOLDER_THUMBNAILS_DEFAULT);

	super._resetProperties();
    }
}
