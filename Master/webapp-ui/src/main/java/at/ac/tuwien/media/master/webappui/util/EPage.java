package at.ac.tuwien.media.master.webappui.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.ac.tuwien.media.master.webappapi.model.ERole;

public enum EPage {
    ASSETS("assets",
	    Value.PAGE_ASSETS,
	    ERole.ADMIN),
    GROUPS("groups",
	    Value.PAGE_GROUPS,
	    ERole.ADMIN),
    LOGIN("login",
	    Value.PAGE_LOGIN,
	    ERole.DEFAULT),
    PROJECTS("projects",
	    Value.PAGE_PROJECTS,
	    ERole.ADMIN),
    ROOT("",
	    Value.FOLDER_ROOT,
	    ERole.DEFAULT),
    START("start",
	    Value.PAGE_START,
	    ERole.USER),
    USERS("users",
	    Value.PAGE_USERS,
	    ERole.ADMIN),
    VIEW("view",
	    Value.PAGE_VIEW,
	    ERole.DEFAULT);

    private final String f_sName;
    private final String f_sPath;
    private final ERole f_aRole;

    private EPage(@Nonnull final String sName, @Nonnull final String sPath, @Nonnull final ERole aRole) {
	f_sName = sName;
	f_sPath = sPath;
	f_aRole = aRole;
    }

    public String getName() {
	return f_sName;
    }

    public String getPath() {
	return f_sPath;
    }

    public ERole getRole() {
	return f_aRole;
    }

    @Nullable
    public static EPage getFromName(@Nonnull final String sName) {
	final String sCleanName = sName.toLowerCase().replaceAll("/", "");

	for (final EPage aPage : EPage.values())
	    if (aPage.getName().equals(sCleanName))
		return aPage;

	return null;
    }

    @Nullable
    public static EPage getFromPath(@Nonnull final String sPath) {
	for (final EPage aPage : EPage.values())
	    if (aPage.getPath().equals(sPath))
		return aPage;

	return null;
    }

    @Nullable
    public static EPage getFromNameOrPath(@Nonnull final String sNameOrPath) {
	final String sCleanName = sNameOrPath.toLowerCase().replaceAll("/", "");

	for (final EPage aPage : EPage.values())
	    if (aPage.getName().equals(sCleanName))
		return aPage;
	    else if (aPage.getPath().equals(sNameOrPath))
		return aPage;

	return null;
    }
}
