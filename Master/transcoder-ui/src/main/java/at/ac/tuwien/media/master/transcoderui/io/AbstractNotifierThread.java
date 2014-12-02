package at.ac.tuwien.media.master.transcoderui.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;

import at.ac.tuwien.media.master.commons.IOnCompleteCallback;
import at.ac.tuwien.media.master.commons.ISetProgress;
import at.ac.tuwien.media.master.commons.ISetTextText;

public abstract class AbstractNotifierThread extends Thread {
    private final Collection<Object> f_aCallbackObjects;
    private BlockingQueue<Object> m_aQueue;
    private final Collection<File> f_aInFiles;
    private final File f_aOutDirectory;
    protected boolean m_bTerminate;

    public AbstractNotifierThread(@Nonnull final Collection<File> aInFiles) {
	f_aCallbackObjects = new ArrayList<Object>();
	f_aInFiles = aInFiles;
	f_aOutDirectory = null;
	m_bTerminate = false;
    }

    public AbstractNotifierThread(@Nonnull final Collection<File> aInFiles, @Nonnull final File aOutDirectory) {
	if (CollectionUtils.isEmpty(aInFiles))
	    throw new NullPointerException("in file");
	if (aOutDirectory == null)
	    throw new NullPointerException("out file");

	f_aCallbackObjects = new ArrayList<Object>();
	f_aInFiles = aInFiles;
	f_aOutDirectory = aOutDirectory;
	m_bTerminate = false;
    }

    public void addCallback(@Nonnull final Object aCallback) {
	if (aCallback != null)
	    f_aCallbackObjects.add(aCallback);
    }

    public void setQueue(@Nonnull final BlockingQueue<Object> aQueue) {
	m_aQueue = aQueue;
    }

    protected void _setCallbackValues(@Nonnegative final double nProgress, @Nullable final String sText1, @Nullable final String sText2) {
	f_aCallbackObjects.forEach(aObject -> {
	    // set progress
		if (aObject instanceof ISetProgress)
		    ((ISetProgress) aObject).setProgress(nProgress);

		// set Text
		if (aObject instanceof ISetTextText)
		    ((ISetTextText) aObject).setText(sText1, sText2);
	    });
    }

    protected void _notifyOnComplete(@Nonnull final Thread aThread) {
	f_aCallbackObjects.stream().filter(aObject -> aObject instanceof IOnCompleteCallback).forEach(aObject -> ((IOnCompleteCallback) aObject).onComplete());
    }

    protected void _putInQueue(@Nonnull final Object aObject) throws InterruptedException {
	if (m_aQueue != null && aObject != null)
	    m_aQueue.put(aObject);
    }

    protected Object _takeFromQueue() throws InterruptedException {
	return m_aQueue != null ? m_aQueue.take() : null;
    }

    protected void _process(@Nonnull final File aInFile, @Nonnull final File aOutDirectory) {
	// this method intentionally left blank
    }

    protected void _process(@Nonnull final File aInFile) {
	// this method intentionally left blank
    }

    protected void _processQueue() {
	// this method intentionally left blank
    }

    @Override
    public void run() {
	if (f_aInFiles != null)
	    f_aInFiles.forEach(aInFile -> {
		if (!m_bTerminate)
		    if (f_aOutDirectory != null)
			_process(aInFile, f_aOutDirectory);
		    else
			_process(aInFile);
	    });

	if (m_aQueue != null)
	    _processQueue();
    }

    public void terminate() {
	m_bTerminate = true;
    }
}
