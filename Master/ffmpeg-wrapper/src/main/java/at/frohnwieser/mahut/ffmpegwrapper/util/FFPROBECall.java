package at.frohnwieser.mahut.ffmpegwrapper.util;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.ArrayUtils;

public class FFPROBECall extends AbstractSystemCall {
    private static final String FFPROBE = ClassLoader.getSystemResource("ffprobe").getPath();

    private FFPROBECall() {
    }

    @Nonnull
    public static Process execute(@Nonnull final String... sArgs) throws IOException {
	return _internalExecute(ArrayUtils.addAll(new String[] { FFPROBE }, sArgs));
    }
}
