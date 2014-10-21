package at.ac.tuwien.media.master.ffmpegwrapper;

import org.junit.Ignore;
import org.junit.Test;

public class FFMPEGWrapperTest {

    @Ignore
    @Test
    public void testTranscode() {
	FFMPEGWrapper.transcode("./1.mxf", "./out.avi");
    }
}
