package at.frohnwieser.mahut.ffmpegwrapper.util;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

public final class FFMPEGUtils {
    private enum EFlag {
	ENCODING('E',
	        1),
	DECODING('D',
	        0),
	CODEC_VIDEO('V',
	        2),
	CODEC_AUDIO('A',
	        2),
	CODEC_SUBTITLE('S',
	        2);

	private final char f_cFlag;
	private final int f_nPosition;

	private EFlag(final char cFlag, @Nonnegative final int nPosition) {
	    f_cFlag = cFlag;
	    f_nPosition = nPosition;
	}

	public boolean isSupported(@Nonnull final String sFlags) {
	    return sFlags.length() >= f_nPosition ? sFlags.charAt(f_nPosition) == f_cFlag : false;
	}
    }

    private static Map<String, String> s_aCodecs;
    private static Map<String, String> s_aFormats;

    private FFMPEGUtils() {
    }

    // ffmpeg -codes
    private static boolean _isCodecFlagSupported(@Nonnull final String sCodec, @Nonnull final EFlag... sFlags) {
	// load the format list if necessary
	if (s_aCodecs == null)
	    s_aCodecs = new TreeMap<String, String>();

	// check if the format is contained
	final String sCodecFlags = s_aCodecs.get(sCodec);
	if (StringUtils.isNotEmpty(sCodecFlags))
	    return false;

	// check if the format is supported for the indicated flag
	for (final EFlag sFlag : sFlags)
	    if (!sFlag.isSupported(sCodecFlags))
		return false;

	return true;
    }

    public static boolean isCodecSupported(@Nonnull final String sCodec) {
	return _isCodecFlagSupported(sCodec, EFlag.DECODING, EFlag.ENCODING);
    }

    @Nonnull
    private static Map<String, String> _readFormats() throws IOException {
	final Map<String, String> aResultMap = new TreeMap<String, String>();
	final Process aProcess = FFMPEGCall.execute("-formats");

	Scanner aScanner = null;
	try {
	    aScanner = new Scanner(aProcess.getInputStream());

	    // parse output
	    String sFormatLine;
	    String[] sFormats;
	    while (null != (sFormatLine = aScanner.findWithinHorizon(Pattern.compile("(DE|D | E) [a-z0-9,_]+"), 0))) {
		sFormats = StringUtils.trim(sFormatLine).replaceAll("\\s+", " ").split(" ")[1].split(",");

		// add format to map
		for (int i = 0; i < sFormats.length; i++)
		    aResultMap.put(sFormats[i], sFormatLine.substring(0, 2));
	    }
	} catch (final Exception aException) {
	    aException.printStackTrace();
	} finally {
	    if (aScanner != null)
		aScanner.close();
	}

	return aResultMap;
    }

    // ffmpeg -formats
    private static boolean _isFormatFlagSupported(@Nonnull final String sFormat, @Nonnull final EFlag... sFlags) {
	try {
	    // load the format list if necessary
	    if (s_aFormats == null)
		s_aFormats = _readFormats();

	    // check if the format is contained
	    final String sFormatFlags = s_aFormats.get(sFormat.toLowerCase());
	    if (sFormatFlags == null)
		return false;

	    // check if the format is supported for the indicated flag
	    for (final EFlag sFlag : sFlags)
		if (!sFlag.isSupported(sFormatFlags))
		    return false;

	    return true;
	} catch (final IOException aIOException) {
	    aIOException.printStackTrace();
	}
	return false;
    }

    public static boolean isFormatSupported(@Nonnull final String sFormat) {
	return _isFormatFlagSupported(sFormat, EFlag.DECODING, EFlag.ENCODING);
    }

    public static boolean isFormatSupportedForDecoding(@Nonnull final String sFormat) {
	return _isFormatFlagSupported(sFormat, EFlag.DECODING);
    }

    public static boolean isFormatSupportedForEncoding(@Nonnull final String sFormat) {
	return _isFormatFlagSupported(sFormat, EFlag.ENCODING);
    }
}
