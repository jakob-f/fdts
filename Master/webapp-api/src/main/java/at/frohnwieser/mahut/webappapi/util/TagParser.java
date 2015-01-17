package at.frohnwieser.mahut.webappapi.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.commons.CommonValue;

public final class TagParser {

    private static Collection<String> _parse(@Nonnull final String sText, @Nonnull final String sTag) {
	final Collection<String> aTags = new ArrayList<String>();

	if (StringUtils.isNotEmpty(sText)) {
	    final StringTokenizer aTokenizer = new StringTokenizer(sText);

	    // parse hash tags
	    while (aTokenizer.hasMoreTokens()) {
		final String sToken = aTokenizer.nextToken();
		if (sToken.length() > 1 && sToken.startsWith(sTag))
		    aTags.add(sToken.substring(1, sToken.length()));
	    }
	}

	return aTags;
    }

    public static Collection<String> parseHashTags(@Nonnull final String sText) {
	return _parse(sText, CommonValue.CHARACTER_HASH);
    }

    public static Collection<String> parseAtTags(@Nonnull final String sText) {
	return _parse(sText, CommonValue.CHARACTER_AT);
    }
}
