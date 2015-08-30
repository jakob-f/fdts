package at.frohnwieser.mahut.webapp.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.webapp.bean.Credentials;
import at.frohnwieser.mahut.webapp.controller.AssetsController;
import at.frohnwieser.mahut.webapp.controller.ContactFormController;
import at.frohnwieser.mahut.webapp.controller.FileUploadController;
import at.frohnwieser.mahut.webapp.controller.GroupsController;
import at.frohnwieser.mahut.webapp.controller.HashTagsController;
import at.frohnwieser.mahut.webapp.controller.LoginController;
import at.frohnwieser.mahut.webapp.controller.NavigationController;
import at.frohnwieser.mahut.webapp.controller.ResourcesController;
import at.frohnwieser.mahut.webapp.controller.SetsController;
import at.frohnwieser.mahut.webapp.controller.UsersController;
import at.frohnwieser.mahut.webapp.controller.WallpaperController;
import at.frohnwieser.mahut.webapp.page.EPage;
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

    @Nullable
    public User getLoggedInUser() {
	final Credentials aCredentials = _getManagedBean(Value.BEAN_CREDENTIALS, Credentials.class);
	if (aCredentials != null)
	    return aCredentials.getUser();

	return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T _getManagedBean(@Nonnull final String sName, @Nonnull final Class<T> aClass) {
	final FacesContext aFacesContext = _getFacesContext();

	return (T) aFacesContext.getELContext().getELResolver().getValue(aFacesContext.getELContext(), null, sName);
    }

    public Credentials getCredentials() {
	return _getManagedBean(Value.BEAN_CREDENTIALS, Credentials.class);
    }

    public AssetsController getAssetsController() {
	return _getManagedBean(Value.CONTROLLER_ASSETS, AssetsController.class);
    }

    public ContactFormController getContactFormController() {
	return _getManagedBean(Value.CONTROLLER_CONTACT, ContactFormController.class);
    }

    public FileUploadController getFileUploadController() {
	return _getManagedBean(Value.CONTROLLER_FILE_UPLOAD, FileUploadController.class);
    }

    public GroupsController getGroupsController() {
	return _getManagedBean(Value.CONTROLLER_GROUPS, GroupsController.class);
    }

    public HashTagsController getHashTagsController() {
	return _getManagedBean(Value.CONTROLLER_HASHTAGS, HashTagsController.class);
    }

    public LoginController getLoginController() {
	return _getManagedBean(Value.CONTROLLER_LOGIN, LoginController.class);
    }

    public NavigationController getNavigationController() {
	return _getManagedBean(Value.CONTROLLER_NAVIGATION, NavigationController.class);
    }

    public ResourcesController getResourcesController() {
	return _getManagedBean(Value.CONTROLLER_RESOURCES, ResourcesController.class);
    }

    public SetsController getSetsController() {
	return _getManagedBean(Value.CONTROLLER_SETS, SetsController.class);
    }

    public UsersController getUsersController() {
	return _getManagedBean(Value.CONTROLLER_USERS, UsersController.class);
    }

    public WallpaperController getWallpaperController() {
	return _getManagedBean(Value.CONTROLLER_WALLPAPER, WallpaperController.class);
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
		    if (aRequestParameter.equals(Value.REQUEST_PARAMETER_ASSET) || aRequestParameter.equals(Value.REQUEST_PARAMETER_SEARCH)
		            || aRequestParameter.equals(Value.REQUEST_PARAMETER_SET) || aRequestParameter.equals(Value.REQUEST_PARAMETER_USER))
		        aSB.append(getAsRequestParameter(aRequestParameter, aEntry.getValue()));
	        });

	final String sRequesetParameters = aSB.toString().replaceFirst("&", "");
	if (StringUtils.isNotEmpty(sRequesetParameters))
	    return "?" + sRequesetParameters;

	return "";
    }
}
