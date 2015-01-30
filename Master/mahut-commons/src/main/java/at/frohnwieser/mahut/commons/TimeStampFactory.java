package at.frohnwieser.mahut.commons;

import java.time.Instant;
import java.time.LocalDateTime;

import org.apache.commons.lang3.time.FastDateFormat;

public final class TimeStampFactory {
    // TODO add time zone
    public final static String DATE_PATTERN = "yyyy-MM-dd HH:mm";

    private TimeStampFactory() {
    }

    public static String format(final long nTimeStamp) {
	return FastDateFormat.getInstance(DATE_PATTERN).format(nTimeStamp);
    }

    public static LocalDateTime now() {
	return LocalDateTime.now();
    }

    public static String nowFormatted() {
	return format(nowMillis());
    }

    public static long nowMillis() {
	return Instant.now().toEpochMilli();
    }
}
