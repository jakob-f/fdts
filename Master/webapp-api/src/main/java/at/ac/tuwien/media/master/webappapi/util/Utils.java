package at.ac.tuwien.media.master.webappapi.util;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;

import at.ac.tuwien.media.master.webappapi.db.manager.impl.SetManager;
import at.ac.tuwien.media.master.webappapi.db.model.Set;

public final class Utils {

    // also checks FS structure
    @Nullable
    private static File _getParentDirectory(@Nullable final Set aSet) {
	if (aSet != null) {
	    // get all parent ids up to root folder
	    final LinkedList<Long> aParentSetIds = new LinkedList<Long>();
	    Set aCurrentSet = aSet;
	    while ((aCurrentSet = SetManager.getInstance().getParentSet(aCurrentSet)) != null)
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

    public static boolean createSetOnFS(@Nullable final Set aSet) {
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

    public static boolean moveSetOnFS(@Nullable final Set aSet, @Nullable final Set aNewParentSet) {
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
}
