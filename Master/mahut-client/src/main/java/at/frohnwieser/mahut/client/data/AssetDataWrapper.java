package at.frohnwieser.mahut.client.data;

import java.io.File;
import java.net.MalformedURLException;

import javax.activation.DataHandler;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.frohnwieser.mahut.commons.IdFactory;
import at.frohnwieser.mahut.webapp.AssetData;

public class AssetDataWrapper extends AssetData {

    public AssetDataWrapper(@Nonnull final File aFile, @Nullable final String sMetaContent, final boolean bIsMetaContent) throws MalformedURLException {
	if (aFile == null)
	    throw new NullPointerException("file");

	super.setId(IdFactory.getInstance().getStringId());
	super.setName(aFile.getName());
	super.setMetaContent(sMetaContent);
	super.setIsMetaContent(bIsMetaContent);
	super.setAssetData(new DataHandler(aFile.toURI().toURL()));
    }
}
