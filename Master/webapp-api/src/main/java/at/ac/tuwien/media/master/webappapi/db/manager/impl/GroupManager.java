package at.ac.tuwien.media.master.webappapi.db.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import at.ac.tuwien.media.master.webappapi.db.manager.AbstractManager;
import at.ac.tuwien.media.master.webappapi.db.model.Group;
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
    public Collection<Group> allForUser(@Nonnull final User aUser) {
	if (aUser != null) {
	    aRWLock.readLock().lock();

	    final Collection<Group> aEntries = f_aEntries.values().stream().filter(aGroup -> aGroup.containsUser(aUser))
		    .collect(Collectors.toCollection(ArrayList::new));

	    aRWLock.readLock().unlock();

	    return aEntries;
	}

	return new ArrayList<Group>();
    }
}
