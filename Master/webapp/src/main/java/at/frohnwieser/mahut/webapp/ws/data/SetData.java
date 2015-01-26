package at.frohnwieser.mahut.webapp.ws.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import at.frohnwieser.mahut.webappapi.db.model.Set;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SetData {
    @XmlElement(name = "Id")
    private long m_nId;
    @XmlElement(name = "Name")
    private String m_sName;
    @XmlElement(name = "MetaContent")
    private String m_sMetaContent;

    public SetData() {
    }

    public SetData(@Nonnull final Set aSet) {
	if (aSet == null)
	    throw new NullPointerException("set");

	m_nId = aSet.getId();
	m_sName = aSet.getName();
	m_sMetaContent = aSet.getMetaContent();
    }

    public long getId() {
	return m_nId;
    }

    public void setId(final long nId) {
	m_nId = nId;
    }

    @Nullable
    public String getName() {
	return m_sName;
    }

    public void setName(@Nullable final String sName) {
	m_sName = sName;
    }

    @Nullable
    public String getMetaContent() {
	return m_sMetaContent;
    }

    public void setMetaContent(@Nullable final String sMetaContent) {
	m_sMetaContent = sMetaContent;
    }
}
