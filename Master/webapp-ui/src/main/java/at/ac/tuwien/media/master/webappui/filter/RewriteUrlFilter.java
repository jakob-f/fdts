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

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappui.beans.Credentials;
import at.ac.tuwien.media.master.webappui.util.EPage;
import at.ac.tuwien.media.master.webappui.util.Value;

public class RewriteUrlFilter implements Filter {

    @Override
    public void doFilter(final ServletRequest aServletRequest, final ServletResponse aServletResponse, final FilterChain aFilterChain) throws IOException,
	    ServletException {
	final HttpServletRequest aRequest = (HttpServletRequest) aServletRequest;
	final HttpServletResponse aResponse = (HttpServletResponse) aServletResponse;

	final String sContextPath = aRequest.getContextPath();
	final String sRequestSitePath = StringUtils.removeStart(aRequest.getRequestURI(), sContextPath);

	// exclude resources and rewritten paths
	if (sRequestSitePath.startsWith(Value.FOLDER_JAVAX) || sRequestSitePath.startsWith(Value.FOLDER_RESOURCES)
	        || sRequestSitePath.startsWith(Value.FOLDER_ASSET) || sRequestSitePath.equals(Value.RES_NOT_FOUND)
	        || EPage.getFromName(sRequestSitePath) != null)
	    aFilterChain.doFilter(aRequest, aResponse);
	// try to get page from path
	else {
	    final EPage aRequestPage = EPage.getFromPath(sRequestSitePath);

	    // valid page
	    if (aRequestPage != null) {
		final String sReferrer = aRequest.getHeader("Referer");
		final EPage aReferrerPage = StringUtils.isNotEmpty(sReferrer) ? EPage.getFromNameOrPath(StringUtils.removePattern(sReferrer,
		        ".*" + aRequest.getContextPath())) : null;

		// exclude redirects to current page - necessary for buttons ...
		if (aReferrerPage.equals(aRequestPage) || (aReferrerPage == EPage.ROOT && aRequestPage == EPage.LOGIN))
		    aFilterChain.doFilter(aRequest, aResponse);
		// send redirect to rewritten page
		else {
		    aResponse.setStatus(301);
		    aResponse.sendRedirect(sContextPath + "/" + aRequestPage.getName());
		}
	    }
	    // page does not exist
	    else {
		EPage aRedirectPage = EPage.LOGIN;

		// try to open last shown page
		final Credentials aCredentials = (Credentials) aRequest.getSession().getAttribute(Value.BEAN_CREDENTIALS);
		if (aCredentials != null) {
		    final EPage aLastPage = aCredentials.getLastPage();
		    if (aLastPage != null)
			aRedirectPage = aLastPage;
		}

		aResponse.sendRedirect(sContextPath + "/" + aRedirectPage.getName());
	    }
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
