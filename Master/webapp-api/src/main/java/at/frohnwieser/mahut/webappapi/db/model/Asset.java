package at.frohnwieser.mahut.webappapi.db.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("serial")
public class Asset extends AbstractResource {
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
