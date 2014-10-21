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

import at.ac.tuwien.media.master.webappui.controller.LoginController;
import at.ac.tuwien.media.master.webappui.util.Value;

public class LoginFilter implements Filter {

    @Override
    public void doFilter(final ServletRequest aServletRequest, final ServletResponse aServletResponse, final FilterChain aFilterChain) throws IOException,
	    ServletException {
	final HttpServletRequest aRequest = (HttpServletRequest) aServletRequest;
	final HttpServletResponse aResponse = (HttpServletResponse) aServletResponse;

	final String sContextPath = aRequest.getContextPath();
	final String sRequestSitePath = StringUtils.removeStart(aRequest.getRequestURI(), sContextPath);

	final LoginController aLoginController = (LoginController) aRequest.getSession().getAttribute(Value.CONTROLLER_LOGIN);

	// redirect to login page if not logged in
	if (sRequestSitePath.startsWith(Value.FOLDER_PAGE) && (aLoginController == null || !aLoginController.isLoggedIn()))
	    aResponse.sendRedirect(sContextPath + Value.PAGE_LOGIN);
	else if (sRequestSitePath.equals(Value.FOLDER_ROOT))
	    aResponse.sendRedirect(sContextPath + Value.PAGE_START);

	aFilterChain.doFilter(aRequest, aResponse);
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
