package at.ac.tuwien.media.master.webappapi.fs.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

import javax.activation.DataHandler;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;

import at.ac.tuwien.media.master.webappapi.db.manager.impl.SetManager;
import at.ac.tuwien.media.master.webappapi.db.model.Asset;
import at.ac.tuwien.media.master.webappapi.db.model.Set;
import at.ac.tuwien.media.master.webappapi.util.Value;

public final class FSManager {

    // TODO check
    // also checks FS structure
    @Nullable
    private static File _getParentDirectory(@Nullable final Set aSet) {
	if (aSet != null) {
	    // get all parent ids up to root folder
	    final LinkedList<Long> aParentSetIds = new LinkedList<Long>();
	    Set aCurrentSet = aSet;
	    while ((aCurrentSet = SetManager.getInstance().getParent(aCurrentSet)) != null)
		aParentSetIds.add(aCurrentSet.getId());

	    // check all directories down to parent folder
	    File aCurrentDirectory = new File(Value.DATA_PATH_ASSETS);
	    if (!aCurrentDirectory.isDirectory())
		throw new RuntimeException("root folder does not exist");

	    if (CollectionUtils.isNotEmpty(aParentSetIds))
		for (final long aSetId : aParentSetIds) {
		    aCurrentDirectory = new File(aCurrentDirectory.getAbsolutePath() + File.separator + aSetId);

		    if (!aCurrentDirectory.isDirectory())
			throw new RuntimeException("expected folder '" + aCurrentDirectory.getAbsolutePath() + "' not found");

		    System.out.println(aCurrentDirectory);
		}

	    return aCurrentDirectory;
	}

	return null;
    }

    public static boolean move(@Nullable final Asset aAsset, @Nullable final Set aNewParentSet) {
	return false;
    }

    public static boolean move(@Nullable final Set aSet, @Nullable final Set aNewParentSet) {
	try {
	    final File aOldParentDirectory = _getParentDirectory(aSet);
	    final File aNewParentParentDirectory = _getParentDirectory(aSet);

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

    public static File save(@Nullable final Set aSet, @Nullable final String sName, @Nullable final DataHandler aData) {
	final File aCurrentDirecory = _getParentDirectory(aSet);

	if (aCurrentDirecory != null) {
	    InputStream aIS = null;
	    OutputStream aOS = null;

	    try {
		final File aAssetFile = new File(aCurrentDirecory.getAbsoluteFile() + File.separator + sName);

		if (!aAssetFile.exists()) {
		    aIS = aData.getInputStream();
		    aOS = new FileOutputStream(aAssetFile);

		    // see
		    // http://www.journaldev.com/861/4-ways-to-copy-file-in-java
		    final byte[] nBuffer = new byte[1024];
		    int nBytesRead = 0;
		    while ((nBytesRead = aIS.read(nBuffer)) != -1)
			aOS.write(nBuffer, 0, nBytesRead);

		    return aAssetFile;
		}
	    } catch (final Exception aException) {
		// TODO
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

    public static boolean save(@Nullable final Set aSet) {
	File aCurrentDirecory = _getParentDirectory(aSet);

	if (aCurrentDirecory != null) {
	    // create new set directories
	    aCurrentDirecory = new File(aCurrentDirecory.getAbsolutePath() + File.separator + aSet.getId());

	    if (aCurrentDirecory.mkdir()) {
		aCurrentDirecory = new File(aCurrentDirecory.getAbsolutePath() + File.separator + Value.ASSET_FOLDER_META_CONTENT);

		return aCurrentDirecory.mkdir();
	    }
	}

	return false;
    }
}
