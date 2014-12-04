package at.ac.tuwien.media.master.transcoderui.io;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;

import javax.activation.DataHandler;
import javax.annotation.Nonnull;

import at.ac.tuwien.media.master.commons.IdFactory;
import at.ac.tuwien.media.master.webapp.AssetData;
import at.ac.tuwien.media.master.webapp.FailedLoginException_Exception;
import at.ac.tuwien.media.master.wsclient.WSClient;

public class UploadProgressThread extends AbstractNotifierThread {
    private final long f_nSetId;

    public UploadProgressThread(final long nSetId, @Nonnull final Collection<File> aInFiles) {
	super(aInFiles);

	f_nSetId = nSetId;
    }

    private boolean _uploadAssetData(@Nonnull final AssetData aAssetData) throws FailedLoginException_Exception {
	if (aAssetData != null && WSClient.getInstance().isCreated()) {
	    _setCallbackValues(-1, aAssetData.getName(), "");

	    if (WSClient.getInstance().uploadAsset(f_nSetId, aAssetData)) {
		_setCallbackValues(1, aAssetData.getName(), "");

		return true;
	    }
	}

	return false;
    }

    private AssetData _createAssetData(@Nonnull final File aInFile, final boolean bIsMetaContent) throws MalformedURLException {
	final AssetData aAssetData = new AssetData();
	aAssetData.setId(IdFactory.getInstance().getId());
	aAssetData.setName(aInFile.getName());
	aAssetData.setAssetData(new DataHandler(aInFile.toURI().toURL()));
	aAssetData.setArchiveFilePath("");
	aAssetData.setMetaContent("");
	aAssetData.setIsMetaContent(bIsMetaContent);

	return aAssetData;
    }

    @Override
    public void _process(@Nonnull final File aInFile) {
	try {
	    // upload meta content files
	    _uploadAssetData(_createAssetData(aInFile, true));
	} catch (final Exception aException) {
	    // TODO
	    aException.printStackTrace();
	}
    }

    @Override
    protected void _processQueue() {
	try {
	    Object aObject = null;

	    // upload assets
	    while (((aObject = _takeFromQueue()) instanceof File) && !m_bTerminate)
		_uploadAssetData(_createAssetData((File) aObject, false));

	    // notify listener
	    _setCallbackValues(1, "", "");
	    _notifyOnComplete(this);
	} catch (final Exception aException) {
	    // TODO
	    aException.printStackTrace();
	}

    }
}
