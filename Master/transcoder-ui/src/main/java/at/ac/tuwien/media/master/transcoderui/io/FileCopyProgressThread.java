package at.ac.tuwien.media.master.transcoderui.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Collection;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;

public class FileCopyProgressThread extends AbstractNotifierThread {
    private final Collection<File> f_aInFiles;
    private final File f_aOutDirectory;

    public FileCopyProgressThread(@Nonnull final Collection<File> aInFiles, @Nonnull final File aOutDirectory) {
	if (CollectionUtils.isEmpty(aInFiles))
	    throw new NullPointerException("in file");
	if (aOutDirectory == null)
	    throw new NullPointerException("out file");

	f_aInFiles = aInFiles;
	f_aOutDirectory = aOutDirectory;
    }

    @SuppressWarnings("resource")
    private void _copyFile(@Nonnull final File aInFile, @Nonnull final File aOutFile) {
	FileChannel aInChannel = null;
	FileChannel aOutChannel = null;
	try {
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
	    for (int i = 0; i < nChunkCount;) {
		aInChannel.transferTo(nBytesCopied, nChunkSize, aOutChannel);

		i++;
		nBytesCopied = i * nChunkSize;
		nBytesLeft -= nChunkSize;
		nProgress = nBytesCopied / (double) nInFileLenght;

		// set values
		_setCallbackValues(nProgress, aInFile.getName(), String.valueOf(nBytesLeft / 1000));
	    }

	    // copy bytes left
	    if (nBytesLeft > 0)
		aInChannel.transferTo(nBytesCopied, nBytesLeft, aOutChannel);

	    // set values
	    _setCallbackValues(100, aInFile.getName(), "0");
	} catch (final Exception aException) {
	    aException.printStackTrace();
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

    @Override
    public void run() {
	File aOutFile;
	for (final File aInFile : f_aInFiles) {
	    aOutFile = new File(f_aOutDirectory.getAbsolutePath() + File.separator + aInFile.getName());
	    _copyFile(aInFile, aOutFile);
	}

	// notify listener
	_notifyListener(this);
    }
}
