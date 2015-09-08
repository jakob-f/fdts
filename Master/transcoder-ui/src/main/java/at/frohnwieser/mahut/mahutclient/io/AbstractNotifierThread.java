package at.frohnwieser.mahut.mahutclient.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;

import at.frohnwieser.mahut.commons.ICallback;
import at.frohnwieser.mahut.commons.IOnCompleteCallback;
import at.frohnwieser.mahut.commons.IOnCompleteFileCallback;
import at.frohnwieser.mahut.commons.ISetProgress;
import at.frohnwieser.mahut.commons.ISetTextText;

public abstract class AbstractNotifierThread extends Thread {
    private static final String CLOSE_QUEUE_TOKEN = "close";
    private final Collection<ICallback> f_aCallbacks;
    private BlockingQueue<Object> m_aQueue;
    private final Collection<File> f_aInFiles;
    private final File f_aOutDirectory;
    protected boolean m_bTerminate;

    public AbstractNotifierThread() {
	f_aCallbacks = new ArrayList<ICallback>();
	f_aInFiles = null;
	f_aOutDirectory = null;
	m_bTerminate = false;
    }

    public AbstractNotifierThread(@Nonnull final Collection<File> aInFiles, @Nonnull final File aOutDirectory) {
	if (CollectionUtils.isEmpty(aInFiles))
	    throw new NullPointerException("in file");
	if (aOutDirectory == null)
	    throw new NullPointerException("out file");

	f_aCallbacks = new ArrayList<ICallback>();
	f_aInFiles = aInFiles;
	f_aOutDirectory = aOutDirectory;
	m_bTerminate = false;
    }

    public void addCallback(@Nonnull final ICallback aCallback) {
	if (aCallback != null)
	    f_aCallbacks.add(aCallback);
    }

    public void setQueue(@Nonnull final BlockingQueue<Object> aQueue) {
	m_aQueue = aQueue;
    }

    protected void _setCallbackValues(@Nonnegative final double nProgress, @Nullable final String sText1, @Nullable final String sText2) {
	f_aCallbacks.forEach(aObject -> {
	    // set progress
		if (aObject instanceof ISetProgress)
		    ((ISetProgress) aObject).setProgress(nProgress);

		// set Text
		if (aObject instanceof ISetTextText)
		    ((ISetTextText) aObject).setText(sText1, sText2);
	    });
    }

    protected void _processFile(@Nonnull final File aInFile, @Nonnull final File aOutDirectory) {
	// this method intentionally left blank
    }

    protected void _processQueueObject(@Nonnull final Object aObject) {
	// this method intentionally left blank
    }

    private void _notifyOnCompleteFile() {
	f_aCallbacks.stream().filter(aObject -> aObject instanceof IOnCompleteFileCallback)
	        .forEach(aObject -> ((IOnCompleteFileCallback) aObject).onCompleteFile());
    }

    private void _notifyOnComplete() {
	f_aCallbacks.stream().filter(aObject -> aObject instanceof IOnCompleteCallback).forEach(aObject -> ((IOnCompleteCallback) aObject).onComplete());
    }

    protected void _putInQueue(@Nonnull final Object aObject) throws InterruptedException {
	if (m_aQueue != null && aObject != null)
	    m_aQueue.put(aObject);
    }

    protected void _closeQueue() throws InterruptedException {
	_putInQueue(CLOSE_QUEUE_TOKEN);
    }

    private Object _takeFromQueue() throws InterruptedException {
	return m_aQueue != null ? m_aQueue.take() : null;
    }

    private void _processQueue() {
	try {
	    Object aObject = null;

	    while ((aObject = _takeFromQueue()) != null && !m_bTerminate) {
		if (aObject instanceof String && aObject.equals(CLOSE_QUEUE_TOKEN))
		    break;

		_processQueueObject(aObject);
		_notifyOnCompleteFile();
	    }
	} catch (final InterruptedException aIException) {
	    throw new RuntimeException(aIException);
	}
    }

    @Override
    public void run() {
	if (f_aInFiles != null && f_aOutDirectory != null && !m_bTerminate)
	    f_aInFiles.forEach(aInFile -> {
		if (!m_bTerminate) {
		    _processFile(aInFile, f_aOutDirectory);
		    _notifyOnCompleteFile();
		}
	    });
	else if (m_aQueue != null && !m_bTerminate)
	    _processQueue();

	_notifyOnComplete();
    }

    public void terminate() {
	m_bTerminate = true;
    }
}
