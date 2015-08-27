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
    private String m_sId;
    @XmlElement(name = "Name")
    private String m_sName;
    @XmlElement(name = "MetaContent")
    private String m_sMetaContent;

    public SetData() {
    }

    public SetData(@Nonnull final Set aSet) {
	if (aSet == null)
	    throw new NullPointerException("set");

	setId(aSet.getId());
	setName(aSet.getName());
	setMetaContent(aSet.getMetaContent());
    }

    @Nullable
    public String getId() {
	return m_sId;
    }

    public void setId(@Nonnull final String sId) {
	m_sId = sId;
    }

    @Nullable
    public String getName() {
	return m_sName;
    }

    public void setName(@Nonnull final String sName) {
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
