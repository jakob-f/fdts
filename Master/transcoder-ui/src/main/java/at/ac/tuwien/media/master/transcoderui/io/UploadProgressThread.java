package at.ac.tuwien.media.master.transcoderui.io;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.ac.tuwien.media.master.commons.IOnCompleteNotifyListener;
import at.ac.tuwien.media.master.commons.ISetProgress;
import at.ac.tuwien.media.master.commons.ISetText;
import at.ac.tuwien.media.master.webapp.FailedLoginException_Exception;
import at.ac.tuwien.media.master.webapp.ProjectData;
import at.ac.tuwien.media.master.wsclient.WSClient;

public class UploadProgressThread extends AbstractNotifierThread implements IOnCompleteNotifyListener {
    private final ProjectData m_aData;

    public UploadProgressThread(@Nonnull final ProjectData aData, @Nullable final ISetProgress aProgressIndicator, @Nullable final ISetText aProgressText,
	    @Nullable final IOnCompleteNotifyListener aCompleteListener) {
	super(aProgressIndicator, aProgressText, aCompleteListener);

	if (aData == null)
	    throw new NullPointerException("ws data");

	m_aData = aData;
    }

    @Override
    public void run() {
	if (WSClient.isReady()) {
	    _setCallbackValues(0.5, "uploading");
	    try {
		WSClient.upload(m_aData);
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
