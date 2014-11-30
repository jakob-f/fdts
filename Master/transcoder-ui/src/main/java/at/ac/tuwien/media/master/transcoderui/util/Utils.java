package at.ac.tuwien.media.master.transcoderui.util;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

public final class Utils {
    private final static String LOCALE_NAME_ENGLISH = "English";
    private final static String LOCALE_NAME_GERMAN = "Deutsch";
    public final static String[] SUPPORTED_LOCALES = new String[] { LOCALE_NAME_ENGLISH, LOCALE_NAME_GERMAN };

    @Nullable
    public static File getDirectoryOrNull(@Nullable final String sFilePath) {
	if (StringUtils.isEmpty(sFilePath))
	    return null;

	final File aDirectory = new File(sFilePath);

	return aDirectory.exists() && aDirectory.isDirectory() ? aDirectory : null;
    }

    @Nonnull
    public static File getDirectoryOrDefault(@Nullable final String sFilePath) {
	final File aDirectory = getDirectoryOrNull(sFilePath);

	return aDirectory != null ? aDirectory : new File(Value.FILEPATH_DEFAULT);
    }

    @Nonnull
    public static File getDirectorySave(@Nullable final String sFilePath) {
	if (StringUtils.isEmpty(sFilePath))
	    return null;

	File aDirectory = getDirectoryOrNull(sFilePath);

	if (aDirectory == null) {
	    aDirectory = new File(sFilePath);
	    aDirectory.mkdirs();
	}
	return aDirectory;
    }

    public static boolean cleanDirectory(@Nullable final String sFilePath) {
	final File aDirectory = getDirectoryOrNull(sFilePath);

	if (aDirectory != null) {
	    final HashSet<Boolean> aSet = Arrays.stream(aDirectory.listFiles()).map(aFile -> aFile.delete()).collect(Collectors.toCollection(HashSet::new));

	    return !aSet.contains(false);
	}

	return false;
    }

    @Nonnull
    public static String localeToString(@Nullable final Locale aLocale) {
	String sLocale = LOCALE_NAME_ENGLISH;

	if (aLocale != null && aLocale.equals(Locale.GERMAN))
	    sLocale = LOCALE_NAME_GERMAN;

	return sLocale;
    }

    @Nonnull
    public static Locale stringtoLocale(@Nullable final String sLocale) {
	Locale aLocale = Locale.ENGLISH;

	if (sLocale != null)
	    switch (sLocale) {
	    case LOCALE_NAME_GERMAN:
		aLocale = Locale.GERMAN;
		break;

	    case LOCALE_NAME_ENGLISH:
	    default:
		aLocale = Locale.ENGLISH;
	    }

	return aLocale;
    }
}
