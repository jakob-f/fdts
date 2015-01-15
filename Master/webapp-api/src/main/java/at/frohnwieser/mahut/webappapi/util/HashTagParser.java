package at.frohnwieser.mahut.webappapi.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.commons.CommonValue;

public final class HashTagParser {

    public static Collection<String> parse(@Nonnull final String sText) {
	final Collection<String> aTags = new ArrayList<String>();

	if (StringUtils.isNotEmpty(sText)) {
	    final StringTokenizer aTokenizer = new StringTokenizer(sText);

	    // parse hash tags
	    while (aTokenizer.hasMoreTokens()) {
		final String sToken = aTokenizer.nextToken();
		if (sToken.length() > 1 && sToken.startsWith(CommonValue.CHARACTER_HASH))
		    aTags.add(sToken.substring(1, sToken.length()));
	    }
	}

	return aTags;
    }
}
