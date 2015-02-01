package at.frohnwieser.mahut.ffmpegwrapper.util;

import javax.annotation.Nonnull;

public enum EQuality {
    P240("scale=240:-1",
            "400k"),
    P320("scale=320:-1",
            "600k"),
    P360("scale=360:-1",
            "750k"),
    P480("scale=480:-1",
            "1000k"),
    P720("scale=720:-1",
            "2500k"),
    P1080("scale=1080:-1",
            "4500k");

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