package at.ac.tuwien.media.master.webappapi.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;

public class Asset {
    public enum EFileType {
	IMAGE("([^\\s]+(\\.(?i)(bmp|gif|jpg|png))$)"),
	PDF("([^\\s]+(\\.(?i)pdf)$)"),
	VIDEO("([^\\s]+(\\.(?i)(ogg|webm))$)"),
	OTHER("([^\\s]+(\\.(?i)([a-x]{3})$)");

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

	public boolean isImage() {
	    return this.equals(IMAGE);
	}

	public boolean isPdf() {
	    return this.equals(PDF);
	}

	public boolean isVideo() {
	    return this.equals(VIDEO);
	}

	public boolean isOther() {
	    return this.equals(OTHER);
	}
    }

    private long m_nId;
    private File m_aFile;
    private String m_sHash;
    private DateTime m_aTimeStamp;
    private EFileType m_aFileType;
    private boolean m_bPublish;
    private boolean m_bMetadata;
    private boolean m_bShowOnMainPage;

    private String _generateHash(@Nonnull final File aFile) {
	FileInputStream aFis = null;
	try {
	    aFis = new FileInputStream(aFile);
	    return DigestUtils.md5Hex(aFis);
	} catch (final Exception aException) {
	    throw new RuntimeException(aException);
	} finally {
	    if (aFis != null)
		try {
		    aFis.close();
		} catch (final IOException e) {
		}
	}
    }

    private void _init(@Nullable final File aFile) {
	if (aFile == null || !aFile.isFile())
	    throw new NullPointerException("file");

	m_nId = IdFactory.getInstance().getNextId();
	m_aFile = aFile;
	m_sHash = _generateHash(m_aFile);
	m_aTimeStamp = new DateTime();
	m_aFileType = EFileType.getFileTypeFromName(m_aFile.getName());
	m_bPublish = false;
	m_bMetadata = false;
	m_bShowOnMainPage = false;

	System.out.println(m_sHash + " " + aFile.getAbsolutePath() + "  " + m_aFileType);
    }

    public Asset(@Nullable final File aFile) {
	_init(aFile);
    }

    public long getId() {
	return m_nId;
    }

    @Nonnull
    public File getFile() {
	return m_aFile;
    }

    @Nonnull
    public String getHash() {
	return m_sHash;
    }

    @Nonnull
    public DateTime getTimeStamp() {
	return m_aTimeStamp;
    }

    @Nonnull
    public EFileType getFileType() {
	return m_aFileType;
    }

    public Asset setPublish(final boolean bPublish) {
	m_bPublish = bPublish;

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
	m_bShowOnMainPage = bShowOnMainPage;

	return this;
    }

    public boolean isShowOnMainPage() {
	return m_bShowOnMainPage;
    }
}
