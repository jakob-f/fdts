package at.frohnwieser.mahut.webapp.controller;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import at.frohnwieser.mahut.webapp.page.EPage;
import at.frohnwieser.mahut.webapp.util.SessionUtils;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.db.model.ERole;

@SuppressWarnings("serial")
@ApplicationScoped
@Named(Value.CONTROLLER_NAVIGATION)
public class NavigationController implements Serializable {
    public final static EPage[] PAGES_NAV = new EPage[] { EPage.START, EPage.ASSETS, EPage.ASSETS2, EPage.USERS, EPage.GROUPS, EPage.SETS };
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
	SessionUtils.getInstance().redirect(EPage.VIEW.getName() + "?" + Value.REQUEST_PARAMETER_SEARCH + "=" + m_sSearchString.replaceAll("\\s+", "+"));
	m_sSearchString = "";
    }

    public static String toAfterLogin() {
	final EPage aCurrentPage = SessionUtils.getInstance().getCurrentPage();

	if (aCurrentPage != null)
	    if (aCurrentPage == EPage.HOME)
		SessionUtils.getInstance().redirect(EPage.START.getName());
	    else if (aCurrentPage == EPage.VIEW)
		SessionUtils.getInstance().redirect(EPage.VIEW.getName() + SessionUtils.getInstance().getRequesetParametersForViewPage());

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