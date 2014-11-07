package at.ac.tuwien.media.master.webappui.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.model.ERole;
import at.ac.tuwien.media.master.webappui.beans.Credentials;
import at.ac.tuwien.media.master.webappui.controller.LoginController;
import at.ac.tuwien.media.master.webappui.util.EPage;
import at.ac.tuwien.media.master.webappui.util.Value;

public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(final ServletRequest aServletRequest, final ServletResponse aServletResponse, final FilterChain aFilterChain) throws IOException,
	    ServletException {
	final HttpServletRequest aRequest = (HttpServletRequest) aServletRequest;
	final HttpServletResponse aResponse = (HttpServletResponse) aServletResponse;

	final String sContextPath = aRequest.getContextPath();
	final String sRequestSitePath = StringUtils.removeStart(aRequest.getRequestURI(), sContextPath);

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
	else if (sRequestSitePath.equals(Value.RES_NOT_FOUND) || sRequestSitePath.startsWith(Value.FOLDER_ASSET))
	    aFilterChain.doFilter(aRequest, aResponse);
	// site pages
	else {
	    final EPage aRequestPage = EPage.getFromNameOrPath(sRequestSitePath);
	    EPage aRedirectPage = null;

	    // got page - check credentials
	    if (aRequestPage != null) {
		final LoginController aLoginController = (LoginController) aRequest.getSession().getAttribute(Value.CONTROLLER_LOGIN);

		// user is logged in
		if (aLoginController != null && aLoginController.isLoggedIn()) {
		    // XXX always redirect to last page shown
		    if (aRequestPage.equals(EPage.ROOT) || aRequestPage.equals(EPage.LOGIN))
			aRedirectPage = EPage.START;
		    // check credentials for page
		    else {
			final Credentials aCredentials = (Credentials) aRequest.getSession().getAttribute(Value.BEAN_CREDENTIALS);

			if (!aCredentials.getRole().is(aRequestPage.getRole()))
			    aRedirectPage = EPage.START;
		    }
		}
		// redirect to login
		else if (!ERole.PUBLIC.is(aRequestPage.getRole()))
		    aRedirectPage = EPage.LOGIN;
	    }
	    // invalid page
	    // this case is already covered by the rewrite url filter
	    else
		aRedirectPage = EPage.LOGIN;

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
    public void init(final FilterConfig arg0) throws ServletException {
	// this method intentionally left blank!
    }
}
