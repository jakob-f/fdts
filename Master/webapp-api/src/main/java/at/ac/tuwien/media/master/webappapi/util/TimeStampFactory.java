package at.ac.tuwien.media.master.webappapi.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//TODO move to commons
public final class TimeStampFactory {
    private TimeStampFactory() {
    }

    public static LocalDateTime get() {
	return LocalDateTime.now();
    }

    public static String getAsString() {
	return get().format(DateTimeFormatter.ofPattern(Value.DATE_PATTERN));
    }
}
