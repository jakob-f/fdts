package at.frohnwieser.mahut.webappapi.db.model;

import java.io.Serializable;

import javax.annotation.Nonnull;

public enum EPermission implements
        Serializable {
    NONE("none",
	    false,
	    false),
    READ_ONLY("read only",
	    true,
	    false),
    READ_WRITE("read and write",
	    true,
	    true);

    private final String f_sName;
    private boolean f_bIsRead;
    private boolean f_bIsWrite;

    private EPermission(@Nonnull final String sName, final boolean bIsRead, final boolean bIsWrite) {
	f_sName = sName;
	f_bIsRead = bIsRead;
	f_bIsWrite = bIsWrite;
    }

    @Nonnull
    public String getName() {
	return f_sName;
    }

    public boolean isRead() {
	return f_bIsRead;
    }

    public boolean isWrite() {
	return f_bIsWrite;
    }

    public static EPermission getFromPermissions(final boolean bIsRead, final boolean bIsWrite) {
	if (!bIsWrite)
	    if (!bIsRead)
		return NONE;
	    else
		return READ_ONLY;
	else
	    return READ_WRITE;

    }
}
