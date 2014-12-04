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
	// new set: save set in root on file system
	if (!contains(aSet))
	    if (!FSManager.save(null, aSet))
		return false;

	// save or update set
	return super.save(aSet);
    }

    @Nonnull
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
		    aOldParentSet.removeChildSet(aSet);
		    super.save(aOldParentSet);
		}
		// save new set
		else if (!FSManager.save(aParentSet, aSet))
		    return false;

		// update (new) parent set
		aParentSet.addChildSet(aSet);
		super.save(aParentSet);

		// save or update set
		return super.save(aSet);
	    }
	}

	return false;
    }
}
