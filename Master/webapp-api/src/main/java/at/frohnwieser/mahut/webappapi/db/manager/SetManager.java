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
	    super.save(new Set(Value.ROOT_SET_ID, Value.DATA_FOLDER_ASSETS, "root set", Value.ROOT_SET_ID));
	}
    }

    public static SetManager getInstance() {
	return m_aInstance;
    }

    @Nonnull
    public Collection<Set> allFor(@Nullable final User aUser, @Nullable final User aFor) {
	if (aUser != null && aFor != null) {
	    m_aRWLock.readLock().lock();

	    final Collection<Set> aEntries = f_aEntries.values().stream().filter(aSet -> aSet.getOwnerId() == aFor.getId() && isRead(aUser, aSet))
		    .collect(Collectors.toCollection(ArrayList::new));

	    m_aRWLock.readLock().unlock();

	    return aEntries;
	}

	return new ArrayList<Set>();
    }

    @Nonnull
    public Collection<Set> allRead(@Nullable final User aUser) {
	m_aRWLock.readLock().lock();

	final Collection<Set> aEntries = f_aEntries.values().stream().filter(aSet -> isRead(aUser, aSet)).collect(Collectors.toCollection(ArrayList::new));

	m_aRWLock.readLock().unlock();

	return aEntries;
    }

    @Nonnull
    public Collection<Set> allWriteFor(@Nullable final User aFor) {
	if (aFor != null)
	    if (aFor.getRole().is(ERole.ADMIN))
		return all();
	    else
		return GroupManager.getInstance().allFor(aFor).stream().flatMap(aGroup -> aGroup.getWriteSetIds().stream()).map(nId -> get(nId))
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
		aChildAssetIds.forEach(nSetId -> delete(get(nSetId)));

		// remove this set from all groups and hash tags
		if (GroupManager.getInstance().removeFromAll(aSet) && HashTagManager.getInstance().removeFromAll(aEntry)) {
		    // remove all assets of this set
		    final Collection<Long> aAssetIds = new ArrayList<Long>(aSet.getAssetIds());
		    aAssetIds.forEach(nAssetId -> AssetManager.getInstance().delete(AssetManager.getInstance().get(nAssetId)));

		    // remove from file system
		    if (FSManager.delete(aSet))
			// remove from parent set
			if (_removeFromAll(aSet))
			    return super.delete(aSet);
		}
	    }
	}

	return false;
    }

    @Nullable
    public Set getParent(@Nullable final Asset aAsset) {

	if (aAsset != null) {
	    m_aRWLock.readLock().lock();

	    final Set aFoundSet = f_aEntries.values().stream().filter(aEntry -> aEntry.getAssetIds().contains(aAsset.getId())).findFirst().orElse(null);

	    m_aRWLock.readLock().unlock();

	    return aFoundSet;
	}

	return null;
    }

    @Nullable
    public Set getParent(@Nullable final Set aSet) {
	if (aSet != null) {
	    m_aRWLock.readLock().lock();

	    final Set aFoundSet = f_aEntries.values().stream().filter(aEntry -> aEntry.getChildSetIds().contains(aSet.getId())).findFirst().orElse(null);

	    m_aRWLock.readLock().unlock();

	    return aFoundSet;
	}

	return null;
    }

    @Nonnull
    public Collection<Set> getParents(@Nullable final Set aSet) {
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
	if (StringUtils.isNotEmpty(sHash)) {
	    m_aRWLock.readLock().lock();

	    final Set aFound = f_aEntries.values().stream().filter(aSet -> aSet.getHash().equals(sHash)).findFirst().orElse(null);

	    m_aRWLock.readLock().unlock();

	    return aFound;
	}

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

    public boolean removeFromAll(@Nullable final Asset aAsset) {
	if (aAsset != null) {
	    final Set aSet = getParent(aAsset);

	    if (aSet != null && aSet.remove(aAsset))
		return save(aSet);
	    else
		return true;
	}

	return false;
    }

    private boolean _removeFromAll(@Nullable final Set aSet) {
	if (aSet != null) {
	    final Set aParentSet = getParent(aSet);

	    if (aParentSet != null && aParentSet.remove(aSet))
		return save(aParentSet);
	    else
		return true;
	}

	return false;
    }

    private boolean _copyReadPermissionsFromParent(@Nullable final Set aSet, @Nullable final Set aParentSet) {
	if (aSet != null && aParentSet != null) {
	    final Collection<Group> aParentGroups = GroupManager.getInstance().allFor(aParentSet);

	    if (CollectionUtils.isNotEmpty(aParentGroups))
		aParentGroups.stream().filter(aGroup -> {
		    final ReadWrite aPermission = aGroup.getPermissionFor(aParentSet);
		    return aPermission.isRead() || aPermission.isWrite();
		}).forEach(aGroup -> GroupManager.getInstance().save(aGroup.setPermission(aSet, true, false)));

	    return true;
	}

	return false;
    }

    // TODO very slow
    private boolean _copyState(@Nullable final Set aSet) {
	if (aSet != null)
	    return AssetManager.getInstance().setStates(aSet.getAssetIds(), aSet.getState());

	return false;
    }

    @Override
    public boolean save(@Nullable final Set aSet) {
	if (aSet != null) {
	    // new set: save set in root on file system
	    if (!contains(aSet) && !FSManager.save(null, aSet))
		return false;

	    // save or update hash tags and update set assets
	    if (HashTagManager.getInstance().save(aSet) && _copyState(aSet))
		// save or update set
		return super.save(aSet);
	}

	return false;

    }

    public boolean save(final long nParentSetId, @Nullable final Set aSet) {
	final Set aParentSet = get(nParentSetId);

	if (aSet != null && aParentSet != null) {
	    // set already present -> move on file system
	    if (contains(aSet)) {
		if (!FSManager.move(aSet, aParentSet))
		    return false;

		// update old set
		final Set aOldParentSet = getParent(aSet);
		aOldParentSet.remove(aSet);
		super.save(aOldParentSet);
	    }
	    // save new set on file system and copy groups from parent
	    else if (FSManager.save(aParentSet, aSet))
		if (!_copyReadPermissionsFromParent(aSet, aParentSet))
		    return false;

	    // update parent set and save hash tags
	    aParentSet.add(aSet);
	    if (super.save(aParentSet) && HashTagManager.getInstance().save(aSet) && _copyState(aSet))
		return super.save(aSet);
	}

	return false;
    }
}
