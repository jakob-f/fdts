package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import at.ac.tuwien.media.master.webappapi.DataManager;
import at.ac.tuwien.media.master.webappapi.model.User;
import at.ac.tuwien.media.master.webappui.beans.Credentials;
import at.ac.tuwien.media.master.webappui.util.EPage;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.CONTROLLER_LOGIN)
public class LoginController implements Serializable {
    @ManagedProperty(value = "#{" + Value.BEAN_CREDENTIALS + "}")
    private Credentials credentials;

    private boolean m_bIsLoggedIn;

    public LoginController() {
    }

    @Nullable
    public String doLogin() {
	User aUser = null;
	if ((aUser = DataManager.getInstance().getValidUser(credentials.getUsername(), credentials.getPassword())) != null) {
	    m_bIsLoggedIn = true;
	    credentials.setRole(aUser.getRole());
	}

	return m_bIsLoggedIn ? EPage.START.getName() + Value.PARAMETER_REDIRECT : null;
    }

    public String doLogout() {
	m_bIsLoggedIn = false;
	credentials.setUsername("");
	credentials.setPassword("");
	credentials.setRole(null);
	FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

	return EPage.LOGIN.getName() + Value.PARAMETER_REDIRECT;
    }

    public boolean isLoggedIn() {
	return m_bIsLoggedIn;
    }

    public void setCredentials(final Credentials credentials) {
	this.credentials = credentials;
    }
}
