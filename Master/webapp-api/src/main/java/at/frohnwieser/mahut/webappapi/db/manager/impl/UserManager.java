package at.frohnwieser.mahut.webappapi.db.manager.impl;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.webappapi.db.manager.AbstractManager;
import at.frohnwieser.mahut.webappapi.db.model.ERole;
import at.frohnwieser.mahut.webappapi.db.model.User;
import at.frohnwieser.mahut.webappapi.util.Value;

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

    @Override
    public boolean delete(@Nullable final User aEntry) {
	if (aEntry != null && contains(aEntry))
	    if (GroupManager.getInstance().removeFromAll(aEntry))
		return super.delete(aEntry);

	return false;
    }

    @Nullable
    public User get(@Nullable final String sUsername, @Nullable final String sPassword) {
	User aFoundUser = null;

	if (StringUtils.isNotEmpty(sUsername) && StringUtils.isNoneEmpty(sPassword)) {
	    m_aRWLock.readLock().lock();

	    aFoundUser = f_aEntries.values().stream().filter(aUser -> aUser.getName().equals(sUsername) && aUser.authenticate(sPassword)).findFirst()
		    .orElse(null);

	    m_aRWLock.readLock().unlock();
	}

	return aFoundUser;
    }
}
