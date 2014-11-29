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
    protected ReadWriteLock aRWLock;
    protected ConcurrentMap<Long, E> m_aEntries;

    protected AbstractManager(final String sDBCollectionName) {
	if (StringUtils.isEmpty(sDBCollectionName))
	    throw new NullPointerException("db collection name");

	m_aEntries = DBConnector.getInstance().getCollectionHashMap(sDBCollectionName);
	aRWLock = new ReentrantReadWriteLock();
    }

    @Override
    @Nonnull
    public Collection<E> all() {
	final Collection<E> aEntries = new ArrayList<E>();

	aRWLock.readLock().lock();

	aEntries.addAll(m_aEntries.values());

	aRWLock.readLock().unlock();

	return aEntries;
    }

    @Override
    @Nonnull
    public Collection<E> delete(@Nullable final E aEntry) {
	if (aEntry != null) {
	    aRWLock.writeLock().lock();

	    m_aEntries.remove(aEntry.getId());
	    DBConnector.getInstance().commit();

	    aRWLock.writeLock().unlock();
	}

	return all();
    }

    @Override
    @Nonnull
    public Collection<E> save(@Nullable final E aEntry) {
	if (aEntry != null) {
	    aRWLock.writeLock().lock();

	    m_aEntries.put(aEntry.getId(), aEntry);
	    DBConnector.getInstance().commit();

	    aRWLock.writeLock().unlock();
	}

	return all();
    }
}
