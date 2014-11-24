package at.ac.tuwien.media.master.webappapi.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.ac.tuwien.media.master.webappapi.model.Group;

public class GroupManager {
    private static GroupManager m_aInstance = new GroupManager();
    private static ReadWriteLock aRWLock;
    private static Collection<Group> s_aGroups;

    static {
	aRWLock = new ReentrantReadWriteLock();

	s_aGroups = new ArrayList<Group>();
	s_aGroups.add(new Group("group one", "description for group one"));
	s_aGroups.add(new Group("group two", "description for group 2"));
    }

    private GroupManager() {
    }

    public static GroupManager getInstance() {
	return m_aInstance;
    }

    @Nonnull
    public Collection<Group> all() {
	final Collection<Group> aGroups = new ArrayList<Group>();

	aRWLock.readLock().lock();

	aGroups.addAll(s_aGroups);

	aRWLock.readLock().unlock();

	return aGroups;
    }

    @Nonnull
    public Collection<Group> delete(@Nonnull final Group aGroup) {
	if (aGroup == null)
	    throw new NullPointerException("group");

	aRWLock.writeLock().lock();

	s_aGroups.remove(aGroup);

	aRWLock.writeLock().unlock();

	return all();
    }

    @Nonnull
    public Collection<Group> merge(@Nonnull final Group aGroup) {
	if (aGroup == null)
	    throw new NullPointerException("group");

	aRWLock.writeLock().lock();

	for (Group aOld : s_aGroups)
	    if (aOld.getId() == aOld.getId()) {
		aOld = aGroup;
		break;
	    }

	aRWLock.writeLock().unlock();

	return all();
    }

    @Nonnull
    public Group read(@Nullable final String sGroupname, @Nullable final String sPassword) {
	// XXX
	final Group aFoundGroup = null;

	return aFoundGroup;
    }

    @Nonnull
    public Collection<Group> save(@Nonnull final Group aGroup) {
	if (aGroup == null)
	    throw new NullPointerException("group");

	aRWLock.writeLock().lock();

	s_aGroups.add(aGroup);

	aRWLock.writeLock().unlock();

	return all();
    }
}
