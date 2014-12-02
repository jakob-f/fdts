package at.ac.tuwien.media.master.webapp.data;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import at.ac.tuwien.media.master.webappapi.db.model.Set;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SetData {
    @XmlElement(name = "Id")
    private long m_nId;
    @XmlElement(name = "TimeStamp")
    private String m_sTimeStamp;
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
