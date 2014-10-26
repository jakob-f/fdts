package at.ac.tuwien.media.master.transcoderui.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.ac.tuwien.media.master.commons.IOnCompleteNotifyListener;
import at.ac.tuwien.media.master.commons.ISetProgress;
import at.ac.tuwien.media.master.commons.ISetText;

public abstract class AbstractNotifierThread extends Thread {
    private final List<Object> m_aCallbackObjectList;

    protected AbstractNotifierThread() {
	m_aCallbackObjectList = new ArrayList<Object>();
    }

    public void addCallback(@Nonnull final Object... aCallbacks) {
	m_aCallbackObjectList.addAll(Arrays.asList(aCallbacks));
    }

    protected void _setCallbackValues(@Nonnegative final double nProgress, @Nullable final String sText1, @Nullable final String sText2) {
	for (final Object aObject : m_aCallbackObjectList) {
	    // set progress
	    if (aObject instanceof ISetProgress)
		((ISetProgress) aObject).setProgress(nProgress);

	    // set Text
	    if (aObject instanceof ISetText)
		((ISetText) aObject).setText(sText1, sText2);
	}
    }

    protected void _notifyListener(@Nonnull final Thread aThread) {
	for (final Object aObject : m_aCallbackObjectList)
	    if (aObject instanceof IOnCompleteNotifyListener)
		((IOnCompleteNotifyListener) aObject).onThreadComplete(aThread);
    }
}
