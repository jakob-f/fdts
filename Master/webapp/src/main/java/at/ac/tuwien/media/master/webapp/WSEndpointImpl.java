package at.ac.tuwien.media.master.webapp;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javafx.scene.image.Image;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.security.auth.login.FailedLoginException;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.MTOM;

import at.ac.tuwien.media.master.webapp.data.AssetData;
import at.ac.tuwien.media.master.webapp.data.SetData;
import at.ac.tuwien.media.master.webappapi.db.manager.impl.MetaManager;
import at.ac.tuwien.media.master.webappapi.db.manager.impl.UserManager;
import at.ac.tuwien.media.master.webappapi.db.model.Set;
import at.ac.tuwien.media.master.webappapi.db.model.User;

@MTOM
@WebService(endpointInterface = "at.ac.tuwien.media.master.webapp.IWSEndpoint")
public class WSEndpointImpl implements IWSEndpoint {
    String HEADER_USERNAME = "username";
    String HEADER_PASSWORD = "password";

    @Resource
    WebServiceContext aWSContext;

    @Nullable
    private User _authenticate() throws FailedLoginException {
	final Map<?, ?> aHeaders = (Map<?, ?>) aWSContext.getMessageContext().get(MessageContext.HTTP_REQUEST_HEADERS);
	final List<?> aUsers = (List<?>) aHeaders.get(HEADER_USERNAME);
	final List<?> aPasswords = (List<?>) aHeaders.get(HEADER_PASSWORD);

	if (aUsers != null && aUsers.size() == 1 && aPasswords != null && aPasswords.size() == 1) {
	    final String sUsername = aUsers.get(0).toString();
	    final String sPassword = aPasswords.get(0).toString();

	    return UserManager.getInstance().read(sUsername, sPassword);
	}

	throw new FailedLoginException("wrong username or password");
    }

    @Override
    public boolean upload(final long nSetId, @Nonnull final AssetData aAssetData) throws FailedLoginException {
	final User aUser = _authenticate();

	if (aUser != null) {

	    return true;
	}

	return false;
    }

    @Override
    @Nonnull
    public SetData[] getAllSets() throws FailedLoginException {
	final User aUser = _authenticate();

	if (aUser != null) {
	    System.out.println("GET PROJECTS");

	    final Collection<Set> aSets = MetaManager.getInstance().allSetsForUser(aUser);

	    return aSets.stream().map(aSet -> new SetData(aSet)).toArray(SetData[]::new);
	}

	return new SetData[] {};
    }

    @Override
    public void get(final Image aImage) {
	// TODO Auto-generated method stub

    }
}
