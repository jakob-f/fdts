package at.ac.tuwien.media.master.webappapi.db.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.db.DBConnector;
import at.ac.tuwien.media.master.webappapi.db.model.ERole;
import at.ac.tuwien.media.master.webappapi.db.model.User;
import at.ac.tuwien.media.master.webappapi.util.Value;

public class UserManager {
    private static UserManager m_aInstance = new UserManager();
    private static ReadWriteLock aRWLock;
    private static ConcurrentMap<Long, User> s_aUsers;

    static {
	aRWLock = new ReentrantReadWriteLock();
	s_aUsers = DBConnector.getInstance().getCollectionHashMap(Value.DB_COLLECTION_USERS);

	if (s_aUsers.isEmpty()) {
	    final User aUser = new User("admin", "pass", "admin@mahut.com", ERole.ADMIN);
	    s_aUsers.put(aUser.getId(), aUser);
	    DBConnector.getInstance().commit();
	}
    }

    private UserManager() {
    }

    public static UserManager getInstance() {
	return m_aInstance;
    }

    @Nonnull
    public Collection<User> all() {
	final Collection<User> aUsers = new ArrayList<User>();

	aRWLock.readLock().lock();

	aUsers.addAll(s_aUsers.values());

	aRWLock.readLock().unlock();

	return aUsers;
    }

    @Nonnull
    public Collection<User> delete(@Nonnull final User aUser) {
	if (aUser == null)
	    throw new NullPointerException("user");

	aRWLock.writeLock().lock();

	s_aUsers.remove(aUser.getId());

	DBConnector.getInstance().commit();

	aRWLock.writeLock().unlock();

	return all();
    }

    @Nonnull
    public User read(@Nullable final String sUsername, @Nullable final String sPassword) {
	User aFoundUser = null;

	if (StringUtils.isNotEmpty(sUsername) && StringUtils.isNoneEmpty(sPassword)) {
	    aRWLock.readLock().lock();

	    for (final User aUser : s_aUsers.values()) {
		if (aUser.getName().equals(sUsername) && aUser.getPassword().equals(sPassword)) {
		    aFoundUser = aUser;
		    break;
		}
	    }

	    aRWLock.readLock().unlock();
	}

	return aFoundUser;
    }

    @Nonnull
    public Collection<User> save(@Nonnull final User aUser) {
	if (aUser == null)
	    throw new NullPointerException("user");

	aRWLock.writeLock().lock();

	s_aUsers.put(aUser.getId(), aUser);

	DBConnector.getInstance().commit();

	aRWLock.writeLock().unlock();

	return all();
    }
}
