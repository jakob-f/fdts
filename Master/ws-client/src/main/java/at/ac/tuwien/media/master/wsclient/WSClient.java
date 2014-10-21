package at.ac.tuwien.media.master.wsclient;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webapp.FailedLoginException_Exception;
import at.ac.tuwien.media.master.webapp.IWSEndpoint;
import at.ac.tuwien.media.master.webapp.ProjectData;

public final class WSClient {
    private final static String NAMESPACE_URI = "http://webapp.master.media.tuwien.ac.at/";
    private final static String LOCAL_PART = "WSEndpointImplService";

    private static IWSEndpoint s_aWSEndpoint;
    private static String s_sUsername;
    private static String s_sPassword;
    private static URL s_aWSURL;

    private WSClient() {
    }

    public static void setUsername(@Nonnull final String sUsername) {
	if (StringUtils.isEmpty(sUsername))
	    throw new NullPointerException("username");

	s_sUsername = sUsername;
    }

    public static void setPassword(@Nonnull final String sPassword) {
	if (StringUtils.isEmpty(sPassword))
	    throw new NullPointerException("password");

	s_sPassword = sPassword;
    }

    public static void setWSURL(@Nonnull final URL aURL) {
	if (aURL == null)
	    throw new NullPointerException("url");

	s_aWSURL = aURL;
    }

    public static boolean isReady() {
	return StringUtils.isNotEmpty(s_sUsername) && StringUtils.isNotEmpty(s_sPassword) && s_aWSURL != null;
    }

    public static void createEndpoint() {
	if (!isReady())
	    throw new IllegalStateException("not all data is set");

	// create end point
	final QName aQName = new QName(NAMESPACE_URI, LOCAL_PART);
	final Service service = Service.create(s_aWSURL, aQName);
	s_aWSEndpoint = service.getPort(IWSEndpoint.class);

	// create authentication headers
	final Map<String, List<String>> aHeaders = new HashMap<String, List<String>>();
	// TODO use final fields
	aHeaders.put("username", Collections.singletonList(s_sUsername));
	aHeaders.put("password", Collections.singletonList(s_sPassword));

	// set headers
	final Map<String, Object> aContext = ((BindingProvider) s_aWSEndpoint).getRequestContext();
	aContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, s_aWSURL.toString());
	aContext.put(MessageContext.HTTP_REQUEST_HEADERS, aHeaders);
    }

    public static boolean upload(@Nonnull final ProjectData aData) throws FailedLoginException_Exception {
	if (!isReady() && s_aWSEndpoint == null)
	    throw new IllegalStateException("ws end point not yet ready");

	return s_aWSEndpoint.upload(aData);
    }

    @Nonnull
    public static List<String> getProjects() throws FailedLoginException_Exception {
	if (!isReady() && s_aWSEndpoint == null)
	    throw new IllegalStateException("ws end point not yet ready");

	return s_aWSEndpoint.getProjects();
    }
}
