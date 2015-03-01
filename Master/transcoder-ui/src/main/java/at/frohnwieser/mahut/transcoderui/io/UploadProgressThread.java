package at.frohnwieser.mahut.transcoderui.io;

import javax.annotation.Nonnull;

import at.frohnwieser.mahut.webapp.AssetData;
import at.frohnwieser.mahut.wsclient.WSClient;

public class UploadProgressThread extends AbstractNotifierThread {
    private final long f_nSetId;

    public UploadProgressThread(final long nSetId) {
	f_nSetId = nSetId;
    }

    @Override
    protected void _processQueueObject(@Nonnull final Object aObject) {
	// upload assets
	try {
	    if (WSClient.getInstance().isCreated()) {
		if (aObject != null && aObject instanceof AssetData) {
		    final AssetData aAssetData = (AssetData) aObject;

		    _setCallbackValues(-1, aAssetData.getName(), "");

		    if (WSClient.getInstance().uploadAsset(f_nSetId, aAssetData))
			_setCallbackValues(1.0, aAssetData.getName(), "");
		    else {
			_setCallbackValues(0.0, "cannot upload asset '" + aAssetData.getName() + "'", "");
			// TODO warning
			System.out.println("cannot upload asset '" + aAssetData.getName() + "'");
		    }
		}

		sleep(10);
	    } else
		throw new IllegalStateException("ws end point not yet ready");
	} catch (final Exception aException) {
	    throw new RuntimeException(aException);
	}
    }
}
