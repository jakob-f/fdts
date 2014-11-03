package at.ac.tuwien.media.master.webapp;

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

import at.ac.tuwien.media.master.webappapi.DataManager;
import at.ac.tuwien.media.master.webappapi.model.Project;
import at.ac.tuwien.media.master.webappapi.model.ProjectData;
import at.ac.tuwien.media.master.webappapi.model.User;

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

	    return DataManager.getInstance().getValidUser(sUsername, sPassword);
	}

	throw new FailedLoginException("wrong username or password");
    }

    @Override
    public boolean upload(@Nonnull final ProjectData aData) throws FailedLoginException {
	if (_authenticate() != null) {
	    System.out.println("UPLOAD");

	    return true;
	}

	return false;
    }

    @Override
    @Nonnull
    public String[] getProjects() throws FailedLoginException {
	final User aUser = _authenticate();

	if (aUser != null) {
	    System.out.println("GET PROJECTS");

	    final List<Project> aProjectsList = DataManager.getInstance().getProjectsForUser(aUser.getId());
	    final String[] aPrjectArray = new String[aProjectsList.size()];
	    for (int i = 0; i < aProjectsList.size(); i++)
		aPrjectArray[i] = aProjectsList.get(i).getName();

	    return aPrjectArray;
	}

	return new String[] {};
    }
}
