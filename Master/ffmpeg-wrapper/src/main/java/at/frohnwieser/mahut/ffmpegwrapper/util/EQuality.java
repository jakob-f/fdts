package at.frohnwieser.mahut.ffmpegwrapper.util;

import javax.annotation.Nonnull;

public enum EQuality {
    P240("scale=424:-1",
	    "576k"),
    P360("scale=640:-1",
	    "896k"),
    P432("scale=768:-1",
	    "1088k"),
    P480("scale=848:-1",
	    "1216k"),
    P720("scale=1280:-1",
	    "2496k"),
    P1080("scale=1920:-1",
	    "4992k");

    private final String f_sScale;
    private final String f_sBitrate;

    private EQuality(@Nonnull final String sScale, @Nonnull final String sBitrate) {
	f_sScale = sScale;
	f_sBitrate = sBitrate;
    }

    public String getScale() {
	return f_sScale;
    }

    public String getBitrate() {
	return f_sBitrate;
    }
}