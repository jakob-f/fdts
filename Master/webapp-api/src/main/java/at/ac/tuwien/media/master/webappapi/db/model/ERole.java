package at.ac.tuwien.media.master.webappapi.db.model;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum ERole implements
        Serializable {
    PUBLIC("public",
	    null),
    USER("user",
	    PUBLIC),
    ADMIN("admin",
	    USER);

    private String f_sName;
    private ERole m_aParent = null;

    private ERole(@Nonnull final String sName, @Nullable final ERole aParent) {
	f_sName = sName;
	m_aParent = aParent;
    }

    public String getName() {
	return f_sName;
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
