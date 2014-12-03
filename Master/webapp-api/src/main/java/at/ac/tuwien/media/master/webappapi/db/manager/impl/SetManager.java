package at.ac.tuwien.media.master.webappapi.db.manager.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.ac.tuwien.media.master.webappapi.db.manager.AbstractManager;
import at.ac.tuwien.media.master.webappapi.db.model.Asset;
import at.ac.tuwien.media.master.webappapi.db.model.Set;
import at.ac.tuwien.media.master.webappapi.fs.manager.FSManager;
import at.ac.tuwien.media.master.webappapi.util.Value;

public class SetManager extends AbstractManager<Set> {
    private static SetManager m_aInstance = new SetManager();

    private SetManager() {
	super(Value.DB_COLLECTION_SETS);
    }

    public static SetManager getInstance() {
	return m_aInstance;
    }

    @Nullable
    public Set getParent(@Nullable final Asset aAsset) {
	aRWLock.readLock().lock();

	final Set nFoundSet = f_aEntries.values().stream().filter(aEntry -> aEntry.getAssetsIds().contains(aAsset.getId())).findFirst().orElse(null);

	aRWLock.readLock().unlock();

	// returns null for assets in root
	return nFoundSet;
    }

    @Nullable
    public Set getParent(@Nullable final Set aSet) {
	aRWLock.readLock().lock();

	final Set nFoundSet = f_aEntries.values().stream().filter(aEntry -> aEntry.getChildSetIds().contains(aSet.getId())).findFirst().orElse(null);

	aRWLock.readLock().unlock();

	// returns null for sets in root
	return nFoundSet;
    }

    @Override
    @Nonnull
    public boolean save(@Nullable final Set aSet) {
	// new set: create files on file system
	if (!contains(aSet)) {
	    if (FSManager.save(aSet))
		return super.save(aSet);
	    else
		return false;
	}

	// save or update set
	return super.save(aSet);
    }

    @Nonnull
    public boolean save(final long nParentSetId, @Nullable final Set aSet) {
	final Set aParentSet = get(nParentSetId);

	// try to add to parent
	if (aParentSet != null && aParentSet.addChildSet(aSet))
	    if (super.save(aParentSet))
		return save(aSet);

	return false;
    }

    @Nonnull
    public boolean move(@Nullable final Set aSet, @Nullable final Set aNewParentSet) {
	// TODO order of calls
	// move files on file system
	if (FSManager.move(aSet, aNewParentSet)) {
	    // update new set
	    aNewParentSet.addChildSet(aSet);
	    save(aNewParentSet);

	    // update old set
	    final Set aOldParentSet = getParent(aSet);
	    aOldParentSet.removeChildSet(aSet);
	    save(aOldParentSet);
	}

	// save or update set
	return super.save(aSet);
    }
}
