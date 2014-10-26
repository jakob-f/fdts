package at.ac.tuwien.media.master.transcoderui.config;

import javax.annotation.Nonnull;

public enum ConfigurationValue {
    FILEPATH_COPY("filepath.copy"),
    FILEPATH_METADATA("filepath.metadata"),
    FILEPATH_UPLOAD("filepath.upload"),
    IS_PASSWORD_SAVE("password.save"),
    IS_SELECTED_COPY("selected.copy"),
    IS_SELECTED_UPLOAD("selected.upload"),
    LOCALE("locale"),
    PASSWORD("password"),
    SELECTED_PROJECT("selected.project"),
    SERVER_URL("sever.url"),
    USERNAME("username");

    private String m_sKey;

    ConfigurationValue(@Nonnull final String sKey) {
	m_sKey = sKey;
    }

    public String getKey() {
	return m_sKey;
    }
}
