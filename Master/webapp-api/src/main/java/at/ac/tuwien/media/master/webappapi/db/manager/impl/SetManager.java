package at.ac.tuwien.media.master.webappapi.db.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.ac.tuwien.media.master.webappapi.db.manager.AbstractManager;
import at.ac.tuwien.media.master.webappapi.db.model.Asset;
import at.ac.tuwien.media.master.webappapi.db.model.Set;
import at.ac.tuwien.media.master.webappapi.db.model.User;
import at.ac.tuwien.media.master.webappapi.fs.manager.FSManager;
import at.ac.tuwien.media.master.webappapi.util.Value;

public class SetManager extends AbstractManager<Set> {
    private static SetManager m_aInstance = new SetManager();

    private SetManager() {
	super(Value.DB_COLLECTION_SETS);

	if (f_aEntries.isEmpty())
	    save(new Set(0, "root", "root set"));
    }

    public static SetManager getInstance() {
	return m_aInstance;
    }

    @Nonnull
    public Collection<Set> allFor(@Nullable final User aUser) {
	if (aUser != null)
	    return GroupManager.getInstance().allFor(aUser).stream().map(aGroup -> aGroup.getWriteSetIds()).flatMap(c -> c.stream())
		    .collect(Collectors.toCollection(HashSet::new)).stream().map(nId -> SetManager.getInstance().get(nId)).filter(o -> o != null)
		    .collect(Collectors.toCollection(ArrayList::new));

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
		if (GroupManager.getInstance().removeFromAll(aSet) && HashManager.getInstance().removeFromAll(aEntry)) {
		    // remove all assets of this set
		    final Collection<Long> aAssetIds = new ArrayList<Long>(aSet.getAssetsIds());
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

    public boolean removeFromAll(@Nullable final Asset aAsset) {
	if (aAsset != null) {
	    final Set aSet = getParent(aAsset);

	    if (aSet != null && aSet.remove(aAsset))
		return save(aSet);
	}

	return false;
    }

    private boolean _removeFromAll(@Nullable final Set aSet) {
	if (aSet != null) {
	    final Set aParentSet = getParent(aSet);

	    if (aParentSet != null && aParentSet.remove(aSet))
		return save(aParentSet);
	}

	return false;
    }

    @Nullable
    public Set getParent(@Nullable final Asset aAsset) {
	Set aFoundSet = null;

	if (aAsset != null) {
	    m_aRWLock.readLock().lock();

	    aFoundSet = f_aEntries.values().stream().filter(aEntry -> aEntry.getAssetsIds().contains(aAsset.getId())).findFirst().orElse(null);

	    m_aRWLock.readLock().unlock();
	}

	// returns null for assets in root
	return aFoundSet;
    }

    @Nullable
    public Set getParent(@Nullable final Set aSet) {
	Set aFoundSet = null;

	if (aSet != null) {
	    m_aRWLock.readLock().lock();

	    aFoundSet = f_aEntries.values().stream().filter(aEntry -> aEntry.getChildSetIds().contains(aSet.getId())).findFirst().orElse(null);

	    m_aRWLock.readLock().unlock();
	}

	// returns null for sets in root
	return aFoundSet;
    }

    @Override
    public boolean save(@Nullable final Set aSet) {
	if (aSet != null) {
	    // new set: save set in root on file system
	    if (!contains(aSet))
		if (!FSManager.save(null, aSet))
		    return false;

	    // save or update hash tags
	    if (HashManager.getInstance().save(aSet, aSet.getMetaContent()))
		// save or update set
		return super.save(aSet);
	}

	return false;

    }

    public boolean save(final long nParentSetId, @Nullable final Set aSet) {
	if (aSet != null) {
	    final Set aParentSet = SetManager.getInstance().get(nParentSetId);

	    if (aParentSet != null) {
		// set already present -> move on file system
		if (contains(aSet)) {
		    if (!FSManager.move(aSet, aParentSet))
			return false;

		    // update old set
		    final Set aOldParentSet = getParent(aSet);
		    aOldParentSet.remove(aSet);
		    super.save(aOldParentSet);
		}
		// save new set
		else if (!FSManager.save(aParentSet, aSet))
		    return false;

		// update (new) parent set
		aParentSet.add(aSet);
		super.save(aParentSet);

		// save or update set
		if (HashManager.getInstance().save(aSet, aSet.getMetaContent()))
		    return super.save(aSet);
	    }
	}

	return false;
    }
}
