package at.ac.tuwien.media.master.webappapi.fs.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedList;

import javax.activation.DataHandler;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.db.manager.impl.SetManager;
import at.ac.tuwien.media.master.webappapi.db.model.Asset;
import at.ac.tuwien.media.master.webappapi.db.model.Set;
import at.ac.tuwien.media.master.webappapi.util.Value;

public final class FSManager {

    // also checks FS structure
    @Nonnull
    private static File _getSetDirectory(@Nullable final Set aSet) {
	File aCurrentDirectory = new File(Value.DATA_PATH_ASSETS);

	if (aSet != null) {
	    // get all parent ids up to the root folder
	    final LinkedList<Long> aParentSetIds = new LinkedList<Long>();
	    aParentSetIds.add(aSet.getId());

	    // find all parent sets
	    Set aCurrentSet = aSet;
	    while ((aCurrentSet = SetManager.getInstance().getParent(aCurrentSet)) != null)
		aParentSetIds.add(aCurrentSet.getId());

	    // check all directories down to parent folder
	    if (!aCurrentDirectory.isDirectory())
		throw new RuntimeException("root folder does not exist");

	    if (CollectionUtils.isNotEmpty(aParentSetIds)) {
		// go back to the current parent set
		Collections.reverse(aParentSetIds);
		for (final long aSetId : aParentSetIds) {
		    aCurrentDirectory = new File(aCurrentDirectory.getAbsolutePath() + File.separator + aSetId);

		    if (!aCurrentDirectory.isDirectory())
			throw new RuntimeException("expected folder '" + aCurrentDirectory.getAbsolutePath() + "' not found");
		}
	    }
	}

	return aCurrentDirectory;
    }

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

    public static File save(@Nullable final Set aSet, @Nullable final String sName, @Nullable final DataHandler aData, final boolean bIsMetaContent) {
	if (StringUtils.isNotEmpty(sName) && aData != null) {
	    InputStream aIS = null;
	    OutputStream aOS = null;

	    try {
		String sAssetFilePath = _getSetDirectory(aSet).getAbsolutePath();
		if (bIsMetaContent)
		    sAssetFilePath += File.separator + Value.ASSET_FOLDER_META_CONTENT;
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

    public static boolean save(@Nullable final Set aParentSet, @Nullable final Set aSet) {
	if (aSet != null) {
	    // create new set directories
	    File aCurrentDirecory = new File(_getSetDirectory(aParentSet).getAbsolutePath() + File.separator + aSet.getId());

	    if (aCurrentDirecory.mkdir()) {
		aCurrentDirecory = new File(aCurrentDirecory.getAbsolutePath() + File.separator + Value.ASSET_FOLDER_META_CONTENT);

		return aCurrentDirecory.mkdir();
	    }
	}

	return false;
    }
}
