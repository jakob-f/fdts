package at.frohnwieser.mahut.webappapi.db.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.frohnwieser.mahut.webappapi.db.manager.AbstractManager;
import at.frohnwieser.mahut.webappapi.db.model.Group;
import at.frohnwieser.mahut.webappapi.db.model.Set;
import at.frohnwieser.mahut.webappapi.db.model.User;
import at.frohnwieser.mahut.webappapi.util.Value;

public class GroupManager extends AbstractManager<Group> {
    private static GroupManager m_aInstance = new GroupManager();

    private GroupManager() {
	super(Value.DB_COLLECTION_GROUPS);
    }

    public static GroupManager getInstance() {
	return m_aInstance;
    }

    @Nonnull
    public Collection<Group> allFor(@Nullable final Set aSet) {
	if (aSet != null) {
	    m_aRWLock.readLock().lock();

	    final Collection<Group> aEntries = f_aEntries.values().stream().filter(aGroup -> aGroup.contains(aSet))
		    .collect(Collectors.toCollection(ArrayList::new));

	    m_aRWLock.readLock().unlock();

	    return aEntries;
	}

	return new ArrayList<Group>();
    }

    @Nonnull
    public Collection<Group> allFor(@Nullable final User aUser) {
	if (aUser != null) {
	    m_aRWLock.readLock().lock();

	    final Collection<Group> aEntries = f_aEntries.values().stream().filter(aGroup -> aGroup.contains(aUser))
		    .collect(Collectors.toCollection(ArrayList::new));

	    m_aRWLock.readLock().unlock();

	    return aEntries;
	}

	return new ArrayList<Group>();
    }

    @Nonnull
    public Collection<Group> allFor(@Nullable final User aUser, @Nullable final Set aSet) {
	if (aUser != null && aSet != null) {
	    m_aRWLock.readLock().lock();

	    final Collection<Group> aEntries = f_aEntries.values().stream().filter(aGroup -> aGroup.contains(aUser) && aGroup.contains(aSet))
		    .collect(Collectors.toCollection(ArrayList::new));

	    m_aRWLock.readLock().unlock();

	    return aEntries;
	}

	return new ArrayList<Group>();
    }

    @Nonnull
    public boolean isRead(@Nullable final User aUser, @Nullable final Set aSet) {
	boolean bIsRead = false;

	if (aUser != null && aSet != null) {
	    m_aRWLock.readLock().lock();

	    bIsRead = f_aEntries.values().stream().filter(aGroup -> aGroup.contains(aUser) && aGroup.contains(aSet) && aGroup.getPermissionFor(aSet).isRead())
		    .findFirst().orElse(null) != null;

	    m_aRWLock.readLock().unlock();
	}

	return bIsRead;
    }

    @Nonnull
    public boolean isWrite(@Nullable final User aUser, @Nullable final Set aSet) {
	boolean bIsWrite = false;

	if (aUser != null && aSet != null) {
	    m_aRWLock.readLock().lock();

	    bIsWrite = f_aEntries.values().stream()
		    .filter(aGroup -> aGroup.contains(aUser) && aGroup.contains(aSet) && aGroup.getPermissionFor(aSet).isWrite()).findFirst().orElse(null) != null;

	    m_aRWLock.readLock().unlock();
	}

	return bIsWrite;
    }

    final boolean removeFromAll(@Nullable final Set aSet) {
	if (aSet != null) {
	    all().stream().filter(aGroup -> aGroup.remove(aSet)).forEach(aGroup -> save(aGroup));

	    return true;
	}

	return false;
    }

    final boolean removeFromAll(@Nullable final User aUser) {
	if (aUser != null) {
	    all().stream().filter(aGroup -> aGroup.remove(aUser)).forEach(aGroup -> save(aGroup));

	    return true;
	}

	return false;
    }
}
