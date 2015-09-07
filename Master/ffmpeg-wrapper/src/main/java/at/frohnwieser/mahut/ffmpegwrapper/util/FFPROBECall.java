package at.frohnwieser.mahut.ffmpegwrapper.util;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;

public class FFPROBECall extends AbstractSystemCall {
    private static final String FFPROBE = "ffprobe";
    private static String m_sPathToAlternative;

    private FFPROBECall() {
    }

    public static void setAlternativeEnvironment(@Nullable final String sPathToAlternative) {
	m_sPathToAlternative = sPathToAlternative;
    }

    @Nonnull
    public static Process execute(@Nonnull final String... sArgs) throws IOException {
	final String sExecutablePath = m_sPathToAlternative != null ? m_sPathToAlternative : FFPROBE;
	return _internalExecute(ArrayUtils.addAll(new String[] { sExecutablePath }, sArgs));
    }
}
