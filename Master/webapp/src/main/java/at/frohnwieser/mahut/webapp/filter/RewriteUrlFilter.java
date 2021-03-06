package at.frohnwieser.mahut.webapp.filter;

import java.io.IOException;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.webapp.bean.Credentials;
import at.frohnwieser.mahut.webapp.page.EPage;
import at.frohnwieser.mahut.webapp.util.Value;

public class RewriteUrlFilter implements Filter {

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

	// exclude resources and rewritten paths
	if (sRequestSitePath.startsWith(Value.FOLDER_ASSET) || sRequestSitePath.startsWith(Value.FOLDER_JAVAX)
	        || sRequestSitePath.startsWith(Value.FOLDER_RESOURCES) || sRequestSitePath.startsWith(Value.FOLDER_WS)
	        || sRequestSitePath.equals(Value.RES_NOT_FOUND) || sRequestSitePath.equals(Value.RES_ROBOTS) || EPage.getFromName(sRequestSitePath) != null)
	    aFilterChain.doFilter(aRequest, aResponse);
	// try to get page from path
	else {
	    final EPage aRequestPage = EPage.getFromPath(sRequestSitePath);

	    // valid page
	    if (aRequestPage != null) {
		// get referrer page
		// / XXX better version?
		final URL aReferrerURL = new URL(aRequest.getHeader("Referer"));
		final String sReferrerPath = aReferrerURL.getPath();
		final String sPathWithoutContext = sContextPath.equals("") ? sReferrerPath : StringUtils.removePattern(sReferrerPath, ".*" + sContextPath);
		final EPage aReferrerPage = StringUtils.isNotEmpty(sReferrerPath) ? EPage.getFromNameOrPath(sPathWithoutContext) : null;

		// exclude redirects to current page - necessary for buttons ...
		if (aReferrerPage != null && ((aReferrerPage.equals(aRequestPage) || (aReferrerPage == EPage.ROOT && aRequestPage == EPage.HOME))))
		    aFilterChain.doFilter(aRequest, aResponse);
		// send redirect to rewritten page
		else {
		    aResponse.setStatus(301);
		    aResponse.sendRedirect(sContextPath + "/" + aRequestPage.getName());
		}
	    }
	    // page does not exist
	    else {
		EPage aRedirectPage = EPage.HOME;

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
    public void init(final FilterConfig aFilterConfig) throws ServletException {
	// this method intentionally left blank!
    }
}
