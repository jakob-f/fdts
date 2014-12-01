package at.ac.tuwien.media.master.webapp.data;

import javax.annotation.Nonnull;

import at.ac.tuwien.media.master.webappapi.db.model.Set;

public class SetData {
    private final long m_nId;
    private final String m_sTimeStamp;
    private final String m_sName;
    private final String m_sMetaContent;

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

    public String getTimeStamp() {
	return m_sTimeStamp;
    }

    public String getName() {
	return m_sName;
    }

    public String getMetaContent() {
	return m_sMetaContent;
    }
}
