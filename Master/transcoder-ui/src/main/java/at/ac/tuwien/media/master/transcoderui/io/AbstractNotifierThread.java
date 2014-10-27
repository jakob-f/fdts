package at.ac.tuwien.media.master.transcoderui.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;

import at.ac.tuwien.media.master.commons.IOnCompleteNotifyListener;
import at.ac.tuwien.media.master.commons.ISetProgress;
import at.ac.tuwien.media.master.commons.ISetText;

public abstract class AbstractNotifierThread extends Thread {
    private final List<Object> m_aCallbackObjectList;
    private final Collection<File> f_aInFiles;
    private final File f_aOutDirectory;

    public AbstractNotifierThread(@Nonnull final Collection<File> aInFiles, @Nonnull final File aOutDirectory) {
	if (CollectionUtils.isEmpty(aInFiles))
	    throw new NullPointerException("in file");
	if (aOutDirectory == null)
	    throw new NullPointerException("out file");

	m_aCallbackObjectList = new ArrayList<Object>();
	f_aInFiles = aInFiles;
	f_aOutDirectory = aOutDirectory;
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

    abstract protected void _process(@Nonnull final File aInFile, @Nonnull final File aOutDirectory);

    @Override
    public void run() {
	for (final File aInFile : f_aInFiles)
	    _process(aInFile, f_aOutDirectory);

	// on finish notify listener
	_notifyListener(this);
    }
}
