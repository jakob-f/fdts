package at.ac.tuwien.media.master.webappapi.db.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.ac.tuwien.media.master.webappapi.db.manager.AbstractManager;
import at.ac.tuwien.media.master.webappapi.db.model.Group;
import at.ac.tuwien.media.master.webappapi.db.model.Set;
import at.ac.tuwien.media.master.webappapi.db.model.User;
import at.ac.tuwien.media.master.webappapi.util.Value;

public class GroupManager extends AbstractManager<Group> {
    private static GroupManager m_aInstance = new GroupManager();

    private GroupManager() {
	super(Value.DB_COLLECTION_GROUPS);
    }

    public static GroupManager getInstance() {
	return m_aInstance;
    }

    @Nonnull
    public Collection<Group> allFor(@Nullable final User aUser) {
	if (aUser != null) {
	    aRWLock.readLock().lock();

	    final Collection<Group> aEntries = f_aEntries.values().stream().filter(aGroup -> aGroup.contains(aUser))
		    .collect(Collectors.toCollection(ArrayList::new));

	    aRWLock.readLock().unlock();

	    return aEntries;
	}

	return new ArrayList<Group>();
    }

    final boolean removeFromAll(@Nullable final Set aSet) {
	if (aSet != null) {
	    all().forEach(aGroup -> {
		if (aGroup.remove(aSet))
		    save(aGroup);
	    });

	    return true;
	}

	return false;
    }

    final boolean removeFromAll(@Nullable final User aUser) {
	if (aUser != null) {
	    all().forEach(aGroup -> {
		if (aGroup.remove(aUser))
		    save(aGroup);
	    });

	    return true;
	}

	return false;
    }
}
