package at.frohnwieser.mahut.webapp.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.el.ELContext;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.webapp.bean.Credentials;
import at.frohnwieser.mahut.webapp.page.EPage;
import at.frohnwieser.mahut.webappapi.db.model.Asset;
import at.frohnwieser.mahut.webappapi.db.model.HashTag;
import at.frohnwieser.mahut.webappapi.db.model.Set;
import at.frohnwieser.mahut.webappapi.db.model.User;

public class SessionUtils {
    private static SessionUtils s_aInstance = new SessionUtils();

    private SessionUtils() {
    }

    public static SessionUtils getInstance() {
	return s_aInstance;
    }

    private static FacesContext _getFacesContext() {
	return FacesContext.getCurrentInstance();
    }

    private static ExternalContext _getExternalContext() {
	return _getFacesContext().getExternalContext();
    }

    public boolean hasMessage() {
	return CollectionUtils.isNotEmpty(_getFacesContext().getMessageList());
    }

    private void _showMessage(@Nonnull final Severity aSeverity, @Nullable final String sSummary, @Nullable final String sDetail) {
	_getFacesContext().addMessage(null, new FacesMessage(aSeverity, sSummary, sDetail));
    }

    public void error(@Nullable final String sSummary, @Nullable final String sDetail) {
	_showMessage(FacesMessage.SEVERITY_ERROR, sSummary, sDetail);
    }

    public void fatal(@Nullable final String sSummary, @Nullable final String sDetail) {
	_showMessage(FacesMessage.SEVERITY_FATAL, sSummary, sDetail);
    }

    public void info(@Nullable final String sSummary, @Nullable final String sDetail) {
	_showMessage(FacesMessage.SEVERITY_INFO, sSummary, sDetail);
    }

    public void warn(@Nullable final String sSummary, @Nullable final String sDetail) {
	_showMessage(FacesMessage.SEVERITY_WARN, sSummary, sDetail);
    }

    public void invalidateSession() {
	final HttpSession aSession = (HttpSession) _getExternalContext().getSession(true);
	if (aSession != null)
	    aSession.invalidate();
    }

    public void destroyManagedBean(@Nonnull final String sName) {
	_getExternalContext().getSessionMap().put(sName, null);
    }

    @SuppressWarnings("unchecked")
    private <T> T _getManagedBean(@Nonnull final String sName, @Nonnull final Class<T> aClass) {
	final ELContext aELContext = _getFacesContext().getELContext();
	return (T) aELContext.getELResolver().getValue(aELContext, null, sName);
    }

    public static InetAddress getClientAddress(@Nonnull final HttpServletRequest aRequest) {
	try {
	    // if user is behind a proxy
	    String sIpAddress = aRequest.getHeader("X-FORWARDED-FOR");
	    if (StringUtils.isEmpty(sIpAddress) || sIpAddress.equals("null"))
		sIpAddress = aRequest.getRemoteAddr();

	    return InetAddress.getByName(sIpAddress);
	} catch (final UnknownHostException aUHException) {
	    aUHException.printStackTrace();
	}

	return null;
    }

    @Nullable
    public InetAddress getClientAddress() {
	return getClientAddress((HttpServletRequest) _getExternalContext().getRequest());
    }

    @Nullable
    public EPage getCurrentPage() {
	final String sCurrentViewId = _getFacesContext().getViewRoot().getViewId();

	return EPage.getFromPath(sCurrentViewId);
    }

    public Credentials getCredentials() {
	return _getManagedBean(Value.BEAN_CREDENTIALS, Credentials.class);
    }

    @Nullable
    public User getLoggedInUser() {
	final Credentials aCredentials = getCredentials();
	if (aCredentials != null)
	    return aCredentials.getUser();
	return null;
    }

    public void redirect(@Nullable final String sURL) {
	try {
	    if (StringUtils.isNotEmpty(sURL))
		_getExternalContext().redirect(sURL);
	} catch (final IOException aIOException) {
	}
    }

    @Nullable
    public String getRequestParameter(final String sRequestParameter) {
	final Map<String, String> aRequestParameterMap = _getExternalContext().getRequestParameterMap();
	return aRequestParameterMap.get(sRequestParameter);
    }

    @Nullable
    public boolean hasRequestParameter(final String sRequestParameter) {
	return getRequestParameter(sRequestParameter) != null;
    }

    @Nonnull
    public final static String getAsRequestParameter(final String sRequestParameter, final String sValue) {
	if (StringUtils.isNotEmpty(sRequestParameter) && StringUtils.isNotEmpty(sValue) && !sValue.equals("null"))
	    return "&" + sRequestParameter + "=" + sValue;

	return "";
    }

    @Nonnull
    public String getRequesetParametersForViewPage() {
	final Map<String, String> aRequestParameterMap = _getExternalContext().getRequestParameterMap();
	final StringBuilder aSB = new StringBuilder();

	aRequestParameterMap.entrySet().forEach(
	        aEntry -> {
		    final String aRequestParameter = aEntry.getKey();
		    if (aRequestParameter.equals(Asset.REQUEST_PARAMETER) || aRequestParameter.equals(HashTag.REQUEST_PARAMETER)
		            || aRequestParameter.equals(Set.REQUEST_PARAMETER) || aRequestParameter.equals(User.REQUEST_PARAMETER))
		        aSB.append(getAsRequestParameter(aRequestParameter, aEntry.getValue()));
	        });

	final String sRequesetParameters = aSB.toString().replaceFirst("&", "");
	if (StringUtils.isNotEmpty(sRequesetParameters))
	    return "?" + sRequesetParameters;

	return "";
    }
}
