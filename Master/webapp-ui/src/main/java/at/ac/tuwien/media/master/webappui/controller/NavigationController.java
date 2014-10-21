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

    public String toLogin() {
	return Value.PAGE_LOGIN + PARAMETER_REDIRECT;
    }

    public String toProjects() {
	return Value.PAGE_PROJECTS + PARAMETER_REDIRECT;
    }

    public String toStart() {
	return Value.PAGE_START + PARAMETER_REDIRECT;
    }

    public String toUsers() {
	return Value.PAGE_USERS + PARAMETER_REDIRECT;
    }
}
