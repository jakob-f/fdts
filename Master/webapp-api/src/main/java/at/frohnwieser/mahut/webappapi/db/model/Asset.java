package at.frohnwieser.mahut.webappapi.db.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.frohnwieser.mahut.commons.JSONFormatter;

@SuppressWarnings("serial")
public class Asset extends AbstractResource {
    public final static String REQUEST_PARAMETER = "a";
    public final static String REQUEST_PARAMETER_THUMBNAIL = "thumb";

    private final boolean f_bMetaContent;

    public Asset(@Nonnull final String sOwnerId, @Nonnull final String sName, @Nullable final String sMetaContent, final boolean bMetaContent) {
	super(sOwnerId, sName, sMetaContent);
	f_bMetaContent = bMetaContent;
    }

    public boolean isMetaContent() {
	return f_bMetaContent;
    }

    @Override
    @Nonnull
    public EFileType getFileType() {
	return EFileType.getFileTypeFromName(getName());
    }

    @Nonnull
    public String getStreamPath() {
	return "asset/" + getHash(); // TODO
    }

    @Nonnull
    public String getThumbnailStreamPath() {
	final String s = getStreamPath() + "&" + REQUEST_PARAMETER_THUMBNAIL; // TODO
	return s;
    }

    // TODO
    @Override
    @Nonnull
    public String getMetaContentFormatted() {
	return JSONFormatter.format(getMetaContent());
    }
}
