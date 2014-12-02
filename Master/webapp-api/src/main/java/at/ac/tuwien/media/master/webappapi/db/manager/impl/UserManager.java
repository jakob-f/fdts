package at.ac.tuwien.media.master.webappapi.db.manager.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.db.manager.AbstractManager;
import at.ac.tuwien.media.master.webappapi.db.model.ERole;
import at.ac.tuwien.media.master.webappapi.db.model.User;
import at.ac.tuwien.media.master.webappapi.util.Value;

public class UserManager extends AbstractManager<User> {
    private static UserManager m_aInstance = new UserManager();

    private UserManager() {
	super(Value.DB_COLLECTION_USERS);

	// XXX remove this
	if (f_aEntries.isEmpty())
	    save(new User("admin", "pass", "admin@mahut.com", ERole.ADMIN));
    }

    public static UserManager getInstance() {
	return m_aInstance;
    }

    @Nonnull
    public User get(@Nullable final String sUsername, @Nullable final String sPassword) {
	User aFoundUser = null;

	if (StringUtils.isNotEmpty(sUsername) && StringUtils.isNoneEmpty(sPassword)) {
	    aRWLock.readLock().lock();

	    aFoundUser = f_aEntries.values().stream().filter(aUser -> aUser.getName().equals(sUsername) && aUser.getPassword().equals(sPassword)).findFirst()
		    .orElse(null);

	    aRWLock.readLock().unlock();
	}

	return aFoundUser;
    }
}
