package at.frohnwieser.mahut.webappapi.db.model;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import at.frohnwieser.mahut.commons.IdFactory;
import at.frohnwieser.mahut.commons.TimeStampFactory;
import at.frohnwieser.mahut.webappapi.util.Value;

@SuppressWarnings("serial")
public class Asset extends AbstractResource {
    private final boolean f_bMetaContent;

    private Asset(@Nonnull final String sId, final long nCreationTimeStamp, @Nonnull final String sOwnerId, @Nonnull final String sName,
	    @Nullable final String sMetaContent, final boolean bMetaContent) {
	super(sId, nCreationTimeStamp, sOwnerId, sName, sMetaContent);
	f_bMetaContent = bMetaContent;
    }

    public Asset(@Nonnull final String sId, @Nonnull final String sOwnerId, @Nonnull final String sName, @Nullable final String sMetaContent,
	    final boolean bMetaContent) {
	this(sId, TimeStampFactory.nowMillis(), sOwnerId, sName, sMetaContent, bMetaContent);
    }

    public Asset(@Nonnull final String sOwnerId, @Nonnull final String sName, @Nullable final String sMetaContent, final boolean bMetaContent) {
	this(IdFactory.getInstance().getStringId(), TimeStampFactory.nowMillis(), sOwnerId, sName, sMetaContent, bMetaContent);
    }

    public boolean isMetaContent() {
	return f_bMetaContent;
    }

    @Nonnull
    public EFileType getFileType() {
	return EFileType.getFileTypeFromName(getName());
    }

    @Nonnull
    public String getFilePath() {
	// TODO store as field?
	final String sFileExtension = FilenameUtils.getExtension(getName().toLowerCase());
	// TODO move to utily fct.
	final String sID = getId();
	final StringBuilder aSB = new StringBuilder(sID);
	aSB.insert(3, File.separatorChar);
	aSB.insert(6, File.separatorChar);
	aSB.append(File.separatorChar);
	aSB.append(sID + "." + sFileExtension);

	return aSB.toString();
    }

    @Nonnull
    public String getThumbnailFilePath() {
	// TODO move to utily fct.
	return Value.DATA_FOLDER_THUMBNAILS + File.separator + getFilePath();
    }

    @Nonnull
    public String getFileSize() {
	return FileUtils.byteCountToDisplaySize(new File(getFilePath()).length());
    }

    public String getLink() {
	return "./view?a=" + getHash(); // TODO
    }

    @Nonnull
    public String getStreamURL() {
	return "asset/" + getHash(); // TODO
    }

    @Nonnull
    public String getThumbnailStreamURL() {
	return getStreamURL() + "&thumb"; // TODO
    }
}
