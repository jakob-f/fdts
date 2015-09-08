package at.frohnwieser.mahut.commons;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

public final class FileUtils {
    @Nullable
    public static File getDirectoryOrNull(@Nullable final String sFilePath) {
	if (StringUtils.isEmpty(sFilePath))
	    return null;

	final File aDirectory = new File(sFilePath);
	return aDirectory.exists() && aDirectory.isDirectory() ? aDirectory : null;
    }

    @Nonnull
    public static File getDirectoryOrDefault(@Nullable final String sFilePath, @Nonnull final String sDefault) {
	final File aDirectory = getDirectoryOrNull(sFilePath);
	return aDirectory != null ? aDirectory : new File(sDefault);
    }

    @Nullable
    public static File getDirectorySave(@Nullable final String sFilePath) {
	if (StringUtils.isEmpty(sFilePath))
	    return null;

	File aDirectory = getDirectoryOrNull(sFilePath);
	if (aDirectory == null) {
	    aDirectory = new File(sFilePath);
	    aDirectory.mkdirs();
	}
	return aDirectory;
    }

    public static boolean cleanDirectory(@Nullable final String sFilePath) {
	final File aDirectory = getDirectoryOrNull(sFilePath);

	if (aDirectory != null) {
	    final HashSet<Boolean> aSet = Arrays.stream(aDirectory.listFiles()).map(File::delete).collect(Collectors.toCollection(HashSet::new));
	    return !aSet.contains(false);
	}
	return false;
    }
}
