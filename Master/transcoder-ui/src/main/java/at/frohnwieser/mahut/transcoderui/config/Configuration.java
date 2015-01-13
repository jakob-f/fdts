package at.frohnwieser.mahut.transcoderui.config;

import java.util.Locale;

import javax.annotation.Nonnull;

import at.frohnwieser.mahut.commons.AbstractConfiguration;
import at.frohnwieser.mahut.commons.IHasKey;
import at.frohnwieser.mahut.transcoderui.config.Configuration.EField;
import at.frohnwieser.mahut.transcoderui.util.Value;

public final class Configuration extends AbstractConfiguration<EField> {
    private static Configuration m_aInstance = new Configuration();

    public enum EField implements
	    IHasKey {
	FILEPATH_COPY("filepath.copy"),
	FILEPATH_MATERIALS("filepath.materials"),
	FILEPATH_METACONTENT("filepath.metacontent"),
	IS_PASSWORD_SAVE("password.save"),
	IS_SELECTED_COPY("is.copy"),
	IS_SELECTED_UPLOAD("is.upload"),
	LOCALE("locale"),
	PASSWORD("password"),
	SELECTED_SET("selected.set"),
	SERVER_URL("sever.url"),
	USERNAME("username");

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
	m_aProperties.setProperty(EField.FILEPATH_COPY.getKey(), Value.FILEPATH_DEFAULT);
	m_aProperties.setProperty(EField.FILEPATH_MATERIALS.getKey(), Value.FILEPATH_DEFAULT);
	m_aProperties.setProperty(EField.FILEPATH_METACONTENT.getKey(), Value.FILEPATH_DEFAULT);
	m_aProperties.setProperty(EField.IS_PASSWORD_SAVE.getKey(), String.valueOf(Value.DEFAULT_IS_SELECTED));
	m_aProperties.setProperty(EField.IS_SELECTED_COPY.getKey(), String.valueOf(Value.DEFAULT_IS_SELECTED));
	m_aProperties.setProperty(EField.IS_SELECTED_UPLOAD.getKey(), String.valueOf(Value.DEFAULT_IS_SELECTED));
	m_aProperties.setProperty(EField.LOCALE.getKey(), Locale.getDefault().getLanguage());
	m_aProperties.setProperty(EField.PASSWORD.getKey(), Value.DEFAULT_EMPTY);
	m_aProperties.setProperty(EField.SELECTED_SET.getKey(), Value.DEFAULT_EMPTY);
	m_aProperties.setProperty(EField.SERVER_URL.getKey(), Value.DEFAULT_SERVER_URL);
	m_aProperties.setProperty(EField.USERNAME.getKey(), Value.DEFAULT_EMPTY);

	super._resetProperties();
    }
}
