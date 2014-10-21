package at.ac.tuwien.media.master.transcoderui.config;

import javax.annotation.Nonnull;

public enum ConfigurationValue {
    FILEPATH_COPY(10, "filepath.copy"),
    FILEPATH_METADATA(20, "filepath.metadata"),
    FILEPATH_VIDEO(30, "filepath.video"),
    IS_PASSWORD_SAVE(40, "password.save"),
    IS_SELECTED_COPY(50, "selected.copy"),
    IS_SELECTED_UPLOAD(60, "selected.upload"),
    LANGUAGE(70, "language"),
    PASSWORD(80, "password"),
    SELECTED_PROJECT(90, "selected.project"),
    SERVER_URL(100, "sever.url"),
    USERNAME(120, "username");

    private int m_nId;
    private String m_sKey;

    ConfigurationValue(final int nId, @Nonnull final String sKey) {
	m_nId = nId;
	m_sKey = sKey;
    }

    public int getId() {
	return m_nId;
    }

    public String getKey() {
	return m_sKey;
    }
}
