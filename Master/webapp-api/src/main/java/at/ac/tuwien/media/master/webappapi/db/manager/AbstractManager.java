package at.ac.tuwien.media.master.webappapi.db.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.commons.IHasId;
import at.ac.tuwien.media.master.webappapi.db.DBConnector;

public abstract class AbstractManager<E extends IHasId> implements IManager<E> {
    protected final ConcurrentMap<Long, E> f_aEntries;
    protected ReadWriteLock aRWLock;

    protected AbstractManager(final String sDBCollectionName) {
	if (StringUtils.isEmpty(sDBCollectionName))
	    throw new NullPointerException("db collection name");

	aRWLock = new ReentrantReadWriteLock();
	f_aEntries = DBConnector.getInstance().getCollectionHashMap(sDBCollectionName);
    }

    @Override
    @Nonnull
    public Collection<E> all() {
	final Collection<E> aEntries = new ArrayList<E>();

	aRWLock.readLock().lock();

	aEntries.addAll(f_aEntries.values());

	aRWLock.readLock().unlock();

	return aEntries;
    }

    public boolean contains(final long nId) {
	aRWLock.readLock().lock();

	final boolean bFound = f_aEntries.containsKey(nId);

	aRWLock.readLock().unlock();

	return bFound;
    }

    @Override
    @Nonnull
    public Collection<E> delete(@Nullable final E aEntry) {
	if (aEntry != null) {
	    aRWLock.writeLock().lock();

	    f_aEntries.remove(aEntry.getId());
	    DBConnector.getInstance().commit();

	    aRWLock.writeLock().unlock();
	}

	return all();
    }

    @Nullable
    public E get(final long nId) {
	aRWLock.readLock().lock();

	final E aFound = f_aEntries.get(nId);

	aRWLock.readLock().unlock();

	return aFound;
    }

    @Override
    @Nonnull
    public Collection<E> save(@Nullable final E aEntry) {
	if (aEntry != null) {
	    aRWLock.writeLock().lock();

	    f_aEntries.put(aEntry.getId(), aEntry);
	    DBConnector.getInstance().commit();

	    aRWLock.writeLock().unlock();
	}

	return all();
    }
}
