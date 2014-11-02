package at.ac.tuwien.media.master.webappapi.model;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

public class Group {
    private long m_nId;
    private String m_sName;

    public Group(@Nonnegative final long nId, @Nullable final String sName) {
	m_nId = nId;
	m_sName = sName;
    }

    public long getId() {
	return m_nId;
    }

    public void setId(@Nonnegative final long nId) {
	m_nId = nId;
    }

    @Nullable
    public String getName() {
	return m_sName;
    }

    public void setM_sName(@Nullable final String sName) {
	m_sName = sName;
    }
}
