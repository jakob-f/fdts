package at.frohnwieser.mahut.webappapi.fs.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;

import at.frohnwieser.mahut.webappapi.config.Configuration;
import at.frohnwieser.mahut.webappapi.config.Configuration.EField;
import at.frohnwieser.mahut.webappapi.db.model.Asset;
import at.frohnwieser.mahut.webappapi.util.ThumbnailGenerator;
import at.frohnwieser.mahut.webappapi.util.Value;

public final class FSManager {
    private static File m_aAssetsDirectory;
    private static File m_aDBDirectory;
    private static File m_aMetaDirectory;
    private static File m_aOnotologyDirectory;
    private static File m_aThumbnailsDirectory;

    @Nullable
    private static File _writeFile(@Nonnull final String sFilePath, final byte[] aBytes) {
	final File aAssetFile = new File(sFilePath);
	if (!aAssetFile.exists())
	    try (final OutputStream aOS = new FileOutputStream(aAssetFile);) {
		aOS.write(aBytes);

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
    public static File getAssetsDirectory() {
	if (m_aAssetsDirectory == null)
	    m_aAssetsDirectory = _getOrCreateDirectory(Configuration.getInstance().getAsString(EField.DATA_PATH) + File.separator + Value.DATA_FOLDER_ASSETS);
	return m_aAssetsDirectory;
    }

    @Nonnull
    public static File getDBDirectory() {
	if (m_aDBDirectory == null)
	    m_aDBDirectory = _getOrCreateDirectory(getMetaDirectory().getAbsolutePath() + File.separator + Value.DATA_FOLDER_DB);
	return m_aDBDirectory;
    }

    @Nonnull
    public static File getMetaDirectory() {
	if (m_aMetaDirectory == null)
	    m_aMetaDirectory = _getOrCreateDirectory(Configuration.getInstance().getAsString(EField.DATA_PATH) + File.separator + Value.DATA_FOLDER_META);
	return m_aMetaDirectory;
    }

    @Nonnull
    public static File getOnotologyDirectory() {
	if (m_aOnotologyDirectory == null)
	    m_aOnotologyDirectory = _getOrCreateDirectory(getMetaDirectory().getAbsolutePath() + File.separator + Value.DATA_FOLDER_ONTOLOGY);
	return m_aOnotologyDirectory;
    }

    @Nonnull
    public static File getThumbnailsDirectroy() {
	if (m_aThumbnailsDirectory == null)
	    m_aThumbnailsDirectory = _getOrCreateDirectory(getMetaDirectory().getAbsolutePath() + File.separator + Value.DATA_FOLDER_THUMBNAILS);
	return m_aThumbnailsDirectory;
    }

    @Nonnull
    private static String _getFilePath(@Nonnull final Asset aAsset) {
	final String sFileExtension = FilenameUtils.getExtension(aAsset.getName().toLowerCase());
	final String sId = aAsset.getId();
	if (sId.length() < 10)
	    throw new IllegalArgumentException("id '" + sId + "' too short");

	final StringBuilder aSB = new StringBuilder();
	aSB.append(File.separatorChar + sId.substring(0, 3));
	aSB.append(File.separatorChar + sId.substring(3, 6));
	aSB.append(File.separatorChar + sId + "." + sFileExtension);
	return aSB.toString();
    }

    @Nullable
    public static String getAbsoluteFilePath(@Nullable final Asset aAsset, final boolean bIsThumbnail) {
	if (aAsset != null) {
	    if (bIsThumbnail)
		return getThumbnailsDirectroy().getAbsolutePath() + File.separator + _getFilePath(aAsset);
	    return getAssetsDirectory().getAbsolutePath() + File.separator + _getFilePath(aAsset);
	}

	return null;
    }

    public static boolean delete(@Nullable final Asset aAsset) {
	if (aAsset != null) {
	    final String sFilePath = _getFilePath(aAsset);
	    if (_deleteFile(sFilePath)) {
		// check if parent directories are empty
		_deleteEmptyDirectories(new File(FilenameUtils.getFullPath(sFilePath)));
		return true;
	    }
	}
	return false;
    }

    public static boolean save(@Nullable final Asset aAsset, final byte[] aBytes) {
	if (aAsset != null) {
	    // if needed create directory to save file in
	    final String sAbsouluteFilePath = getAbsoluteFilePath(aAsset, false);
	    _getOrCreateDirectory(FilenameUtils.getFullPath(sAbsouluteFilePath));
	    final File aFile = _writeFile(sAbsouluteFilePath, aBytes);
	    if (aFile.exists())
		return ThumbnailGenerator.create(_getOrCreateDirectory(FilenameUtils.getFullPath(getAbsoluteFilePath(aAsset, true))), aFile);
	}
	return false;
    }
}
