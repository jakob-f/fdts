package at.frohnwieser.mahut.ffmpegwrapper.util;

import javax.annotation.Nonnull;

public enum EFormat {
    OGG("ogg",
	    "libvorbis",
	    "libtheora"),
    MP4("mp4",
	    "libfaac",
	    "libx264"),
    WEBM("webm",
	    "libvorbis",
	    "libvpx"),
    MAC_MP4("mp4",
	    "libfdk_aac",
	    "libx264");

    private final String f_sName;
    private final String f_sACodec;
    private final String f_sVCodec;

    private EFormat(@Nonnull final String sName, @Nonnull final String sACodec, @Nonnull final String sVCodec) {
	f_sName = sName;
	f_sACodec = sACodec;
	f_sVCodec = sVCodec;
    }

    public String getName() {
	return f_sName;
    }

    public String getACodec() {
	return f_sACodec;
    }

    public String getVCodec() {
	return f_sVCodec;
    }
}