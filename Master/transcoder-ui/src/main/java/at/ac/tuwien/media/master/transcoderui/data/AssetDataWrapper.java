package at.ac.tuwien.media.master.transcoderui.data;

import java.io.File;
import java.net.MalformedURLException;

import javax.activation.DataHandler;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.ac.tuwien.media.master.commons.IdFactory;
import at.ac.tuwien.media.master.webapp.AssetData;

public class AssetDataWrapper extends AssetData {

    public AssetDataWrapper(@Nonnull final File aFile, @Nullable final String sMetaContent, final boolean bIsMetaContent) throws MalformedURLException {
	if (aFile == null)
	    throw new NullPointerException("file");

	super.setId(IdFactory.getInstance().getId());
	super.setName(aFile.getName());
	super.setAssetData(new DataHandler(aFile.toURI().toURL()));
	super.setArchiveFilePath("");
	super.setMetaContent(sMetaContent);
	super.setIsMetaContent(bIsMetaContent);
    }
}
