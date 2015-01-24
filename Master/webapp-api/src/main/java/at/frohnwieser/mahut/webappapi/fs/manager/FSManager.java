package at.frohnwieser.mahut.webappapi.fs.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import javax.activation.DataHandler;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.webappapi.config.Configuration;
import at.frohnwieser.mahut.webappapi.config.Configuration.EField;
import at.frohnwieser.mahut.webappapi.db.manager.SetManager;
import at.frohnwieser.mahut.webappapi.db.model.Asset;
import at.frohnwieser.mahut.webappapi.db.model.Set;
import at.frohnwieser.mahut.webappapi.util.ThumbnailGenerator;
import at.frohnwieser.mahut.webappapi.util.Value;

public final class FSManager {

    private static boolean _createSetFolders(@Nullable final File aDirecory) {
	if (aDirecory != null && aDirecory.mkdir()) {
	    final String sSetPath = aDirecory.getAbsolutePath() + File.separator;

	    return new File(sSetPath + Value.SET_FOLDER_META_CONTENT).mkdir() && new File(sSetPath + Value.SET_FOLDER_THUMBNAILS).mkdir();
	}

	return false;
    }

    public static File createGetAssetsFolder() {
	final File aDirectory = new File(Configuration.getInstance().getAsString(EField.DATA_PATH) + File.separator + Value.DATA_FOLDER_ASSETS);

	if (!aDirectory.isDirectory())
	    _createSetFolders(aDirectory);

	return aDirectory;
    }

    public static File createGetMetaFolder() {
	final File aDirectory = new File(Configuration.getInstance().getAsString(EField.DATA_PATH) + File.separator + Value.DATA_FOLDER_META);

	if (!aDirectory.isDirectory())
	    aDirectory.mkdirs();

	return aDirectory;
    }

    public static File createGetDBFolder() {
	final File aDirectory = new File(createGetMetaFolder().getAbsolutePath() + File.separator + Value.DATA_FOLDER_DB);

	if (!aDirectory.isDirectory())
	    aDirectory.mkdirs();

	return aDirectory;
    }

    public static File createGetOnotologyFolder() {
	final File aDirectory = new File(createGetMetaFolder().getAbsolutePath() + File.separator + Value.DATA_FOLDER_ONTOLOGY);

	if (!aDirectory.isDirectory())
	    aDirectory.mkdirs();

	return aDirectory;
    }

    // also checks FS structure
    @Nonnull
    private static File _getSetDirectory(@Nullable final Set aSet) {
	File aCurrentDirectory = createGetAssetsFolder();

	if (aSet != null) {
	    // get all parent sets up to the root folder
	    final Collection<Set> aParentSets = SetManager.getInstance().getParents(aSet);
	    if (CollectionUtils.isNotEmpty(aParentSets))
		// go back to the current parent set
		for (final Set aParentSet : aParentSets) {
		    // only for root folder
		    if (aParentSet.getId() != Value.ROOT_SET_ID)
			aCurrentDirectory = new File(aCurrentDirectory.getAbsolutePath() + File.separator + aParentSet.getId());

		    if (!aCurrentDirectory.isDirectory())
			throw new RuntimeException("expected folder '" + aCurrentDirectory.getAbsolutePath() + "' not found");
		}
	}

	return aCurrentDirectory;
    }

    // currently not used
    @Deprecated
    public static boolean move(@Nullable final Asset aAsset, @Nullable final Set aNewParentSet) {
	// TODO
	return false;
    }

    public static boolean move(@Nullable final Set aSet, @Nullable final Set aNewParentSet) {
	try {
	    final File aOldParentDirectory = _getSetDirectory(aSet);
	    final File aNewParentParentDirectory = _getSetDirectory(aSet);

	    if (aOldParentDirectory != null && aNewParentParentDirectory != null) {
		final File aOldSetDirectory = new File(aOldParentDirectory.getAbsolutePath() + File.separator + aSet.getId());
		final File aNewParentSetDirectory = new File(aNewParentParentDirectory.getAbsolutePath() + File.separator + aNewParentSet.getId());

		FileUtils.copyDirectoryToDirectory(aOldSetDirectory, aNewParentSetDirectory);

		return true;
	    }
	} catch (final IOException aIOException) {
	    throw new RuntimeException(aIOException);
	}

	return false;
    }

    public static boolean delete(@Nullable final Asset aAsset) {
	if (aAsset != null) {
	    final File aAssetFile = aAsset.getFile();

	    if (aAssetFile.isFile())
		return aAssetFile.delete();
	}

	return false;
    }

    public static boolean delete(@Nullable final Set aSet) {
	try {
	    final File aSetDirectory = _getSetDirectory(aSet);

	    if (aSetDirectory.isDirectory()) {
		FileUtils.deleteDirectory(aSetDirectory);

		return true;
	    }
	} catch (final IOException aIOException) {
	    throw new RuntimeException(aIOException);
	}

	return false;
    }

    private static File _save(@Nullable final File aSetDirectory, @Nullable final DataHandler aData, @Nullable final String sName, final boolean bIsMetaContent) {
	if (aData != null && StringUtils.isNotEmpty(sName)) {
	    InputStream aIS = null;
	    OutputStream aOS = null;

	    try {
		String sAssetFilePath = aSetDirectory.getAbsolutePath();
		if (bIsMetaContent)
		    sAssetFilePath += File.separator + Value.SET_FOLDER_META_CONTENT;
		sAssetFilePath += File.separator + sName;

		final File aAssetFile = new File(sAssetFilePath);

		if (!aAssetFile.exists()) {
		    aIS = aData.getInputStream();
		    aOS = new FileOutputStream(aAssetFile);

		    final byte[] nBuffer = new byte[1024];
		    int nBytesRead = 0;
		    while ((nBytesRead = aIS.read(nBuffer)) != -1)
			aOS.write(nBuffer, 0, nBytesRead);

		    return aAssetFile;
		}
	    } catch (final Exception aException) {
		throw new RuntimeException(aException);
	    } finally {
		try {
		    if (aIS != null)
			aIS.close();
		} catch (final IOException e) {
		}

		try {
		    if (aOS != null)
			aOS.close();
		} catch (final IOException e) {
		}
	    }
	}

	return null;
    }

    public static File save(@Nullable final Set aSet, @Nullable final String sName, @Nullable final DataHandler aData, final boolean bIsMetaContent) {
	final File aSetDirectory = _getSetDirectory(aSet);
	final File aAssetFile = _save(aSetDirectory, aData, sName, bIsMetaContent);

	// create thumbnails
	if (ThumbnailGenerator.create(aAssetFile, aSetDirectory))
	    return aAssetFile;

	return null;
    }

    public static boolean save(@Nullable final Set aParentSet, @Nullable final Set aSet) {
	// create set directories
	if (aSet != null)
	    return _createSetFolders(new File(_getSetDirectory(aParentSet).getAbsolutePath() + File.separator + aSet.getId()));

	return false;
    }
}
