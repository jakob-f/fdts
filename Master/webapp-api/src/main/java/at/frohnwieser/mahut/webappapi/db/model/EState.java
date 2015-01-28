package at.frohnwieser.mahut.webappapi.db.model;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum EState implements
        Serializable {
    PRIVATE("private",
	    null),
    PUBLISHED("published",
	    PRIVATE),
    PUBLIC("public",
	    PUBLISHED),
    HOME_PAGE("home_page",
	    PUBLISHED),
    // TODO delete
    MAIN_PAGE("home_page",
	    PUBLISHED);

    private String f_sName;
    private EState m_aParent = null;

    private EState(@Nonnull final String sName, @Nullable final EState aParent) {
	f_sName = sName;
	m_aParent = aParent;
    }

    public String getName() {
	return f_sName;
    }

    public boolean is(@Nullable final EState aOther) {
	if (aOther == null)
	    return false;

	for (EState aRole = this; aRole != null; aRole = aRole.m_aParent)
	    if (aOther == aRole)
		return true;

	return false;
    }

}
