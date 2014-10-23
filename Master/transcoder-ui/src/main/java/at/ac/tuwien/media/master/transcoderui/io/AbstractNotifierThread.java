package at.ac.tuwien.media.master.transcoderui.io;

import javafx.application.Platform;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.ac.tuwien.media.master.commons.IOnCompleteNotifyListener;
import at.ac.tuwien.media.master.commons.ISetProgress;
import at.ac.tuwien.media.master.commons.ISetText;

public abstract class AbstractNotifierThread extends Thread {
    private final ISetProgress m_aProgressIndicator;
    private final ISetText m_aProgressText;
    private final IOnCompleteNotifyListener m_aOnCompleteListener;

    protected AbstractNotifierThread(@Nullable final ISetProgress aProgressIndicator, @Nullable final ISetText aProgressText,
	    @Nullable final IOnCompleteNotifyListener aCompleteListener) {
	m_aProgressIndicator = aProgressIndicator;
	m_aProgressText = aProgressText;
	m_aOnCompleteListener = aCompleteListener;
    }

    private void _updateText(@Nullable final String sText) {
	Platform.runLater(() -> m_aProgressText.setText(sText));
    }

    protected void _setCallbackValues(@Nonnegative final double nProgress, @Nullable final String sText) {
	// set progress
	if (m_aProgressIndicator != null)
	    m_aProgressIndicator.setProgress(nProgress);

	// set Text
	if (m_aProgressText != null)
	    _updateText(sText);
    }

    protected void _notifyListener(@Nonnull final Thread aThread) {
	if (m_aOnCompleteListener != null)
	    m_aOnCompleteListener.onThreadComplete(this);
    }
}
