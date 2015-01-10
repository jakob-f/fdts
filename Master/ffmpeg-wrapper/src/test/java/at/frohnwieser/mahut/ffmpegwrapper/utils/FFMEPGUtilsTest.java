package at.frohnwieser.mahut.ffmpegwrapper.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import at.frohnwieser.mahut.ffmpegwrapper.util.FFMPEGUtils;

public class FFMEPGUtilsTest {
    @Test
    public void testFormatSupportedTrue() {
	assertTrue(FFMPEGUtils.isFormatSupported("mxf"));
    }

    @Test
    public void testFormatSupportedFalse() {
	assertFalse(FFMPEGUtils.isFormatSupported("xxx"));
    }
}
