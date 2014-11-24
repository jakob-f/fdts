package at.ac.tuwien.media.master.webappapi.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;

import at.ac.tuwien.media.master.webappapi.model.Set;

public class SetManager {
    private static SetManager m_aInstance = new SetManager();
    private static ReadWriteLock aRWLock;
    private static Collection<Set> s_aSets;

    static {
	aRWLock = new ReentrantReadWriteLock();

	s_aSets = new ArrayList<Set>();
	for (int i = 0; i < 25; i++)
	    s_aSets.add(new Set("Set " + i, "set " + i));
    }

    private SetManager() {
    }

    public static SetManager getInstance() {
	return m_aInstance;
    }

    @Nonnull
    public Collection<Set> all() {
	final Collection<Set> aSets = new ArrayList<Set>();

	aRWLock.readLock().lock();

	aSets.addAll(s_aSets);

	aRWLock.readLock().unlock();

	return aSets;
    }

    @Nonnull
    public Collection<Set> delete(@Nonnull final Set aSet) {
	if (aSet == null)
	    throw new NullPointerException("set");

	aRWLock.writeLock().lock();

	s_aSets.remove(aSet);

	aRWLock.writeLock().unlock();

	return all();
    }

    @Nonnull
    public Collection<Set> merge(@Nonnull final Set aSet) {
	if (aSet == null)
	    throw new NullPointerException("set");

	aRWLock.writeLock().lock();

	for (Set aOld : s_aSets)
	    if (aOld.getId() == aOld.getId()) {
		aOld = aSet;
		break;
	    }

	aRWLock.writeLock().unlock();

	return all();
    }

    @Nonnull
    public Collection<Set> read(@Nonnull final Set aSet) {
	// TODO
	return null;
    }

    @Nonnull
    public Collection<Set> save(@Nonnull final Set aSet) {
	if (aSet == null)
	    throw new NullPointerException("set");

	aRWLock.writeLock().lock();

	s_aSets.add(aSet);

	aRWLock.writeLock().unlock();

	return all();
    }

    // XXX
    // @Nonnull
    // public Collection<Set> getReadWriteSetsForUser(@Nonnull final User aUser)
    // {
    // if (aUser == null)
    // throw new NullPointerException("user");
    //
    // final Collection<Set> aSets = new ArrayList<Set>();
    //
    // aRWLock.readLock().lock();
    //
    // for (final Set aSet : s_aSets)
    // for (final Permission aPermission : aSet.getPermissions())
    // if (aPermission.isWrite())
    // aSets.add(aSet);
    //
    // aRWLock.readLock().unlock();
    //
    // return aSets;
    // }
}
