package at.frohnwieser.mahut.commons;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class TimeStampFactory {
    public final static String DATE_PATTERN = "yyyy-MM-dd HH:mm";

    private TimeStampFactory() {
    }

    public static LocalDateTime get() {
	return LocalDateTime.now();
    }

    public static String getAsString() {
	return get().format(DateTimeFormatter.ofPattern(DATE_PATTERN));
    }
}
