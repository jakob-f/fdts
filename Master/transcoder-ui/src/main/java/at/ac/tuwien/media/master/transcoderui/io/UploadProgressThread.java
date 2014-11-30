package at.ac.tuwien.media.master.transcoderui.io;

import at.ac.tuwien.media.master.wsclient.WSClient;

public class UploadProgressThread extends AbstractNotifierThread {

    public UploadProgressThread() {
	super();
    }

    @Override
    public void _process() {
	try {
	    Object aObject = null;

	    // TODO
	    while (!(aObject = _takeFromQueue()).equals("xxx") && !m_bTerminate) {
		if (!WSClient.getInstance().isCreated()) {
		    _setCallbackValues(0.5, aObject.toString(), "xxx");

		    // WSClient.getInstance().upload(-1L, null);
		}
	    }

	    // notify listener
	    _notifyOnComplete(this);
	} catch (final Exception aException) {
	    aException.printStackTrace();
	}
    }
}
