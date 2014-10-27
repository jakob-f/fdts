package at.ac.tuwien.media.master.transcoderui.io;

import java.io.File;
import java.util.Collection;

import javax.annotation.Nonnull;

import at.ac.tuwien.media.master.commons.IOnCompleteNotifyListener;
import at.ac.tuwien.media.master.webapp.FailedLoginException_Exception;
import at.ac.tuwien.media.master.wsclient.WSClient;

public class UploadProgressThread extends AbstractNotifierThread implements IOnCompleteNotifyListener {

    public UploadProgressThread(@Nonnull final Collection<File> aInFiles, @Nonnull final File aOutDirectory) {
	super(aInFiles, aOutDirectory);
    }

    @Override
    public void onThreadComplete(final Thread aThread) {
	run();
    }

    @Override
    protected void _process(final File aInFile, final File aOutDirectory) {
	if (!WSClient.getInstance().isCreated()) {
	    _setCallbackValues(0.5, "", "uploading");
	    try {
		WSClient.getInstance().upload(null);
	    } catch (final FailedLoginException_Exception aFailedLoginException) {
		aFailedLoginException.printStackTrace();
	    }
	}
    }
}
