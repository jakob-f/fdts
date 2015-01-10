package at.frohnwieser.mahut.ffmpegwrapper.util;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.ArrayUtils;

public final class FFMPEGCall {
    private static final String FFMPEG = "ffmpeg";

    private FFMPEGCall() {
    }

    @Nonnull
    public static Process execute(@Nonnull final String... sArgs) throws IOException {
	final String[] sArguments = ArrayUtils.addAll(new String[] { FFMPEG }, sArgs);

	final ProcessBuilder aProcessBuilder = new ProcessBuilder().command(sArguments).redirectErrorStream(true);
	return aProcessBuilder.start();
    }
}