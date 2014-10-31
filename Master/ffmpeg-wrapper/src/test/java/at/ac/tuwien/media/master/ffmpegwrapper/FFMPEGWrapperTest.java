package at.ac.tuwien.media.master.ffmpegwrapper;

import static org.junit.Assert.fail;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import at.ac.tuwien.media.master.ffmpegwrapper.FFMPEGWrapper.EQuality;

public class FFMPEGWrapperTest {

    @Test
    public void testMetadata() {
	try {
	    System.out.println(FFMPEGWrapper.metadata(FileUtils.getFile("src/test/resources", "test-file.mxf")).toJSONString());
	} catch (final Exception aException) {
	    System.out.println(aException.getMessage());
	    fail();
	}
    }

    @Test
    public void testTranscode() {
	try {
	    FFMPEGWrapper.transcode(FileUtils.getFile("src/test/resources", "test-file.mxf"), FileUtils.getFile("src/test/resources", "out-file.avi"),
		    EQuality.P1080);
	} catch (final Exception aException) {
	    aException.printStackTrace();
	    fail();
	}
    }
}
