package at.frohnwieser.mahut.webapp.ws;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.MTOM;

import org.apache.commons.io.IOUtils;

import at.frohnwieser.mahut.webapp.util.SessionUtils;
import at.frohnwieser.mahut.webapp.ws.data.AssetData;
import at.frohnwieser.mahut.webapp.ws.data.SetData;
import at.frohnwieser.mahut.webappapi.db.manager.AssetManager;
import at.frohnwieser.mahut.webappapi.db.manager.GroupManager;
import at.frohnwieser.mahut.webappapi.db.manager.LoginManager;
import at.frohnwieser.mahut.webappapi.db.manager.SetManager;
import at.frohnwieser.mahut.webappapi.db.model.Asset;
import at.frohnwieser.mahut.webappapi.db.model.Set;
import at.frohnwieser.mahut.webappapi.db.model.User;
import at.frohnwieser.mahut.webappapi.fs.manager.FSManager;

@MTOM
@WebService(endpointInterface = "at.frohnwieser.mahut.webapp.ws.IWSEndpoint")
public class WSEndpointImpl implements IWSEndpoint {
    String HEADER_USERNAME = "username";
    String HEADER_PASSWORD = "password";

    @Resource
    WebServiceContext aWSContext;

    @Nullable
    private InetAddress _getClientIP(@Nonnull final MessageContext aMessageContext) {
	InetAddress aInetAddress = null;
	final HttpServletRequest aHttpServletRequest = (HttpServletRequest) aMessageContext.get(MessageContext.SERVLET_REQUEST);

	// WS runs in webapp
	if (aHttpServletRequest != null)
	    aInetAddress = SessionUtils.getClientAddress(aHttpServletRequest);
	// XXX WS runs stand alone (used only for tests)
	if (aInetAddress == null)
	    try {
		aInetAddress = InetAddress.getByName("localhost");
	    } catch (final UnknownHostException aUHException) {
		aUHException.printStackTrace();
	    }

	return aInetAddress;
    }

    @Nonnull
    private User _authenticate() throws FailedLoginException {
	final MessageContext aMessageContext = aWSContext.getMessageContext();
	final Map<?, ?> aHeaders = (Map<?, ?>) aMessageContext.get(MessageContext.HTTP_REQUEST_HEADERS);
	final List<?> aUsers = (List<?>) aHeaders.get(HEADER_USERNAME);
	final List<?> aPasswords = (List<?>) aHeaders.get(HEADER_PASSWORD);

	if (aUsers != null && aUsers.size() == 1 && aPasswords != null && aPasswords.size() == 1) {
	    final String sUsername = aUsers.get(0).toString();
	    final String sPassword = aPasswords.get(0).toString();

	    final LoginManager aLoginManager = LoginManager.getInstance();
	    final User aUser = aLoginManager.login(sUsername, sPassword, _getClientIP(aMessageContext));
	    if (aUser != null)
		return aUser;
	    else
		throw new FailedLoginException(aLoginManager.getErrorMessage());
	}

	throw new FailedLoginException("wrong username or password");
    }

    @Override
    public boolean uploadAsset(final String sParentSetId, @Nullable final AssetData aAssetData) throws FailedLoginException {
	if (aAssetData != null) {
	    final User aUser = _authenticate();
	    final SetManager aSetManager = SetManager.getInstance();
	    final Set aParentSet = aSetManager.get(sParentSetId);

	    try {
		// check write credentials
		// also allow it for owners of recently created sets...
		if (GroupManager.getInstance().isWrite(aUser, aParentSet)) {
		    final DataHandler aDH = aAssetData.getAssetData();
		    if (aDH != null) {
			final Asset aAsset = new Asset(aUser.getId(), aAssetData.getName(), aAssetData.getMetaContent(), aAssetData.isMetaContent());
			if (FSManager.save(aAsset, IOUtils.toByteArray(aDH.getInputStream()))) {
			    aParentSet.add(aAsset);
			    if (aSetManager.save(aParentSet))
				return AssetManager.getInstance().save(aAsset);
			}
		    }
		}
	    } catch (final IOException e) {
		e.printStackTrace();
	    }
	}

	return false;
    }

    @Override
    public boolean createSet(final String sParentSetId, @Nullable final SetData aSetData) throws FailedLoginException {
	if (aSetData != null) {
	    final User aUser = _authenticate();
	    final Set aParentSet = SetManager.getInstance().get(sParentSetId);

	    if (GroupManager.getInstance().isWrite(aUser, aParentSet))
		return SetManager.getInstance().save(sParentSetId, new Set(aSetData.getId(), aUser.getId(), aSetData.getName(), aSetData.getMetaContent()));
	}

	return false;
    }

    @Override
    @Nonnull
    public SetData[] getAllSets() throws FailedLoginException {
	final User aUser = _authenticate();

	return SetManager.getInstance().allWriteFor(aUser).stream().map(aSet -> new SetData(aSet)).toArray(SetData[]::new);
    }
}
