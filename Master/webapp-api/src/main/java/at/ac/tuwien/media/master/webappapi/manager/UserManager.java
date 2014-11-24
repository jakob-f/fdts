package at.ac.tuwien.media.master.webappapi.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.model.ERole;
import at.ac.tuwien.media.master.webappapi.model.User;

public class UserManager {
    private static UserManager m_aInstance = new UserManager();
    private static ReadWriteLock aRWLock;
    private static Collection<User> s_aUsers;

    static {
	aRWLock = new ReentrantReadWriteLock();

	s_aUsers = new ArrayList<User>();
	s_aUsers.add(new User("admin", "pass", "admin@mahut.com", ERole.ADMIN));
	s_aUsers.add(new User("user", "pass", "user@mahut.com", ERole.USER));
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

	aUsers.addAll(s_aUsers);

	aRWLock.readLock().unlock();

	return aUsers;
    }

    @Nonnull
    public Collection<User> delete(@Nonnull final User aUser) {
	if (aUser == null)
	    throw new NullPointerException("user");

	aRWLock.writeLock().lock();

	s_aUsers.remove(aUser);

	aRWLock.writeLock().unlock();

	return all();
    }

    @Nonnull
    public Collection<User> merge(@Nonnull final User aUser) {
	if (aUser == null)
	    throw new NullPointerException("user");

	aRWLock.writeLock().lock();

	for (User aOld : s_aUsers)
	    if (aOld.getId() == aOld.getId()) {
		aOld = aUser;
		break;
	    }

	aRWLock.writeLock().unlock();

	return all();
    }

    @Nonnull
    public User read(@Nullable final String sUsername, @Nullable final String sPassword) {
	User aFoundUser = null;

	if (StringUtils.isNotEmpty(sUsername) && StringUtils.isNoneEmpty(sPassword)) {
	    aRWLock.readLock().lock();

	    for (final User aUser : s_aUsers)
		if (aUser.getName().equals(sUsername) && aUser.getPassword().equals(sPassword)) {
		    aFoundUser = aUser;
		    break;
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

	s_aUsers.add(aUser);

	aRWLock.writeLock().unlock();

	return all();
    }
}
