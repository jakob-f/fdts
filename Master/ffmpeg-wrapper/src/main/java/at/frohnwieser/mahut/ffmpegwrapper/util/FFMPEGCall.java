package at.frohnwieser.mahut.ffmpegwrapper.util;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.ArrayUtils;

public final class FFMPEGCall extends AbstractSystemCall {
    private static final String FFMPEG = ClassLoader.getSystemResource("ffmpeg").getPath();

    private FFMPEGCall() {
    }

    @Nonnull
    public static Process execute(@Nonnull final String... sArgs) throws IOException {
	return _internalExecute(ArrayUtils.addAll(new String[] { FFMPEG }, sArgs));
    }
}