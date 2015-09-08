package at.frohnwieser.mahut.client.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Collection;

import javax.annotation.Nonnull;

public class FileCopyProgressThread extends AbstractNotifierThread {

    public FileCopyProgressThread(@Nonnull final Collection<File> aInFiles, @Nonnull final File aOutDirectory) {
	super(aInFiles, aOutDirectory);
    }

    @Override
    @SuppressWarnings("resource")
    protected void _processFile(@Nonnull final File aInFile, @Nonnull final File aOutDirectory) {
	FileChannel aInChannel = null;
	FileChannel aOutChannel = null;
	try {
	    if (!m_bTerminate) {
		final File aOutFile = new File(aOutDirectory.getAbsolutePath() + File.separator + aInFile.getName());
		aInChannel = new FileInputStream(aInFile).getChannel();
		aOutChannel = new FileOutputStream(aOutFile).getChannel();

		// calc chunk count (copy at least 1MB)
		final long nInFileLenght = aInFile.length();
		int nChunkCount = 1;
		while ((nInFileLenght / nChunkCount) > 1000000) {
		    if (nChunkCount >= 100)
			break;

		    nChunkCount++;
		}

		// copy file in chunk sizes
		final long nChunkSize = nInFileLenght / nChunkCount;
		long nBytesCopied = nChunkSize;
		long nBytesLeft = nInFileLenght;
		double nProgress;
		for (int i = 0; i < nChunkCount && !m_bTerminate;) {
		    aInChannel.transferTo(nBytesCopied, nChunkSize, aOutChannel);

		    i++;
		    nBytesCopied = i * nChunkSize;
		    nBytesLeft -= nChunkSize;
		    nProgress = nBytesCopied / (double) nInFileLenght;

		    // set values
		    _setCallbackValues(nProgress, aInFile.getName(), String.valueOf(nBytesLeft / 1000));
		}

		if (!m_bTerminate) {
		    // copy bytes left
		    if (nBytesLeft > 0)
			aInChannel.transferTo(nBytesCopied, nBytesLeft, aOutChannel);

		    // set values
		    _setCallbackValues(1, aInFile.getName(), "0");
		}
	    }
	} catch (final Exception aException) {
	    throw new RuntimeException(aException);
	} finally {
	    if (aInChannel != null)
		try {
		    aInChannel.close();
		} catch (final IOException aIOException) {
		}
	    if (aOutChannel != null)
		try {
		    aOutChannel.close();
		} catch (final IOException aIOException) {
		}
	}
    }
}
