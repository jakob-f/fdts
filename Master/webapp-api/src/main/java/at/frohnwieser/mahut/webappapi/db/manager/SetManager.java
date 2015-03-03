package at.frohnwieser.mahut.webappapi.db.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.webappapi.db.model.Asset;
import at.frohnwieser.mahut.webappapi.db.model.ERole;
import at.frohnwieser.mahut.webappapi.db.model.EState;
import at.frohnwieser.mahut.webappapi.db.model.Group;
import at.frohnwieser.mahut.webappapi.db.model.ReadWrite;
import at.frohnwieser.mahut.webappapi.db.model.Set;
import at.frohnwieser.mahut.webappapi.db.model.User;
import at.frohnwieser.mahut.webappapi.fs.manager.FSManager;
import at.frohnwieser.mahut.webappapi.util.Value;

public class SetManager extends AbstractManager<Set> {
    private static SetManager m_aInstance = new SetManager();

    private SetManager() {
	super(Value.DB_COLLECTION_SETS);

	if (f_aEntries.isEmpty()) {
	    FSManager.createGetAssetsFolder();
	    _saveCommit(new Set(Value.ROOT_SET_ID, Value.DATA_FOLDER_ASSETS, "root set", Value.ROOT_SET_ID));
	}
    }

    public static SetManager getInstance() {
	return m_aInstance;
    }

    @Nonnull
    public Collection<Set> allFor(@Nullable final User aUser, @Nullable final User aFor) {
	if (aUser != null && aFor != null)
	    return f_aEntries.values().stream().filter(aSet -> aSet.getOwnerId() == aFor.getId() && isRead(aUser, aSet))
		    .collect(Collectors.toCollection(ArrayList::new));

	return new ArrayList<Set>();
    }

    @Nonnull
    public Collection<Set> allRead(@Nullable final User aUser) {
	return f_aEntries.values().stream().filter(aSet -> isRead(aUser, aSet)).collect(Collectors.toCollection(ArrayList::new));
    }

    @Nonnull
    public Collection<Set> allWriteFor(@Nullable final User aFor) {
	if (aFor != null)
	    if (aFor.getRole().is(ERole.ADMIN))
		return all();
	    else
		return GroupManager.getInstance()._allFor(aFor).stream().flatMap(aGroup -> aGroup.getWriteSetIds().stream()).map(nId -> get(nId))
		        .filter(o -> o != null).collect(Collectors.toCollection(ArrayList::new));

	return new ArrayList<Set>();
    }

    @Override
    public boolean delete(@Nullable final Set aEntry) {
	if (aEntry != null) {
	    final Set aSet = get(aEntry.getId());

	    if (aSet != null) {
		// recursively delete all child sets
		final Collection<Long> aChildAssetIds = new ArrayList<Long>(aSet.getChildSetIds());
		aChildAssetIds.forEach(nSetId -> _internalDelete(get(nSetId)));

		// remove this set from all groups and hash tags
		if (GroupManager.getInstance()._removeFromAll(aSet) && HashTagManager.getInstance()._removeFromAll(aEntry)) {
		    // remove all assets of this set
		    final Collection<Long> aAssetIds = new ArrayList<Long>(aSet.getAssetIds());
		    aAssetIds.forEach(nAssetId -> AssetManager.getInstance()._internalDelete(AssetManager.getInstance().get(nAssetId)));

		    // remove from file system
		    if (FSManager.delete(aSet))
			// remove from parent set
			if (_removeFromAll(aSet))
			    return _deleteCommit(aSet);
		}
	    }

	    rollback();
	}

	return false;
    }

    @Nullable
    public Set getParent(@Nullable final Asset aAsset) {
	if (aAsset != null)
	    return f_aEntries.values().stream().filter(aEntry -> aEntry.getAssetIds().contains(aAsset.getId())).findFirst().orElse(null);

	return null;
    }

    @Nullable
    public Set getParent(@Nullable final Set aSet) {
	if (aSet != null)
	    return f_aEntries.values().stream().filter(aEntry -> aEntry.getChildSetIds().contains(aSet.getId())).findFirst().orElse(null);

	return null;
    }

    @Nonnull
    public Collection<Set> getParents(@Nullable final Set aSet) {
	// order is important!
	final LinkedList<Set> aParentSets = new LinkedList<Set>();

	if (aSet != null) {
	    Set aCurrentSet = aSet;
	    aParentSets.add(aCurrentSet);

	    while ((aCurrentSet = getParent(aCurrentSet)) != null)
		aParentSets.addFirst(aCurrentSet);
	}

	return aParentSets;
    }

    @Nullable
    private Set _getFromHash(@Nonnull final String sHash) {
	if (StringUtils.isNotEmpty(sHash))
	    return f_aEntries.values().stream().filter(aSet -> aSet.getHash().equals(sHash)).findFirst().orElse(null);

	return null;
    }

    private Set _checkUserOrReturnNull(@Nullable final User aUser, @Nullable final Set aSet) {
	if (aUser != null && aSet != null && (aUser.getRole().is(ERole.ADMIN) || GroupManager.getInstance().isRead(aUser, aSet)))
	    return aSet;

	return null;
    }

    private Set _returnReadOrNull(@Nullable final User aUser, @Nullable final Set aSet) {
	return aSet != null && aSet.getState() == EState.PUBLIC ? aSet : _checkUserOrReturnNull(aUser, aSet);
    }

    public boolean isRead(@Nullable final User aUser, @Nullable final Set aSet) {
	return _returnReadOrNull(aUser, aSet) != null;
    }

    @Nullable
    public Set getRead(@Nullable final User aUser, @Nullable final long nId) {
	return _returnReadOrNull(aUser, get(nId));
    }

    @Nullable
    public Set getFromHash(@Nullable final User aUser, @Nullable final String sHash) {
	final Set aSet = _getFromHash(sHash);

	return aSet != null ? aSet.getState().is(EState.PUBLISHED) ? aSet : _returnReadOrNull(aUser, aSet) : null;
    }

    /**
     * does not commit
     */
    protected boolean _removeFromAll(@Nullable final Asset aAsset) {
	if (aAsset != null) {
	    final Set aSet = getParent(aAsset);

	    if (aSet != null && aSet.remove(aAsset))
		_internalSave(aSet);

	    return true;
	}

	return false;
    }

    /**
     * does not commit
     */
    private boolean _removeFromAll(@Nullable final Set aSet) {
	if (aSet != null) {
	    final Set aParentSet = getParent(aSet);

	    if (aParentSet != null && aParentSet.remove(aSet))
		_internalSave(aParentSet);

	    return true;
	}

	return false;
    }

    /**
     * does not commit
     */
    private boolean _copyReadPermissionsFromParent(@Nullable final Set aSet, @Nullable final Set aParentSet) {
	if (aSet != null && aParentSet != null) {
	    final Collection<Group> aParentGroups = GroupManager.getInstance()._allFor(aParentSet);

	    if (CollectionUtils.isNotEmpty(aParentGroups))
		aParentGroups.stream().filter(aGroup -> {
		    final ReadWrite aPermission = aGroup.getPermissionFor(aParentSet);
		    return aPermission.isRead() || aPermission.isWrite();
		}).forEach(aGroup -> GroupManager.getInstance()._internalSave(aGroup.setPermission(aSet, true, false)));

	    return true;
	}

	return false;
    }

    /**
     * does not commit
     */
    private boolean _copyState(@Nullable final Set aSet) {
	if (aSet != null)
	    return AssetManager.getInstance()._setStates(aSet.getAssetIds(), aSet.getState());

	return false;
    }

    @Override
    public boolean save(@Nullable final Set aSet) {
	if (aSet != null) {
	    // new set: save set in root on file system
	    if (!contains(aSet) && !FSManager.save(null, aSet))
		return false;

	    // save or update hash tags and update set assets
	    if (HashTagManager.getInstance()._save(aSet) && _copyState(aSet))
		// save or update set
		return _saveCommit(aSet);

	    rollback();
	}

	return false;

    }

    public boolean save(final long nParentSetId, @Nullable final Set aSet) {
	final Set aParentSet = get(nParentSetId);

	if (aSet != null && aParentSet != null) {
	    boolean bSuccess = true;

	    // set already present -> move on file system
	    if (contains(aSet)) {
		if (!FSManager.move(aSet, aParentSet))
		    bSuccess = false;

		// update old set
		final Set aOldParentSet = getParent(aSet);
		aOldParentSet.remove(aSet);
		_internalSave(aOldParentSet);
	    }
	    // save new set on file system and copy groups from parent
	    else if (FSManager.save(aParentSet, aSet))
		if (!_copyReadPermissionsFromParent(aSet, aParentSet))
		    bSuccess = false;

	    // update parent set and save hash tags
	    aParentSet.add(aSet);
	    if (bSuccess && _internalSave(aParentSet) && HashTagManager.getInstance()._save(aSet) && _copyState(aSet))
		return _saveCommit(aSet);

	    rollback();
	}

	return false;
    }
}
