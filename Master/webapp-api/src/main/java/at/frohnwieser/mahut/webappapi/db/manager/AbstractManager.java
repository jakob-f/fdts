package at.frohnwieser.mahut.webappapi.db.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.commons.IHasId;
import at.frohnwieser.mahut.commons.IValidate;
import at.frohnwieser.mahut.webappapi.db.DBConnector;

public abstract class AbstractManager<E extends IHasId & IValidate> {
    protected final ConcurrentMap<Long, E> f_aEntries;
    protected ReadWriteLock m_aRWLock;

    protected AbstractManager(final String sDBCollectionName) {
	if (StringUtils.isEmpty(sDBCollectionName))
	    throw new NullPointerException("db collection name");

	m_aRWLock = new ReentrantReadWriteLock();
	f_aEntries = DBConnector.getInstance().getCollectionHashMap(sDBCollectionName);
    }

    @Nullable
    public Collection<E> all() {
	Collection<E> aEntries = null;

	m_aRWLock.readLock().lock();

	try {
	    aEntries = new ArrayList<E>(f_aEntries.values());
	} catch (final Exception aException) {
	} finally {
	    m_aRWLock.readLock().unlock();
	}

	return aEntries;
    }

    public boolean contains(@Nullable final E aEntry) {
	boolean bFound = false;

	if (aEntry != null) {
	    m_aRWLock.readLock().lock();

	    try {
		bFound = f_aEntries.containsKey(aEntry.getId());
	    } catch (final Exception aException) {
	    } finally {
		m_aRWLock.readLock().unlock();
	    }
	}

	return bFound;
    }

    public boolean delete(@Nullable final E aEntry) {
	boolean bRet = false;

	if (aEntry != null) {
	    m_aRWLock.writeLock().lock();

	    try {
		f_aEntries.remove(aEntry.getId());
		DBConnector.getInstance().commit();
		bRet = true;
	    } catch (final Exception aException) {
	    } finally {
		m_aRWLock.writeLock().unlock();
	    }
	}

	return bRet;
    }

    @Nullable
    public E get(final long nId) {
	m_aRWLock.readLock().lock();

	final E aFound = f_aEntries.get(nId);

	m_aRWLock.readLock().unlock();

	return aFound;
    }

    public boolean save(@Nullable final E aEntry) {
	boolean bRet = false;

	if (aEntry != null && aEntry.isValid()) {
	    m_aRWLock.writeLock().lock();

	    try {
		f_aEntries.put(aEntry.getId(), aEntry);
		DBConnector.getInstance().commit();
		bRet = true;
	    } catch (final Exception aException) {
	    } finally {
		m_aRWLock.writeLock().unlock();
	    }
	}

	return bRet;
    }
}
