package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import at.ac.tuwien.media.master.webappui.util.EPage;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@ApplicationScoped
@ManagedBean(name = Value.CONTROLLER_NAVIGATION)
public class NavigationController implements Serializable {
    public final static EPage[] PAGES_NAV = new EPage[] { EPage.START, EPage.ASSETS, EPage.PROJECTS, EPage.GROUPS, EPage.USERS };
    public final static EPage[] PAGES_FOOTER = new EPage[] { EPage.HOME, EPage.ABOUT, EPage.LEGAL, EPage.CONTACT };
    private static final String PARAMETER_REDIRECT = "?faces-redirect=true";

    public EPage[] getNavPages() {
	return PAGES_NAV;
    }

    public EPage[] getFooterPages() {
	return PAGES_FOOTER;
    }

    public static String toAfterLogin() {
	return EPage.START.getName() + PARAMETER_REDIRECT;
    }

    public static String toAfterLogout() {
	return EPage.LOGIN.getName() + PARAMETER_REDIRECT;
    }
}