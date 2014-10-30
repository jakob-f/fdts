package at.ac.tuwien.media.master.ffmpegwrapper.util;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.ArrayUtils;

public class FFPROBECall {
    private static final String FFPROBE = "ffprobe";

    private FFPROBECall() {
    }

    @Nonnull
    public static Process execute(@Nonnull final String... sArgs) throws IOException {
	final String[] sArguments = ArrayUtils.addAll(new String[] { FFPROBE }, sArgs);

	final ProcessBuilder aProcessBuilder = new ProcessBuilder().command(sArguments).redirectErrorStream(true);
	return aProcessBuilder.start();
    }
}
