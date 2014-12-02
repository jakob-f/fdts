package at.ac.tuwien.media.master.webappapi.db.model;

import java.io.File;
import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.commons.IHasId;
import at.ac.tuwien.media.master.commons.IHasMetaContent;
import at.ac.tuwien.media.master.commons.IHasTimeStamp;
import at.ac.tuwien.media.master.commons.IdFactory;
import at.ac.tuwien.media.master.commons.TimeStampFactory;

@SuppressWarnings("serial")
public class Asset implements Serializable, IHasId, IHasTimeStamp, IHasMetaContent {
    private final long f_nId;
    private final String f_sTimeStamp;
    private final String f_sFileType;
    private final String f_sFilePath;
    private final String f_sArchiveFilePath;
    private String m_sHash;
    // TODO save this as json with "userdescription" : "xxxx"
    private String m_sMetaContent;
    private boolean m_bPublic;
    private boolean m_bPublish;
    private boolean m_bMetadata;
    private boolean m_bShowOnMainPage;

    public Asset(@Nonnull final String sFilePath, @Nonnull final String sArchiveFilePath) {
	if (StringUtils.isEmpty(sFilePath))
	    throw new NullPointerException("file");

	f_nId = IdFactory.getInstance().getId();
	f_sTimeStamp = TimeStampFactory.getAsString();
	f_sFileType = EFileType.getFileTypeFromName(sFilePath).name();
	f_sFilePath = sFilePath;
	f_sArchiveFilePath = sArchiveFilePath;
	resetHash();
	m_sMetaContent = "";
	m_bPublic = false;
	m_bPublish = false;
	m_bMetadata = false;
	m_bShowOnMainPage = false;
    }

    @Override
    public long getId() {
	return f_nId;
    }

    @Override
    @Nonnull
    public String getTimeStamp() {
	return f_sTimeStamp;
    }

    @Nonnull
    public EFileType getFileType() {
	return EFileType.valueOf(f_sFileType);
    }

    @Nonnull
    public File getFile() {
	return new File(f_sFilePath);
    }

    @Nonnull
    public String getFileSize() {
	return FileUtils.byteCountToDisplaySize(getFile().length());
    }

    @Nonnull
    public String getArchiveFilePath() {
	return f_sArchiveFilePath;
    }

    @Nonnull
    public String getStreamURL() {
	return "asset/" + m_sHash; // TODO
    }

    @Nonnull
    public String getViewPath() {
	return "view?a=" + m_sHash; // TODO
    }

    @Nonnull
    public String getHash() {
	return m_sHash;
    }

    public Asset resetHash() {
	m_sHash = IdFactory.getInstance().getHash();

	return this;
    }

    @Override
    @Nullable
    public String getMetaContent() {
	return m_sMetaContent;
    }

    @Override
    public void setMetaContent(@Nullable final String sMetaContent) {
	m_sMetaContent = sMetaContent;
    }

    public Asset setPublic(final boolean bPublic) {
	m_bPublic = bPublic;

	return this;
    }

    public boolean isPublic() {
	return m_bPublic;
    }

    public Asset setPublish(final boolean bPublish) {
	m_bPublish = bPublish;
	m_bShowOnMainPage = !m_bPublish ? false : m_bShowOnMainPage;

	return this;
    }

    public boolean isPublish() {
	return m_bPublish;
    }

    public Asset setMetadata(final boolean bMetadata) {
	m_bMetadata = bMetadata;

	return this;
    }

    public boolean isMetadata() {
	return m_bMetadata;
    }

    public Asset setShowOnMainPage(final boolean bShowOnMainPage) {
	if (f_sFileType.equals(EFileType.IMAGE.name())) {
	    m_bShowOnMainPage = bShowOnMainPage;
	    m_bPublish = m_bShowOnMainPage;

	    return this;
	}

	return null;
    }

    public boolean isShowOnMainPage() {
	return m_bShowOnMainPage;
    }
}
