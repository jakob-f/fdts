package at.ac.tuwien.media.master.webappapi.model;

import java.io.Serializable;

import javax.annotation.Nullable;

import at.ac.tuwien.media.master.webappapi.util.IdFactory;

@SuppressWarnings("serial")
public class Group implements Serializable {
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
