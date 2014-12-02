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

import at.ac.tuwien.media.master.webapp.AssetData;
import at.ac.tuwien.media.master.webapp.FailedLoginException_Exception;
import at.ac.tuwien.media.master.webapp.IWSEndpoint;
import at.ac.tuwien.media.master.webapp.SetData;

public class WSClient {
    private final static String NAMESPACE_URI = "http://webapp.master.media.tuwien.ac.at/";
    private final static String LOCAL_PART = "WSEndpointImplService";

    private static WSClient s_aInstance = new WSClient();
    private IWSEndpoint s_aWSEndpoint;
    private String s_sUsername;
    private String s_sPassword;
    private URL s_aWSURL;

    private WSClient() {
    }

    public static WSClient getInstance() {
	return s_aInstance;
    }

    public void setUsername(@Nonnull final String sUsername) {
	if (StringUtils.isEmpty(sUsername))
	    throw new NullPointerException("username");

	s_sUsername = sUsername;
    }

    public void setPassword(@Nonnull final String sPassword) {
	if (StringUtils.isEmpty(sPassword))
	    throw new NullPointerException("password");

	s_sPassword = sPassword;
    }

    public void setWSURL(@Nonnull final URL aURL) {
	if (aURL == null)
	    throw new NullPointerException("url");

	s_aWSURL = aURL;
    }

    public boolean isReady() {
	return StringUtils.isNotEmpty(s_sUsername) && StringUtils.isNotEmpty(s_sPassword) && s_aWSURL != null;
    }

    public boolean isCreated() {
	return (isReady() && s_aWSEndpoint != null);
    }

    public void createEndpoint() {
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

    public boolean uploadAsset(final long nSetId, @Nonnull final AssetData aAssetData) throws FailedLoginException_Exception {
	if (!isCreated())
	    throw new IllegalStateException("ws end point not yet ready");

	return s_aWSEndpoint.uploadAsset(nSetId, aAssetData);
    }

    public boolean createSet(final long nSetId, @Nonnull final SetData aSetData) throws FailedLoginException_Exception {
	if (!isCreated())
	    throw new IllegalStateException("ws end point not yet ready");

	return s_aWSEndpoint.createSet(nSetId, aSetData);
    }

    @Nonnull
    public List<SetData> getSets() throws FailedLoginException_Exception {
	if (!isCreated())
	    throw new IllegalStateException("ws end point not yet ready");

	return s_aWSEndpoint.getAllSets();
    }
}
