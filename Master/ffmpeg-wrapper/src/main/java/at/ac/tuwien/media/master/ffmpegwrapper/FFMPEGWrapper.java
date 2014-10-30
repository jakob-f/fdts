package at.ac.tuwien.media.master.ffmpegwrapper;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.ffmpegwrapper.util.FFMPEGCall;
import at.ac.tuwien.media.master.ffmpegwrapper.util.FFMPEGUtils;
import at.ac.tuwien.media.master.ffmpegwrapper.util.FFPROBECall;

public final class FFMPEGWrapper {

    private FFMPEGWrapper() {
    }

    @Nonnull
    public static JSONObject metadata(@Nonnull final File aInFile) {
	Scanner aScanner = null;

	try {
	    if (aInFile == null)
		throw new NullPointerException("input file");

	    // ffprobe -v quiet -print_format json -show_format -show_streams
	    final Process aProcess = FFPROBECall.execute("-v", "quiet", "-print_format", "json", "-show_format", "-show_streams", aInFile.getAbsolutePath());
	    aScanner = new Scanner(aProcess.getInputStream());

	    // parse output
	    final StringBuilder aSB = new StringBuilder(1024);
	    while (aScanner.hasNextLine())
		aSB.append(aScanner.nextLine().trim());

	    return new JSONParser(JSONParser.MODE_PERMISSIVE).parse(aSB.toString(), JSONObject.class);
	} catch (final ParseException | IOException aException) {
	    throw new RuntimeException(aException);
	} finally {
	    if (aScanner != null)
		aScanner.close();
	}
    }

    @Nonnull
    public static JSONObject metadata(@Nonnull final String sInputVideo) {
	if (StringUtils.isEmpty(sInputVideo))
	    throw new IllegalArgumentException("input video");

	return metadata(new File(sInputVideo));
    }

    @Nullable
    public static Process transcode(@Nonnull final File aInFile, @Nonnull final File aOutFile) {
	if (aInFile == null || !aInFile.isFile())
	    throw new NullPointerException("input file");
	if (aOutFile == null)
	    throw new NullPointerException("output file");
	if (aOutFile.exists())
	    aOutFile.delete();
	if (!FFMPEGUtils.isFormatSupportedForDecoding(FilenameUtils.getExtension(aInFile.getName())))
	    throw new IllegalArgumentException("input file format");
	if (!FFMPEGUtils.isFormatSupportedForEncoding(FilenameUtils.getExtension(aOutFile.getName())))
	    throw new IllegalArgumentException("output file format");

	try {
	    return FFMPEGCall.execute("-i", aInFile.getAbsolutePath(), aOutFile.getAbsolutePath());
	} catch (final IOException aIOException) {
	    throw new RuntimeException(aIOException);
	}
    }

    @Nullable
    public static Process transcode(@Nonnull final String sInputVideo, @Nonnull final String sOutputVideo) {
	if (StringUtils.isEmpty(sInputVideo))
	    throw new IllegalArgumentException("input video");
	if (StringUtils.isEmpty(sOutputVideo))
	    throw new IllegalArgumentException("output video");

	return transcode(new File(sInputVideo), new File(sOutputVideo));
    }
}
