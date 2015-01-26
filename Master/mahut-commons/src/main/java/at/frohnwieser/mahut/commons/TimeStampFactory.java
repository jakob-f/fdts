package at.frohnwieser.mahut.commons;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nonnull;

public final class TimeStampFactory {
    public final static String DATE_PATTERN = "yyyy-MM-dd HH:mm";

    private TimeStampFactory() {
    }

    private static Instant _now() {
	return Instant.now();
    }

    private static LocalDateTime _fromInstant(@Nonnull final Instant aInstant) {
	return LocalDateTime.ofInstant(aInstant, ZoneId.systemDefault());
    }

    public static String format(final long nTimeStamp) {
	return _fromInstant(Instant.ofEpochMilli(nTimeStamp)).format(DateTimeFormatter.ofPattern(DATE_PATTERN));
    }

    public static LocalDateTime now() {
	return _fromInstant(_now());
    }

    public static String nowFormatted() {
	return now().format(DateTimeFormatter.ofPattern(DATE_PATTERN));
    }

    public static long nowMillis() {
	return _now().toEpochMilli();
    }
}
