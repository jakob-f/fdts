package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import at.ac.tuwien.media.master.webappapi.DataManager;
import at.ac.tuwien.media.master.webappapi.model.User;
import at.ac.tuwien.media.master.webappui.beans.Credentials;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.CONTROLLER_LOGIN)
public class LoginController implements Serializable {
    @ManagedProperty(value = "#{" + Value.CONTROLLER_NAVIGATION + "}")
    private NavigationController navigationController;
    @ManagedProperty(value = "#{" + Value.BEAN_CREDENTIALS + "}")
    private Credentials credentials;

    private boolean m_bIsLoggedIn;

    public LoginController() {
    }

    public String doLogin() {
	User aUser = null;
	if ((aUser = DataManager.getInstance().getValidUser(credentials.getUsername(), credentials.getPassword())) != null) {
	    m_bIsLoggedIn = true;
	    credentials.setRoles(aUser.getRoles());
	}

	return m_bIsLoggedIn ? navigationController.toStart() : "";
    }

    public String doLogout() {
	m_bIsLoggedIn = false;
	credentials.setUsername("");
	credentials.setPassword("");

	return navigationController.toLogin();
    }

    public boolean isLoggedIn() {
	return m_bIsLoggedIn;
    }

    public void setNavigationController(final NavigationController navigationController) {
	this.navigationController = navigationController;
    }

    public void setCredentials(final Credentials credentials) {
	this.credentials = credentials;
    }
}
