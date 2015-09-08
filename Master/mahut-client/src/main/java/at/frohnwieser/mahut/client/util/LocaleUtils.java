package at.frohnwieser.mahut.client.util;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class LocaleUtils {
    private final static String LOCALE_NAME_ENGLISH = "English";
    private final static String LOCALE_NAME_GERMAN = "Deutsch";
    public final static String[] SUPPORTED_LOCALES = new String[] { LOCALE_NAME_ENGLISH, LOCALE_NAME_GERMAN };

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
