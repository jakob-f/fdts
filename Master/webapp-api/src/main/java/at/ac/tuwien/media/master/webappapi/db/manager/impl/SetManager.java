package at.ac.tuwien.media.master.webappapi.db.manager.impl;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.ac.tuwien.media.master.webappapi.db.manager.AbstractManager;
import at.ac.tuwien.media.master.webappapi.db.model.Set;
import at.ac.tuwien.media.master.webappapi.util.Utils;
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
    public Set getParentSet(@Nullable final Set aSet) {
	aRWLock.readLock().lock();

	final Set nFoundSet = f_aEntries.values().stream().filter(aEntry -> aEntry.getChildSetIds().contains(aSet.getId())).findFirst().orElse(null);

	aRWLock.readLock().unlock();

	// returns null for sets in root
	return nFoundSet;
    }

    @Override
    @Nonnull
    public Collection<Set> save(@Nullable final Set aSet) {
	// new set: create files on file system
	if (!contains(aSet.getId()))
	    Utils.createSetOnFS(aSet);

	// save or update set
	return super.save(aSet);
    }

    @Nonnull
    public Collection<Set> move(@Nullable final Set aSet, @Nullable final Set aNewParentSet) {
	// move files on file system
	if (Utils.moveSetOnFS(aSet, aNewParentSet)) {
	    // update new set
	    aNewParentSet.addChildSet(aSet);
	    save(aNewParentSet);

	    // update old set
	    final Set aOldParentSet = getParentSet(aSet);
	    aOldParentSet.removeChildSet(aSet);
	    save(aOldParentSet);
	}

	// save or update set
	return super.save(aSet);
    }
}
