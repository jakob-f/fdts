package at.frohnwieser.mahut.ffmpegwrapper.util;

import java.io.IOException;

import javax.annotation.Nonnull;

public abstract class AbstractSystemCall {
    @Nonnull
    protected static Process _internalExecute(@Nonnull final String[] sArgs) throws IOException {
	return new ProcessBuilder().command(sArgs).redirectErrorStream(true).start();
    }
}
