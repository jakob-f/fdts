package at.frohnwieser.mahut.webappapi.fs.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataHandler;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.webappapi.config.Configuration;
import at.frohnwieser.mahut.webappapi.config.Configuration.EField;
import at.frohnwieser.mahut.webappapi.db.model.Asset;
import at.frohnwieser.mahut.webappapi.util.Value;

public final class FSManager {

    @Nullable
    private static File _writeFile(@Nonnull final String sFilePath, @Nonnull final DataHandler aData) {
	final File aAssetFile = new File(sFilePath);
	if (!aAssetFile.exists())
	    try (final InputStream aIS = aData.getInputStream(); final OutputStream aOS = new FileOutputStream(aAssetFile);) {
		final byte[] nBuffer = new byte[1024];
		int nBytesRead = 0;
		while ((nBytesRead = aIS.read(nBuffer)) != -1)
		    aOS.write(nBuffer, 0, nBytesRead);

		return aAssetFile;
	    } catch (final Exception aException) {
		throw new RuntimeException(aException);
	    }

	return null;
    }

    private static void _deleteEmptyDirectories(@Nonnull final File aDirectory) {
	if (aDirectory.isDirectory())
	    if (aDirectory.list().length == 0) {
		_deleteEmptyDirectories(aDirectory.getParentFile());
		aDirectory.delete();
	    }
    }

    private static boolean _deleteFile(@Nonnull final String sFilePath) {
	final File aFile = new File(sFilePath);

	if (aFile.isFile())
	    return aFile.delete();

	return false;
    }

    @Nonnull
    private static File _getOrCreateDirectory(@Nonnull final String sDirectoryPath) {
	final File aDirectory = new File(sDirectoryPath);

	if (!aDirectory.isDirectory())
	    aDirectory.mkdirs();

	return aDirectory;
    }

    @Nonnull
    public static File createAssetsFolder() {
	return _getOrCreateDirectory(Configuration.getInstance().getAsString(EField.DATA_PATH) + File.separator + Value.DATA_FOLDER_ASSETS);
    }

    @Nonnull
    public static File createDBFolder() {
	return _getOrCreateDirectory(createMetaFolder().getAbsolutePath() + File.separator + Value.DATA_FOLDER_DB);
    }

    @Nonnull
    public static File createMetaFolder() {
	return _getOrCreateDirectory(Configuration.getInstance().getAsString(EField.DATA_PATH) + File.separator + Value.DATA_FOLDER_META);
    }

    @Nonnull
    public static File createOnotologyFolder() {
	return _getOrCreateDirectory(createMetaFolder().getAbsolutePath() + File.separator + Value.DATA_FOLDER_ONTOLOGY);
    }

    public static boolean delete(@Nullable final Asset aAsset) {
	if (aAsset != null) {
	    final String sFilePath = aAsset.getFilePath();
	    if (_deleteFile(sFilePath)) {
		// check if parent directories are empty
		_deleteEmptyDirectories(new File(FilenameUtils.getFullPath(sFilePath)));
		return true;
	    }
	}

	return false;
    }

    public static boolean save(@Nullable final String sFilePath, @Nullable final DataHandler aData) {
	if (StringUtils.isNotEmpty(sFilePath) && aData != null) {
	    _getOrCreateDirectory(FilenameUtils.getFullPath(sFilePath));
	    return _writeFile(sFilePath, aData).exists();
	}

	return false;
    }
}
