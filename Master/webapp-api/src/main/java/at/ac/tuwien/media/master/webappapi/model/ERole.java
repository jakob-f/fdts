package at.ac.tuwien.media.master.webappapi.model;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum ERole {
    DEFAULT(0,
	    null),
    USER(1,
	    DEFAULT),
    ADMIN(2,
	    USER);

    private int f_nId;
    private ERole m_aParent = null;

    private ERole(@Nonnegative final int nId, @Nonnull final ERole aParent) {
	f_nId = nId;
	m_aParent = aParent;
    }

    public int getId() {
	return f_nId;
    }

    public boolean is(@Nullable final ERole aOther) {
	if (aOther == null)
	    return false;

	for (ERole aRole = this; aRole != null; aRole = aRole.m_aParent)
	    if (aOther == aRole)
		return true;

	return false;
    }

}
