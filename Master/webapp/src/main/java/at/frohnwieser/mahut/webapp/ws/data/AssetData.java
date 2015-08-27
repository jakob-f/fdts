package at.frohnwieser.mahut.webapp.ws.data;

import javax.activation.DataHandler;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AssetData")
@XmlAccessorType(XmlAccessType.FIELD)
public class AssetData {
    @XmlElement(name = "Id")
    private String m_sId;
    @XmlElement(name = "Name")
    private String m_sName;
    @XmlElement(name = "MetaContent")
    private String m_sMetaContent;
    @XmlElement(name = "IsMetaContent")
    private boolean m_bIsMetaContent;
    @XmlMimeType("application/octet-stream")
    @XmlElement(name = "AssetData")
    private DataHandler m_aAssetData;

    public AssetData() {
    }

    public String getId() {
	return m_sId;
    }

    public void setId(@Nullable final String sId) {
	m_sId = sId;
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

    public boolean isMetaContent() {
	return m_bIsMetaContent;
    }

    public void setISMetaContent(final boolean isMetaContent) {
	m_bIsMetaContent = isMetaContent;
    }

    @Nullable
    public DataHandler getAssetData() {
	return m_aAssetData;
    }

    public void setAssetData(@Nullable final DataHandler aAssetData) {
	m_aAssetData = aAssetData;
    }
}
