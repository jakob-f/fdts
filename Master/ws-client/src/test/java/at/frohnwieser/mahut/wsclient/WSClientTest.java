package at.frohnwieser.mahut.wsclient;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.activation.DataHandler;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import at.frohnwieser.mahut.webapp.AssetData;
import at.frohnwieser.mahut.webapp.FailedLoginException_Exception;
import at.frohnwieser.mahut.webapp.SetData;

@Ignore
public class WSClientTest {
    @Before
    public void setUp() {
	try {
	    WSClient.getInstance().setUsername("admin");
	    WSClient.getInstance().setPassword("pass");
	    WSClient.getInstance().setWSURL(new URL("http://localhost:8080/ws?wsdl"));
	    WSClient.getInstance().createEndpoint();
	} catch (final MalformedURLException aMalformedURLException) {
	    aMalformedURLException.printStackTrace();
	}
    }

    @Test
    public void testGetProjects() {
	try {
	    final List<SetData> aSetDatas = WSClient.getInstance().getSets();

	    assertNotNull(aSetDatas);
	    aSetDatas.forEach(aSetData -> System.out.println(aSetData.getId() + " " + aSetData.getName()));
	} catch (final FailedLoginException_Exception e) {
	    fail();
	}
    }

    @Test
    public void testUpload() {
	try {
	    final AssetData aAssetData = new AssetData();
	    aAssetData.setName("elephant.jpg");
	    aAssetData.setMetaContent("bla bla bla");
	    aAssetData.setIsMetaContent(true);
	    aAssetData.setAssetData(new DataHandler(FileUtils.getFile("src/test/resources", "elephant.jpg").toURI().toURL()));

	    assertTrue(WSClient.getInstance().uploadAsset("000000", aAssetData));
	} catch (final Exception ex) {
	    ex.printStackTrace();
	    fail();
	}
    }
}
