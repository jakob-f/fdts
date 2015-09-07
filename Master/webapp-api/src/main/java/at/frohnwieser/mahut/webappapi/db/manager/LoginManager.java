package at.frohnwieser.mahut.webappapi.db.manager;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.commons.TimeStampFactory;
import at.frohnwieser.mahut.webappapi.db.model.Login;
import at.frohnwieser.mahut.webappapi.db.model.User;
import at.frohnwieser.mahut.webappapi.util.Value;

public class LoginManager extends AbstractManager<Login> {
    private static LoginManager m_aInstance = new LoginManager();
    private String m_sErrorMessage;

    private LoginManager() {
	super(Value.DB_COLLECTION_LOGINS);
    }

    public static LoginManager getInstance() {
	return m_aInstance;
    }

    @Override
    public boolean delete(final Login aEntry) {
	return _deleteCommit(aEntry);
    }

    @Nonnull
    public Login _getOrNew(final User aUser, @Nonnull final InetAddress aUserIp) {
	final String sUserId = aUser != null ? aUser.getId() : null;
	Login aLogin = f_aEntries.values().stream().filter(aRefLogin -> aRefLogin.contains(sUserId, aUserIp)).findFirst().orElse(null);

	if (aLogin == null) {
	    aLogin = new Login(sUserId, aUserIp);
	    save(aLogin);
	}

	return aLogin;
    }

    public User login(@Nullable final String sUsername, @Nullable final String sPassword, @Nullable final InetAddress aClientAddress) {
	if (StringUtils.isNotEmpty(sUsername) && StringUtils.isNotEmpty(sPassword)) {
	    final User aUser = UserManager.getInstance().getByUsername(sUsername);

	    if (aClientAddress != null) {
		final Login aLogin = _getOrNew(aUser, aClientAddress);

		// is user blocked?
		if (aLogin.getAttempts() >= Value.MAX_LOGIN_ATTEMPTS)
		    if ((aLogin.getLastTimeStamp() + Value.LOCK_TIME) > TimeStampFactory.nowMillis()) {
			m_sErrorMessage = "You are blocked for "
			        + TimeUnit.MILLISECONDS.toSeconds(Value.LOCK_TIME - (TimeStampFactory.nowMillis() - aLogin.getLastTimeStamp())) + " seconds";

			return null;
		    } else
			aLogin.reset();

		aLogin.attempt();

		if (save(aLogin))
		    if (aUser != null && aUser.authenticate(sPassword)) {
			delete(aLogin);

			return aUser;
		    }
	    }
	}

	m_sErrorMessage = "wrong username and / or password";

	return null;
    }

    @Nullable
    public String getErrorMessage() {
	return m_sErrorMessage;
    }

    @Override
    public boolean save(final Login aEntry) {
	return _saveCommit(aEntry);
    }
}
