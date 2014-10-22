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
import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.DataManager;
import at.ac.tuwien.media.master.webappapi.model.Project;
import at.ac.tuwien.media.master.webappapi.model.ProjectData;

@WebService(endpointInterface = "at.ac.tuwien.media.master.webapp.IWSEndpoint")
public class WSEndpointImpl implements IWSEndpoint {
    String HEADER_USERNAME = "username";
    String HEADER_PASSWORD = "password";

    @Resource
    WebServiceContext aWSContext;

    @Nullable
    private String _authenticate() throws FailedLoginException {
	final Map<?, ?> aHeaders = (Map<?, ?>) aWSContext.getMessageContext().get(MessageContext.HTTP_REQUEST_HEADERS);
	final List<?> aUserList = (List<?>) aHeaders.get(HEADER_USERNAME);
	final List<?> aPasswordList = (List<?>) aHeaders.get(HEADER_PASSWORD);

	if (CollectionUtils.isNotEmpty(aUserList) && CollectionUtils.isNotEmpty(aPasswordList)) {
	    final String sUsername = aUserList.get(0).toString();
	    final String sPassword = aPasswordList.get(0).toString();

	    if (DataManager.isValidUser(sUsername, sPassword))
		return sUsername;
	}

	throw new FailedLoginException("wrong username or password");
    }

    @Override
    public boolean upload(@Nonnull final ProjectData aData) throws FailedLoginException {
	if (StringUtils.isNotEmpty(_authenticate())) {
	    System.out.println("UPLOAD");

	    return true;
	}

	return false;
    }

    @Override
    @Nonnull
    public String[] getProjects() throws FailedLoginException {
	final String sUsername = _authenticate();

	if (StringUtils.isNotEmpty(sUsername)) {
	    System.out.println("GET PROJECTS");

	    final List<Project> aProjectsList = DataManager.getProjectsForUser(sUsername);
	    final String[] aPrjectArray = new String[aProjectsList.size()];
	    for (int i = 0; i < aProjectsList.size(); i++)
		aPrjectArray[i] = aProjectsList.get(i).getName();

	    return aPrjectArray;
	}

	return new String[] {};
    }
}
