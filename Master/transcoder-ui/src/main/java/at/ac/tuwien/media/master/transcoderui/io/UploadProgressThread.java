package at.ac.tuwien.media.master.transcoderui.io;

import javax.annotation.Nonnull;

import at.ac.tuwien.media.master.commons.IOnCompleteNotifyListener;
import at.ac.tuwien.media.master.webapp.FailedLoginException_Exception;
import at.ac.tuwien.media.master.webapp.ProjectData;
import at.ac.tuwien.media.master.wsclient.WSClient;

public class UploadProgressThread extends AbstractNotifierThread implements IOnCompleteNotifyListener {
    private final ProjectData f_aData;

    public UploadProgressThread(@Nonnull final ProjectData aData) {
	if (aData == null)
	    throw new NullPointerException("ws data");

	f_aData = aData;
    }

    @Override
    public void run() {
	if (WSClient.getInstance().isReady()) {
	    _setCallbackValues(0.5, "uploading");
	    try {
		WSClient.getInstance().upload(f_aData);
	    } catch (final FailedLoginException_Exception aFailedLoginException) {
		aFailedLoginException.printStackTrace();
	    }
	}
    }

    @Override
    public void onThreadComplete(final Thread aThread) {
	run();
    }
}
