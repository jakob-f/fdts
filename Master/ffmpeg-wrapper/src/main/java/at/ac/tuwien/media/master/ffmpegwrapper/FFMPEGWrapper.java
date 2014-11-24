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
    public enum EQuality {
	P240("scale=-1:480",
	        "400k"),
	P360("scale=-1:360",
	        "750k"),
	P480("scale=-1:480",
	        "1000k"),
	P720("scale=-1:720",
	        "2500k"),
	P1080("scale=-1:1080",
	        "4500k");

	private final String f_sScale;
	private final String f_sBitrate;

	private EQuality(@Nonnull final String sScale, @Nonnull final String sBitrate) {
	    f_sScale = sScale;
	    f_sBitrate = sBitrate;
	}

	public String getScale() {
	    return f_sScale;
	}

	public String getBitrate() {
	    return f_sBitrate;
	}
    }

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
    public static Process transcode(@Nonnull final File aInFile, @Nonnull final File aOutFile, final @Nonnull EQuality aQuality) {
	if (aInFile == null || !aInFile.isFile())
	    throw new NullPointerException("input file");
	if (aOutFile == null)
	    throw new NullPointerException("output file");
	if (aOutFile.exists())
	    aOutFile.delete();
	if (aQuality == null)
	    throw new NullPointerException("quality");
	if (!FFMPEGUtils.isFormatSupportedForDecoding(FilenameUtils.getExtension(aInFile.getName())))
	    throw new IllegalArgumentException("input file format");
	if (!FFMPEGUtils.isFormatSupportedForEncoding(FilenameUtils.getExtension(aOutFile.getName())))
	    throw new IllegalArgumentException("output file format");

	try {
	    return FFMPEGCall.execute("-i", aInFile.getAbsolutePath(), "-b:v", aQuality.getBitrate(), "-vf", aQuality.getScale(), aOutFile.getAbsolutePath());
	} catch (final IOException aIOException) {
	    throw new RuntimeException(aIOException);
	}
    }

    @Nullable
    public static Process transcode(@Nonnull final String sInputVideo, @Nonnull final String sOutputVideo, final @Nonnull EQuality aQuality) {
	if (StringUtils.isEmpty(sInputVideo))
	    throw new IllegalArgumentException("input video");
	if (StringUtils.isEmpty(sOutputVideo))
	    throw new IllegalArgumentException("output video");

	return transcode(new File(sInputVideo), new File(sOutputVideo), aQuality);
    }
}