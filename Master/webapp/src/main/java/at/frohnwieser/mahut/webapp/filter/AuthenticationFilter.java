package at.frohnwieser.mahut.webapp.filter;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;

import at.frohnwieser.mahut.webapp.bean.Credentials;
import at.frohnwieser.mahut.webapp.controller.NavigationController;
import at.frohnwieser.mahut.webapp.page.EPage;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.db.model.ERole;

public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(final ServletRequest aServletRequest, final ServletResponse aServletResponse, final FilterChain aFilterChain) throws IOException,
	    ServletException {
	final HttpServletRequest aRequest = (HttpServletRequest) aServletRequest;
	final HttpServletResponse aResponse = (HttpServletResponse) aServletResponse;

	final String sContextPath = aRequest.getContextPath();
	// remove context path and jsessionid
	final String sRequestURI = aRequest.getRequestURI();
	final int nSessionIdStart = sRequestURI.indexOf(";");
	final String sRequestSitePath = sRequestURI.substring(sContextPath.length(), nSessionIdStart == -1 ? sRequestURI.length() : nSessionIdStart);

	// filter resources
	if (sRequestSitePath.startsWith(Value.FOLDER_JAVAX) || sRequestSitePath.startsWith(Value.FOLDER_RESOURCES)) {

	    String sExtension = null;

	    if (sRequestSitePath.startsWith(Value.FOLDER_JAVAX))
		// is in the form xyz.css.xhtml
		sExtension = FilenameUtils.getExtension((FilenameUtils.getBaseName(sRequestSitePath)));
	    else
		sExtension = FilenameUtils.getExtension(sRequestSitePath);

	    // check extensions
	    if (sExtension.matches(Value.REGEX_ALLOWED_RESOURCES))
		aFilterChain.doFilter(aRequest, aResponse);
	}
	// special cases - continue filter chain
	else if (sRequestSitePath.startsWith(Value.FOLDER_ASSET) || sRequestSitePath.startsWith(Value.FOLDER_WS)
	        || sRequestSitePath.equals(Value.RES_NOT_FOUND))
	    aFilterChain.doFilter(aRequest, aResponse);
	// site pages
	else {
	    final Credentials aCredentials = (Credentials) aRequest.getSession().getAttribute(Value.BEAN_CREDENTIALS);
	    final EPage aRequestPage = EPage.getFromNameOrPath(sRequestSitePath);
	    EPage aRedirectPage = null;

	    // got page - check credentials
	    if (aRequestPage != null) {
		// user is logged in
		if (aCredentials != null && aCredentials.isLoggedIn()) {
		    // always redirect to last page shown
		    if (aRequestPage == EPage.ROOT || aRequestPage == EPage.HOME) {
			aRedirectPage = aCredentials.getLastPage();

			if (Arrays.asList(NavigationController.PAGES_FOOTER).contains(aRedirectPage) || aRedirectPage == EPage.ERROR
			        || aRedirectPage == EPage.VIEW)
			    aRedirectPage = EPage.START;
		    }
		    // check credentials for page
		    else if (!aCredentials.getUser().getRole().is(aRequestPage.getRole()))
			aRedirectPage = EPage.START;
		}
		// redirect to login
		else if (!ERole.PUBLIC.is(aRequestPage.getRole()))
		    aRedirectPage = EPage.HOME;
	    }
	    // invalid page
	    // this case is already covered by the rewrite url filter
	    else
		aRedirectPage = EPage.HOME;

	    // save last page
	    // XXX works only if logged in
	    if (aCredentials != null)
		aCredentials.setLastPage(aRedirectPage != null ? aRedirectPage : aRequestPage);

	    // correct url - "silently" forward to real page
	    if (aRedirectPage == null)
		aRequest.getRequestDispatcher(aRequestPage.getPath()).forward(aRequest, aResponse);
	    // send redirect
	    else
		aResponse.sendRedirect(sContextPath + "/" + aRedirectPage.getName());
	}
    }

    @Override
    public void destroy() {
	// this method intentionally left blank!
    }

    @Override
    public void init(final FilterConfig aFilterConfig) throws ServletException {
	// this method intentionally left blank!
    }
}
