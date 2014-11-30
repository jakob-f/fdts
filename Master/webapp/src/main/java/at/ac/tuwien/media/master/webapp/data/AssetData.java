package at.ac.tuwien.media.master.webapp.data;

import java.io.File;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

public class AssetData {
    private final File f_aFile;
    private final String f_sArchiveFilePath;
    private final String f_sMetaContent;
    private final boolean f_bMetadata;

    public AssetData(@Nonnull final File aFile, @Nonnull final String sArchiveFilePath, @Nonnull final String sMetaContent, final boolean bMetadata) {
	if (aFile == null)
	    throw new NullPointerException("file");
	if (StringUtils.isEmpty(sArchiveFilePath))
	    throw new NullPointerException("archive file path");
	if (StringUtils.isEmpty(sMetaContent))
	    throw new NullPointerException("meta content");

	f_aFile = aFile;
	f_sArchiveFilePath = sArchiveFilePath;
	f_sMetaContent = sMetaContent;
	f_bMetadata = bMetadata;
    }

    public File getFile() {
	return f_aFile;
    }

    public String getArchiveFilePath() {
	return f_sArchiveFilePath;
    }

    public String getMetaContent() {
	return f_sMetaContent;
    }

    public boolean isMetadata() {
	return f_bMetadata;
    }
}
