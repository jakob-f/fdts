package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.CONTROLLER_NAVIGATION)
public class NavigationController implements Serializable {
    private final String PARAMETER_REDIRECT = "?faces-redirect=true";

    public String getLogin() {
	return Value.PAGE_LOGIN;
    }

    public String toLogin() {
	return getLogin() + PARAMETER_REDIRECT;
    }

    public String getProjects() {
	return Value.PAGE_PROJECTS;
    }

    public String toProjects() {
	return getProjects() + PARAMETER_REDIRECT;
    }

    public String getStart() {
	return Value.PAGE_START;
    }

    public String toStart() {
	return getStart() + PARAMETER_REDIRECT;
    }

    public String getUsers() {
	return Value.PAGE_USERS;
    }

    public String toUsers() {
	return getUsers() + PARAMETER_REDIRECT;
    }
}
