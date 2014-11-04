package at.ac.tuwien.media.master.webappapi.model;

import javax.annotation.Nullable;

public class Group {
    private final long m_nId;
    private String m_sName;

    public Group(@Nullable final String sName) {
	m_nId = IdFactory.getInstance().getNextId();
	m_sName = sName;
    }

    public long getId() {
	return m_nId;
    }

    @Nullable
    public String getName() {
	return m_sName;
    }

    public Group setName(@Nullable final String sName) {
	m_sName = sName;

	return this;
    }
}
