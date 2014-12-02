package at.ac.tuwien.media.master.webapp.data;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AssetData")
@XmlAccessorType(XmlAccessType.FIELD)
public class AssetData {
    @XmlElement(name = "Id")
    private long m_nId;
    @XmlElement(name = "Name")
    private String m_sName;
    @XmlMimeType("application/octet-stream")
    @XmlElement(name = "AssetData")
    private DataHandler m_aAssetData;
    @XmlElement(name = "ArchiveFilePath")
    private String m_sArchiveFilePath;
    @XmlElement(name = "MetaContent")
    private String m_sMetaContent;
    @XmlElement(name = "IsMetaContent")
    private boolean m_bIsMetaContent;

    public AssetData() {
    }

    public long getId() {
	return m_nId;
    }

    public void setId(final long nId) {
	m_nId = nId;
    }

    public String getName() {
	return m_sName;
    }

    public void setName(final String sName) {
	m_sName = sName;
    }

    public DataHandler getAssetData() {
	return m_aAssetData;
    }

    public void setAssetData(final DataHandler aAssetData) {
	m_aAssetData = aAssetData;
    }

    public String getArchiveFilePath() {
	return m_sArchiveFilePath;
    }

    public void setArchiveFilePath(final String sArchiveFilePath) {
	m_sArchiveFilePath = sArchiveFilePath;
    }

    public String getMetaContent() {
	return m_sMetaContent;
    }

    public void setMetaContent(final String sMetaContent) {
	m_sMetaContent = sMetaContent;
    }

    public boolean isMetaContent() {
	return m_bIsMetaContent;
    }

    public void setISMetaContent(final boolean isMetaContent) {
	m_bIsMetaContent = isMetaContent;
    }
}
