package at.frohnwieser.mahut.wsclient;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

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
	    WSClient.getInstance().setWSURL(new URL("http://localhost:8080/webapp/ws?wsdl"));
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
	    aAssetData.setAssetData(null);
	    aAssetData.setArchiveFilePath("archive file path");
	    aAssetData.setMetaContent("bla bla bla");
	    aAssetData.setIsMetaContent(false);

	    assertTrue(WSClient.getInstance().uploadAsset(-1L, aAssetData));
	} catch (final FailedLoginException_Exception e) {
	    fail();
	}
    }
}
