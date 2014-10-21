package at.ac.tuwien.media.master.ffmpegwrapper;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.ffmpegwrapper.util.FFMPEGCall;
import at.ac.tuwien.media.master.ffmpegwrapper.util.FFMPEGUtils;

public final class FFMPEGWrapper {

    private FFMPEGWrapper() {
    }

    @Nullable
    public static Process transcode(@Nonnull final File aInFile, @Nonnull final File aOutFile) {
	try {
	    final List<String> aErrorList = new LinkedList<String>();

	    if (aInFile == null)
		aErrorList.add("input file missing");
	    if (aOutFile == null)
		aErrorList.add("output file missing");

	    if (aErrorList.isEmpty()) {
		if (!FFMPEGUtils.isFormatSupportedForDecoding(FilenameUtils.getExtension(aInFile.getName())))
		    aErrorList.add("input format not supported\n");
		if (!FFMPEGUtils.isFormatSupportedForEncoding(FilenameUtils.getExtension(aOutFile.getName())))
		    aErrorList.add("output format not supported\n");

		if (!aInFile.isFile())
		    aErrorList.add("input file does not exist");
		if (aOutFile.exists())
		    aOutFile.delete();

		if (aErrorList.size() != 0)
		    throw new IllegalArgumentException(StringUtils.join(aErrorList, "\n"));

		return FFMPEGCall.execute("-i", aInFile.getAbsolutePath(), aOutFile.getAbsolutePath());
	    }
	} catch (final IOException aIOException) {
	    aIOException.printStackTrace();
	}

	return null;
    }

    @Nullable
    public static Process transcode(@Nonnull final String sInputVideo, @Nonnull final String sOutputVideo) {
	final List<String> aErrorList = new LinkedList<String>();

	if (StringUtils.isEmpty(sInputVideo))
	    aErrorList.add("input filename missing");
	if (StringUtils.isEmpty(sOutputVideo))
	    aErrorList.add("output filename missing");

	if (aErrorList.isEmpty())
	    return transcode(new File(sInputVideo), new File(sOutputVideo));

	return null;
    }
}
