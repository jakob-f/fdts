package at.ac.tuwien.media.master.webapp.data;

import javax.activation.DataHandler;
import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;

@XmlRootElement(name = "AssetData")
@XmlAccessorType(XmlAccessType.FIELD)
public class AssetData {
    @XmlElement(name = "Id")
    private long m_nId;
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

    public AssetData(final long nId, @Nonnull final DataHandler aAssetData, @Nonnull final String sArchiveFilePath, @Nonnull final String sMetaContent,
	    final boolean bIsMetaContent) {
	if (aAssetData == null)
	    throw new NullPointerException("asset data");
	if (StringUtils.isEmpty(sArchiveFilePath))
	    throw new NullPointerException("archive file path");
	if (StringUtils.isEmpty(sMetaContent))
	    throw new NullPointerException("meta content");

	m_nId = nId;
	m_aAssetData = aAssetData;
	m_sArchiveFilePath = sArchiveFilePath;
	m_sMetaContent = sMetaContent;
	m_bIsMetaContent = bIsMetaContent;
    }

    public long getId() {
	return m_nId;
    }

    public DataHandler getAssetData() {
	return m_aAssetData;
    }

    public String getArchiveFilePath() {
	return m_sArchiveFilePath;
    }

    public String getMetaContent() {
	return m_sMetaContent;
    }

    public boolean isMetaContent() {
	return m_bIsMetaContent;
    }
}
