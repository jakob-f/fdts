package at.frohnwieser.mahut.webapp.controller;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.frohnwieser.mahut.webapp.bean.Credentials;
import at.frohnwieser.mahut.webapp.util.SessionUtils;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.db.manager.LoginManager;
import at.frohnwieser.mahut.webappapi.db.model.User;

@SuppressWarnings("serial")
@SessionScoped
@Named(Value.CONTROLLER_LOGIN)
public class LoginController implements Serializable {
    @Inject
    private Credentials m_aCredentials;

    private String m_sUsername;
    private String m_sPassword;

    @Nullable
    public String doLogin() {
	final LoginManager aLoginManager = LoginManager.getInstance();
	final User aUser = aLoginManager.login(m_sUsername, m_sPassword, SessionUtils.getInstance().getClientAddress());

	if (aUser != null) {
	    m_aCredentials.login(aUser);
	    return NavigationController.toAfterLogin();
	}
	SessionUtils.getInstance().error(aLoginManager.getErrorMessage(), "");
	return null;
    }

    public String doLogout() {
	m_aCredentials.clear();
	SessionUtils.getInstance().destroyManagedBean(Value.BEAN_CREDENTIALS);
	SessionUtils.getInstance().invalidateSession();

	return NavigationController.toAfterLogout();
    }

    @Nullable
    public String getUsername() {
	return m_sUsername;
    }

    public void setUsername(@Nullable final String sUsername) {
	m_sUsername = sUsername;
    }

    @Nullable
    public String getPassword() {
	return m_sPassword;
    }

    public void setPassword(@Nullable final String sPassword) {
	m_sPassword = sPassword;
    }
}
