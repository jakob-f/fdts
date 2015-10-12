package at.frohnwieser.mahut.webapp.controller;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import at.frohnwieser.mahut.webapp.page.EPage;
import at.frohnwieser.mahut.webapp.util.SessionUtils;
import at.frohnwieser.mahut.webappapi.db.model.ERole;
import at.frohnwieser.mahut.webappapi.db.model.HashTag;
import at.frohnwieser.mahut.webappapi.db.model.User;

@SuppressWarnings("serial")
@ApplicationScoped
@Named
public class NavigationController implements Serializable {
    public final static EPage[] PAGES_NAV = new EPage[] { EPage.ASSETS, EPage.USERS, EPage.GROUPS, EPage.ASSETS2, EPage.SETS };
    public final static EPage[] PAGES_FOOTER = new EPage[] { EPage.ABOUT, EPage.LEGAL, EPage.CONTACT };
    // private static final String PARAMETER_REDIRECT =
    // "?faces-redirect=true&amp;includeViewParams=true";
    private static final String PARAMETER_REDIRECT = "?faces-redirect=true&amp";
    private String m_sSearchString;

    public EPage[] getNavPages() {
	return PAGES_NAV;
    }

    public EPage[] getFooterPages() {
	return PAGES_FOOTER;
    }

    @Nullable
    public String getSearchString() {
	return m_sSearchString;
    }

    public void setSearchString(@Nullable final String sSearchString) {
	m_sSearchString = sSearchString;
    }

    @Nullable
    public void doSearch() {
	SessionUtils.getInstance().redirect(EPage.VIEW.getName() + "?" + HashTag.REQUEST_PARAMETER + "=" + m_sSearchString.replaceAll("\\s+", "+"));
	m_sSearchString = "";
    }

    public static String toAfterLogin() {
	final SessionUtils aSessionUtils = SessionUtils.getInstance();
	final EPage aCurrentPage = aSessionUtils.getCurrentPage();

	if (aCurrentPage != null) {
	    if (aCurrentPage == EPage.HOME) {
		final User aLoggedInUser = aSessionUtils.getLoggedInUser();
		final ERole eUserRole = aLoggedInUser.getRole();
		if (eUserRole == ERole.ADMIN)
		    aSessionUtils.redirect(EPage.ASSETS.getName());
		else
		    aSessionUtils.redirect(EPage.VIEW.getName() + "?u=" + aLoggedInUser.getName());
	    } else if (aCurrentPage == EPage.VIEW)
		aSessionUtils.redirect(EPage.VIEW.getName() + aSessionUtils.getRequesetParametersForViewPage());
	}

	return null;
    }

    public static String toAfterLogout() {
	final EPage aCurrentPage = SessionUtils.getInstance().getCurrentPage();

	if (aCurrentPage != null)
	    if (aCurrentPage == EPage.VIEW)
		SessionUtils.getInstance().redirect(EPage.VIEW.getName() + SessionUtils.getInstance().getRequesetParametersForViewPage());
	    else if (aCurrentPage.getRole() != ERole.PUBLIC)
		return EPage.HOME.getName() + PARAMETER_REDIRECT;

	return null;
    }
}