package at.ac.tuwien.media.master.webappapi.db.model;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.util.IdFactory;
import at.ac.tuwien.media.master.webappapi.util.Value;

@SuppressWarnings("serial")
public class Asset implements Serializable {
    public enum EFileType {
	IMAGE("([^\\s]+(\\.(?i)(bmp|gif|jpg|png))$)"),
	PDF("([^\\s]+(\\.(?i)(pdf))$)"),
	VIDEO("([^\\s]+(\\.(?i)(mp4|ogv|webm))$)"),
	OTHER("([^\\s]+(\\.(?i)([a-x]{3}))$)");

	private final String f_sRegexFileExtension;

	private EFileType(@Nonnull final String sRegexFileExtension) {
	    f_sRegexFileExtension = sRegexFileExtension;
	}

	@Nonnull
	public static EFileType getFileTypeFromName(@Nonnull final String sFileName) {
	    for (final EFileType aFileType : EFileType.values())
		if (sFileName.matches(aFileType.f_sRegexFileExtension))
		    return aFileType;

	    return EFileType.OTHER;
	}
    }

    private final long m_nId;
    private final String m_sFilePath;
    private final String m_sArchiveFilePath;
    private String m_sHash;
    private final String m_sTimeStamp;
    private final EFileType m_aFileType;
    private boolean m_bPublish;
    private boolean m_bMetadata;
    private boolean m_bShowOnMainPage;
    private String m_sMetadata;
    private String m_sDescription;

    // TODO extract
    public Asset resetHash() {
	final String sUUID = UUID.randomUUID().toString();

	m_sHash = Base64.encodeBase64String(sUUID.getBytes());

	return this;
    }

    public Asset(@Nonnull final String sFilePath, @Nonnull final String sArchiveFilePath) {
	if (StringUtils.isEmpty(sFilePath))
	    throw new NullPointerException("file");

	resetHash();

	m_nId = IdFactory.getInstance().getNextId();
	m_sFilePath = sFilePath;
	m_sArchiveFilePath = sArchiveFilePath;
	// TODO better version?
	m_sTimeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(Value.DATE_PATTERN));
	m_aFileType = EFileType.getFileTypeFromName(sFilePath);
	m_bPublish = false;
	m_bMetadata = false;
	m_bShowOnMainPage = false;

	m_bMarkedForDeletion = false;
    }

    public long getId() {
	return m_nId;
    }

    @Nonnull
    public File getFile() {
	return new File(m_sFilePath);
    }

    @Nonnull
    public String getFileSize() {
	return FileUtils.byteCountToDisplaySize(getFile().length());
    }

    @Nonnull
    public String getArchiveFilePath() {
	return m_sArchiveFilePath;
    }

    @Nonnull
    public String getStreamPath() {
	return "asset/" + m_sHash;
    }

    @Nonnull
    public String getViewPath() {
	return "view?a=" + m_sHash;
    }

    @Nonnull
    public String getHash() {
	return m_sHash;
    }

    @Nonnull
    public String getTimeStamp() {
	return m_sTimeStamp;
    }

    @Nonnull
    public EFileType getFileType() {
	return m_aFileType;
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
	if (m_aFileType == EFileType.IMAGE) {
	    m_bShowOnMainPage = bShowOnMainPage;
	    m_bPublish = m_bShowOnMainPage;

	    return this;
	}

	return null;
    }

    public boolean isShowOnMainPage() {
	return m_bShowOnMainPage;
    }

    // XXX
    private boolean m_bMarkedForDeletion;

    public Asset setMarkedForDeletion(final boolean bMarkedForDeletion) {
	m_bMarkedForDeletion = bMarkedForDeletion;

	return this;
    }

    public boolean isMarkedForDeletion() {
	return m_bMarkedForDeletion;
    }
}
