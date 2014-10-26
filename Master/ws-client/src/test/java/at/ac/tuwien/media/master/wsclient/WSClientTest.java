package at.ac.tuwien.media.master.wsclient;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.media.master.webapp.FailedLoginException_Exception;

public class WSClientTest {
    @Before
    public void setUp() {
	try {
	    WSClient.getInstance().setUsername("admin");
	    WSClient.getInstance().setPassword("pass");
	    WSClient.getInstance().setWSURL(new URL("http://localhost:8080/webapp/ws?wsdl"));
	    WSClient.getInstance().createEndpoint();
	} catch (final MalformedURLException aMalformedURLException) {
	    aMalformedURLException.printStackTrace();
	}
    }

    @Test
    public void testGetProjects() {
	try {
	    final List<String> aProjectList = WSClient.getInstance().getProjects();

	    assertNotNull(aProjectList);
	    assertTrue(aProjectList.size() > 0);
	    System.out.println(aProjectList);
	} catch (final FailedLoginException_Exception e) {
	    fail();
	}
    }
}
