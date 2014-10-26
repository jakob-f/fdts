package at.ac.tuwien.media.master.transcoderui.util;

import java.io.File;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Utils {
    private final static String LOCALE_NAME_ENGLISH = "English";
    private final static String LOCALE_NAME_GERMAN = "Deutsch";
    public final static String[] SUPPORTED_LOCALES = new String[] { LOCALE_NAME_ENGLISH, LOCALE_NAME_GERMAN };

    @Nullable
    public static File getDirectoryOrNull(@Nonnull final String sFilePath) {
	final File aDirectory = new File(sFilePath);

	return aDirectory.exists() && aDirectory.isDirectory() ? aDirectory : null;
    }

    @Nonnull
    public static File getDirectoryOrDefault(@Nonnull final String sFilePath) {
	final File aDirectory = getDirectoryOrNull(sFilePath);

	return aDirectory != null ? aDirectory : new File(Value.DEFAULT_FILEPATH);
    }

    @Nonnull
    public static String localeToString(@Nonnull final Locale aLocale) {
	String sLocale = LOCALE_NAME_ENGLISH;

	if (aLocale.equals(Locale.GERMAN))
	    sLocale = LOCALE_NAME_GERMAN;

	return sLocale;
    }

    @Nonnull
    public static Locale stringtoLocale(@Nonnull final String sLocale) {
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
