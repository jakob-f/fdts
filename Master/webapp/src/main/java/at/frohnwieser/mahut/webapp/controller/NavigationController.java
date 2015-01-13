package at.frohnwieser.mahut.webapp.controller;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.webapp.page.EPage;
import at.frohnwieser.mahut.webapp.util.SessionUtils;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.db.model.ERole;

@SuppressWarnings("serial")
@ApplicationScoped
@ManagedBean(name = Value.CONTROLLER_NAVIGATION)
public class NavigationController implements Serializable {
    public final static EPage[] PAGES_NAV = new EPage[] { EPage.START, EPage.ASSETS, EPage.SETS, EPage.GROUPS, EPage.USERS };
    public final static EPage[] PAGES_FOOTER = new EPage[] { EPage.ABOUT, EPage.LEGAL, EPage.CONTACT };
    private static final String PARAMETER_REDIRECT = "?faces-redirect=true&amp;includeViewParams=true";
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
	if (StringUtils.isNotEmpty(m_sSearchString))
	    SessionUtils.getInstance().redirect(EPage.VIEW.getName() + "?" + Value.REQUEST_PARAMETER_SEARCH + "=" + m_sSearchString.replaceAll("\\s+", "+"));

	m_sSearchString = "";
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