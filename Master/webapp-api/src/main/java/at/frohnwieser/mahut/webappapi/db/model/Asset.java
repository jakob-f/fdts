package at.frohnwieser.mahut.webappapi.db.model;

import java.io.File;
import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.commons.IHasId;
import at.frohnwieser.mahut.commons.IValidate;
import at.frohnwieser.mahut.commons.IdFactory;
import at.frohnwieser.mahut.commons.JSONFormatter;
import at.frohnwieser.mahut.commons.TimeStampFactory;
import at.frohnwieser.mahut.webappapi.util.Value;

@SuppressWarnings("serial")
public class Asset implements Serializable, IHasId, IValidate {
    private final long f_nId;
    private final long f_nTimeStamp;
    // TODO really needed? -> FileName
    private final String f_sFilePath;
    // TODO really needed?
    private final String f_sArchiveFilePath;
    private String m_sHash;
    private String m_sMetaContent;
    private long m_nOwnerId;
    private String m_sState;
    private boolean m_bMetaContent;

    private Asset(final long nId, final long nTimeStamp, @Nonnull final String sFilePath, @Nonnull final String sArchiveFilePath,
	    @Nullable final String sMetaContent, final long nOwnerId, final boolean bMetaContent) {
	if (StringUtils.isEmpty(sFilePath))
	    throw new NullPointerException("file");

	f_nId = nId;
	f_nTimeStamp = nTimeStamp;
	f_sFilePath = sFilePath;
	f_sArchiveFilePath = sArchiveFilePath;
	resetHash();
	m_sMetaContent = sMetaContent;
	m_nOwnerId = nOwnerId;
	m_sState = EState.PRIVATE.name();
	m_bMetaContent = bMetaContent;
    }

    public Asset(final long nId, @Nonnull final String sFilePath, @Nonnull final String sArchiveFilePath, @Nullable final String sMetaContent,
	    final long nOwnerId, final boolean bMetaContent) {
	this(nId, TimeStampFactory.nowMillis(), sFilePath, sArchiveFilePath, sMetaContent, nOwnerId, bMetaContent);
    }

    public Asset(@Nonnull final String sFilePath, @Nonnull final String sArchiveFilePath, final long nOwnerId) {
	this(IdFactory.getInstance().getId(), TimeStampFactory.nowMillis(), sFilePath, sArchiveFilePath, null, nOwnerId, false);
    }

    @Override
    public long getId() {
	return f_nId;
    }

    @Nonnull
    public String getName() {
	return FilenameUtils.getName(f_sFilePath);
    }

    @Nonnull
    public long getTimeStamp() {
	return f_nTimeStamp;
    }

    @Nonnull
    public String getTimeStampFormatted() {
	return TimeStampFactory.format(getTimeStamp());
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
    public File getThumbnailFile() {
	String sFullPath = FilenameUtils.getFullPath(f_sFilePath);

	if (m_bMetaContent)
	    sFullPath = sFullPath.replaceAll(Value.SET_FOLDER_META_CONTENT, "");

	return new File(sFullPath + File.separator + Value.SET_FOLDER_THUMBNAILS + File.separator + FilenameUtils.getBaseName(f_sFilePath) + "."
	        + Value.FILETYPE_THUMBNAIL);
    }

    @Nonnull
    public String getHash() {
	return m_sHash;
    }

    public Asset resetHash() {
	m_sHash = IdFactory.getInstance().getHash();

	return this;
    }

    @Nullable
    public String getMetaContent() {
	return m_sMetaContent;
    }

    @Nonnull
    public String getMetaContentFormatted() {
	return JSONFormatter.format(m_sMetaContent);
    }

    public void setMetaContent(@Nullable final String sMetaContent) {
	m_sMetaContent = sMetaContent;
    }

    public long getOwnerId() {
	return m_nOwnerId;
    }

    public void setOwnerId(final long nOwnerId) {
	m_nOwnerId = nOwnerId;
    }

    @Nullable
    public EState getState() {
	if (StringUtils.isNotEmpty(m_sState))
	    return EState.valueOf(m_sState);
	else
	    return null;
    }

    public Asset setState(@Nonnull final EState aState) {
	m_sState = aState == EState.MAIN_PAGE ? getFileType() != EFileType.IMAGE ? EState.PUBLISHED.name() : aState.name() : aState.name();

	return this;
    }

    public Asset setMetaContent(final boolean bMetaContent) {
	m_bMetaContent = bMetaContent;

	return this;
    }

    public boolean isMetaContent() {
	return m_bMetaContent;
    }

    @Override
    public boolean isValid() {
	// TODO
	return true;
    }

    @Nonnull
    public EFileType getFileType() {
	return EFileType.getFileTypeFromName(f_sFilePath);
    }

    @Nonnull
    public String getStreamURL() {
	return "asset/" + m_sHash; // TODO
    }

    public String getLink() {
	return "./view?a=" + m_sHash; // TODO
    }

    @Nonnull
    public String getThumbnailStreamURL() {
	return getStreamURL() + "&thumb"; // TODO
    }
}
