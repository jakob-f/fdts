package at.frohnwieser.mahut.webappapi.db.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.commons.IHasId;
import at.frohnwieser.mahut.commons.IValidate;
import at.frohnwieser.mahut.webappapi.db.DBConnector;

public abstract class AbstractManager<E extends IHasId & IValidate> {
    protected final ConcurrentMap<Long, E> f_aEntries;

    protected AbstractManager(final String sDBCollectionName) {
	if (StringUtils.isEmpty(sDBCollectionName))
	    throw new NullPointerException("db collection name");

	f_aEntries = DBConnector.getInstance().getCollectionHashMap(sDBCollectionName);
    }

    @Nullable
    public Collection<E> all() {
	return new ArrayList<E>(f_aEntries.values());
    }

    public boolean commit() {
	DBConnector.getInstance().commit();

	return true;
    }

    public boolean contains(@Nullable final E aEntry) {
	if (aEntry != null)
	    return f_aEntries.containsKey(aEntry.getId());

	return false;
    }

    /**
     * does always commit
     */
    public abstract boolean delete(@Nullable final E aEntry);

    /**
     * does always commit
     */
    protected boolean _deleteCommit(@Nullable final E aEntry) {
	if (_internalDelete(aEntry))
	    return commit();

	return false;
    }

    @Nullable
    public E get(final long nId) {
	return f_aEntries.get(nId);
    }

    /**
     * does not commit
     */
    protected boolean _internalDelete(@Nullable final E aEntry) {
	if (aEntry != null)
	    return f_aEntries.remove(aEntry.getId()) != null;

	return false;
    }

    /**
     * does not commit
     */
    protected boolean _internalSave(@Nullable final E aEntry) {
	if (aEntry != null) {
	    f_aEntries.put(aEntry.getId(), aEntry);

	    return true;
	}

	return false;
    }

    /**
     * does always commit
     */
    public abstract boolean save(@Nullable final E aEntry);

    /**
     * does always commit
     */
    protected boolean _saveCommit(@Nullable final E aEntry) {
	if (_internalSave(aEntry))
	    return commit();

	return false;
    }

    public boolean rollback() {
	DBConnector.getInstance().rollback();

	return true;
    }
}
