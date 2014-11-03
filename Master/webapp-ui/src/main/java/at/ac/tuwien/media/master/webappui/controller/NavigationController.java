package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import at.ac.tuwien.media.master.webappui.util.EPage;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.CONTROLLER_NAVIGATION)
public class NavigationController implements Serializable {
    private final String PARAMETER_REDIRECT = "?faces-redirect=true";

    public String getGroups() {
	return EPage.GROUPS.getPath();
    }

    public String toGroups() {
	return getGroups() + PARAMETER_REDIRECT;
    }

    public String getLogin() {
	return EPage.LOGIN.getPath();
    }

    public String toLogin() {
	return getLogin() + PARAMETER_REDIRECT;
    }

    public String getProjects() {
	return EPage.PROJECTS.getPath();
    }

    public String toProjects() {
	return getProjects() + PARAMETER_REDIRECT;
    }

    public String getStart() {
	return EPage.START.getPath();
    }

    public String toStart() {
	return getStart() + PARAMETER_REDIRECT;
    }

    public String getUsers() {
	return EPage.USERS.getPath();
    }

    public String toUsers() {
	return getUsers() + PARAMETER_REDIRECT;
    }
}
