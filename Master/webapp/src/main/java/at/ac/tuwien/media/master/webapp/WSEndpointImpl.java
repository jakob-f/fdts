package at.ac.tuwien.media.master.webapp;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.security.auth.login.FailedLoginException;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.collections4.CollectionUtils;

import at.ac.tuwien.media.master.webappapi.db.manager.SetManager;
import at.ac.tuwien.media.master.webappapi.db.manager.UserManager;
import at.ac.tuwien.media.master.webappapi.db.model.Set;
import at.ac.tuwien.media.master.webappapi.db.model.User;

@WebService(endpointInterface = "at.ac.tuwien.media.master.webapp.IWSEndpoint")
public class WSEndpointImpl implements IWSEndpoint {
    String HEADER_USERNAME = "username";
    String HEADER_PASSWORD = "password";

    @Resource
    WebServiceContext aWSContext;

    @Nullable
    private User _authenticate() throws FailedLoginException {
	final Map<?, ?> aHeaders = (Map<?, ?>) aWSContext.getMessageContext().get(MessageContext.HTTP_REQUEST_HEADERS);
	final List<?> aUserList = (List<?>) aHeaders.get(HEADER_USERNAME);
	final List<?> aPasswordList = (List<?>) aHeaders.get(HEADER_PASSWORD);

	if (CollectionUtils.isNotEmpty(aUserList) && CollectionUtils.isNotEmpty(aPasswordList)) {
	    final String sUsername = aUserList.get(0).toString();
	    final String sPassword = aPasswordList.get(0).toString();

	    return UserManager.getInstance().read(sUsername, sPassword);
	}

	throw new FailedLoginException("wrong username or password");
    }

    @Override
    public boolean upload(@Nonnull final Set aSet) throws FailedLoginException {
	if (_authenticate() != null) {
	    System.out.println("UPLOAD");

	    return true;
	}

	return false;
    }

    @Override
    @Nonnull
    public String[] getAllSets() throws FailedLoginException {
	final User aUser = _authenticate();

	if (aUser != null) {
	    System.out.println("GET PROJECTS");

	    // FIXME
	    final Collection<Set> aSetsList = SetManager.getInstance().all();
	    final String[] aSetsArray = new String[aSetsList.size()];
	    int i = 0;
	    for (final Set aSet : aSetsList) {
		aSetsArray[i] = aSet.getName();
		i++;
	    }

	    return aSetsArray;
	}

	return new String[] {};
    }
}
