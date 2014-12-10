package at.ac.tuwien.media.master.webappapi.db.model;

import java.io.File;
import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.commons.IHasId;
import at.ac.tuwien.media.master.commons.IHasMetaContent;
import at.ac.tuwien.media.master.commons.IHasTimeStamp;
import at.ac.tuwien.media.master.commons.IValidate;
import at.ac.tuwien.media.master.commons.IdFactory;
import at.ac.tuwien.media.master.commons.TimeStampFactory;
import at.ac.tuwien.media.master.webappapi.util.Value;

@SuppressWarnings("serial")
public class Asset implements Serializable, IHasId, IValidate, IHasTimeStamp, IHasMetaContent {
    private final long f_nId;
    private final String f_sTimeStamp;
    private final String f_sFileType;
    private final String f_sFilePath;
    private final String f_sArchiveFilePath;
    private String m_sHash;
    // TODO save this as json with "userdescription" : "xxxx"
    private String m_sMetaContent;
    private boolean m_bMetadata;
    private boolean m_bPublic;
    private boolean m_bPublish;
    private boolean m_bShowOnMainPage;

    private Asset(final long nId, @Nonnull final String sTimeStamp, @Nonnull final String sFilePath, @Nonnull final String sArchiveFilePath,
	    @Nullable final String sMetaContent, final boolean bMetadata) {
	if (StringUtils.isEmpty(sFilePath))
	    throw new NullPointerException("file");

	f_nId = nId;
	f_sTimeStamp = sTimeStamp;
	f_sFileType = EFileType.getFileTypeFromName(sFilePath).name();
	f_sFilePath = sFilePath;
	f_sArchiveFilePath = sArchiveFilePath;
	resetHash();
	m_sMetaContent = sMetaContent;
	m_bMetadata = bMetadata;
	m_bPublic = false;
	m_bPublish = false;
	m_bShowOnMainPage = false;
    }

    public Asset(final long nId, @Nonnull final String sFilePath, @Nonnull final String sArchiveFilePath, @Nullable final String sMetaContent,
	    final boolean bMetadata) {
	this(nId, TimeStampFactory.getAsString(), sFilePath, sArchiveFilePath, sMetaContent, bMetadata);
    }

    public Asset(@Nonnull final String sFilePath, @Nonnull final String sArchiveFilePath) {
	this(IdFactory.getInstance().getId(), TimeStampFactory.getAsString(), sFilePath, sArchiveFilePath, "", false);
    }

    public Asset() {
	this("", "");
    }

    @Override
    public long getId() {
	return f_nId;
    }

    @Nonnull
    public String getName() {
	return FilenameUtils.getName(f_sFilePath);
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
    public File getThumbnailFile() {
	String sFullPath = FilenameUtils.getFullPath(f_sFilePath);

	if (m_bMetadata)
	    sFullPath = sFullPath.replaceAll(Value.SET_FOLDER_META_CONTENT, "");

	return new File(sFullPath + File.separator + Value.SET_FOLDER_THUMBNAILS + File.separator + FilenameUtils.getBaseName(f_sFilePath) + "."
	        + Value.FILETYPE_THUMBNAIL);
    }

    @Nonnull
    public String getThumbnailStreamURL() {
	return getStreamURL() + "&thumb"; // TODO
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

    public Asset setMetadata(final boolean bMetadata) {
	m_bMetadata = bMetadata;

	return this;
    }

    public boolean isMetadata() {
	return m_bMetadata;
    }

    public Asset setPublic2(final boolean bPublic) {
	m_bPublic = bPublic;

	return this;
    }

    public boolean isPublic2() {
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

    @Override
    public boolean isValid() {
	// TODO
	return true;
    }
}
