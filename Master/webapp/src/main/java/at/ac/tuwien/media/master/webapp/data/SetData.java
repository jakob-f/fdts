package at.ac.tuwien.media.master.webapp.data;

import javax.annotation.Nonnull;

import at.ac.tuwien.media.master.webappapi.db.model.Set;

public class SetData {
    private long m_nId;
    private String m_sTimeStamp;
    private String m_sName;
    private String m_sMetaContent;

    public SetData() {
    }

    public SetData(@Nonnull final Set aSet) {
	if (aSet == null)
	    throw new NullPointerException("set");

	m_nId = aSet.getId();
	m_sTimeStamp = aSet.getTimeStamp();
	m_sName = aSet.getName();
	m_sMetaContent = aSet.getMetaContent();
    }

    public long getId() {
	return m_nId;
    }

    public void setId(final long m_nId) {
	this.m_nId = m_nId;
    }

    public String getTimeStamp() {
	return m_sTimeStamp;
    }

    public void setTimeStamp(final String m_sTimeStamp) {
	this.m_sTimeStamp = m_sTimeStamp;
    }

    public String getName() {
	return m_sName;
    }

    public void setName(final String m_sName) {
	this.m_sName = m_sName;
    }

    public String getMetaContent() {
	return m_sMetaContent;
    }

    public void setMetaContent(final String m_sMetaContent) {
	this.m_sMetaContent = m_sMetaContent;
    }
}
