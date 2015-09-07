package at.frohnwieser.mahut.webappapi.db.manager;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.webappapi.db.model.ERole;
import at.frohnwieser.mahut.webappapi.db.model.User;
import at.frohnwieser.mahut.webappapi.util.Value;

public class UserManager extends AbstractManager<User> {
    private static UserManager m_aInstance = new UserManager();

    private UserManager() {
	super(Value.DB_COLLECTION_USERS);

	// create new user on startup
	if (f_aEntries.isEmpty())
	    _saveCommit(new User("admin", "pass", "admin@mahut.com", ERole.ADMIN));
    }

    public static UserManager getInstance() {
	return m_aInstance;
    }

    @Override
    public boolean delete(@Nullable final User aEntry) {
	if (aEntry != null && contains(aEntry))
	    if (GroupManager.getInstance()._removeFromAll(aEntry))
		return _deleteCommit(aEntry);
	return false;
    }

    @Nullable
    public User getByUsername(@Nullable final String sUsername) {
	if (StringUtils.isNotEmpty(sUsername)) {
	    final String sUsernameLC = sUsername.toLowerCase();
	    return f_aEntries.values().stream().filter(aUser -> aUser.getName().equals(sUsernameLC)).findFirst().orElse(null);
	}
	return null;
    }

    @Override
    public boolean save(@Nullable final User aEntry) {
	aEntry.setName(aEntry.getName().toLowerCase());
	return _saveCommit(aEntry);
    }
}
