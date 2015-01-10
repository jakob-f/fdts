package at.frohnwieser.mahut.webappui.controller;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.webapp.util.SessionUtils;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.db.manager.impl.UserManager;
import at.frohnwieser.mahut.webappapi.db.model.User;
import at.frohnwieser.mahut.webappui.bean.Credentials;

@SuppressWarnings("serial")
@ViewScoped
@ManagedBean(name = Value.CONTROLLER_LOGIN)
public class LoginController implements Serializable {
    private String m_sUsername;
    private String m_sPassword;

    public LoginController() {
    }

    @Nullable
    public String doLogin() {
	if (StringUtils.isNotEmpty(m_sUsername) && StringUtils.isNotEmpty(m_sPassword)) {
	    final User aUser = UserManager.getInstance().get(m_sUsername, m_sPassword);

	    if (aUser != null) {
		SessionUtils.getInstance().getManagedBean(Value.BEAN_CREDENTIALS, Credentials.class).login(aUser);

		return NavigationController.toAfterLogin();
	    }
	}

	return null;
    }

    public String doLogout() {
	SessionUtils.getInstance().getManagedBean(Value.BEAN_CREDENTIALS, Credentials.class).clear();
	SessionUtils.getInstance().destroyManagedBean(Value.BEAN_CREDENTIALS);
	SessionUtils.getInstance().invalidateSession();

	return NavigationController.toAfterLogout();
    }

    @Nullable
    public String getUsername() {
	return m_sUsername;
    }

    public void setUsername(@Nullable final String sUsername) {
	this.m_sUsername = sUsername;
    }

    @Nullable
    public String getPassword() {
	return m_sPassword;
    }

    public void setPassword(@Nullable final String sPassword) {
	this.m_sPassword = sPassword;
    }
}
