package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import at.ac.tuwien.media.master.webapp.util.SessionUtils;
import at.ac.tuwien.media.master.webapp.util.Value;
import at.ac.tuwien.media.master.webappapi.db.model.ERole;
import at.ac.tuwien.media.master.webappui.page.EPage;

@SuppressWarnings("serial")
@ApplicationScoped
@ManagedBean(name = Value.CONTROLLER_NAVIGATION)
public class NavigationController implements Serializable {
    public final static EPage[] PAGES_NAV = new EPage[] { EPage.START, EPage.ASSETS, EPage.SETS, EPage.GROUPS, EPage.USERS };
    public final static EPage[] PAGES_FOOTER = new EPage[] { EPage.ABOUT, EPage.LEGAL, EPage.CONTACT };
    private static final String PARAMETER_REDIRECT = "?faces-redirect=true";

    public EPage[] getNavPages() {
	return PAGES_NAV;
    }

    public EPage[] getFooterPages() {
	return PAGES_FOOTER;
    }

    public static String toAfterLogin() {
	final EPage aCurrentPage = SessionUtils.getInstance().getCurrentPage();

	if (aCurrentPage != null && aCurrentPage != EPage.HOME)
	    return null;

	return EPage.START.getName() + PARAMETER_REDIRECT;
    }

    public static String toAfterLogout() {
	final EPage aCurrentPage = SessionUtils.getInstance().getCurrentPage();

	if (aCurrentPage != null && aCurrentPage.getRole() == ERole.PUBLIC)
	    return null;

	return EPage.HOME.getName() + PARAMETER_REDIRECT;
    }
}