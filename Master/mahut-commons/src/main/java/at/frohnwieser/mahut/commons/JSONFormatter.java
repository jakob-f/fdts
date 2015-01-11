package at.frohnwieser.mahut.commons;

import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public final class JSONFormatter {
    @Nonnull
    public static String format(@Nullable final JSONArray aJSONArray) {
	if (aJSONArray != null)
	    for (final Object aObject : aJSONArray)
		return format(aObject);

	return "";
    }

    @Nonnull
    public static String format(@Nullable final JSONObject aJSONObject) {
	if (aJSONObject != null) {
	    final StringBuilder aSB = new StringBuilder();
	    for (final Entry<String, Object> aEntry : aJSONObject.entrySet())
		aSB.append(aEntry.getKey() + ": " + format(aEntry.getValue()) + "\n");

	    return aSB.toString();
	}
	return "";
    }

    @Nonnull
    public static String format(@Nullable final Object aObject) {
	if (aObject != null)
	    if (aObject instanceof JSONArray)
		return format((JSONArray) aObject);
	    else if (aObject instanceof JSONObject)
		return format((JSONObject) aObject);
	    else
		return aObject.toString().trim();

	return "";
    }

    @Nonnull
    public static String format(@Nullable final String aJSONString) {
	try {
	    if (aJSONString != null)
		return format(new JSONParser(JSONParser.MODE_PERMISSIVE).parse(aJSONString, JSONObject.class));
	} catch (final ParseException aParseException) {
	    aParseException.printStackTrace();
	}

	return "";
    }

}
