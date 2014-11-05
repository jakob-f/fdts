package at.ac.tuwien.media.master.webappui.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.ac.tuwien.media.master.webappapi.model.ERole;

public enum EPage {
    ABOUT("about",
	    "about",
	    Value.PAGE_ABOUT,
	    ERole.DEFAULT),
    ASSETS("assets",
	    "Assets",
	    Value.PAGE_ASSETS,
	    ERole.ADMIN),
    CONTACT("contact",
	    "contact",
	    Value.PAGE_CONTACT,
	    ERole.DEFAULT),
    GROUPS("groups",
	    "Groups",
	    Value.PAGE_GROUPS,
	    ERole.ADMIN),
    HOME("login",
	    "home",
	    Value.FOLDER_ROOT,
	    ERole.DEFAULT),
    LEGAL("legal",
	    "legal information",
	    Value.PAGE_LEGAL,
	    ERole.DEFAULT),
    LOGIN("login",
	    "Login",
	    Value.PAGE_LOGIN,
	    ERole.DEFAULT),
    PROJECTS("projects",
	    "Projects",
	    Value.PAGE_PROJECTS,
	    ERole.ADMIN),
    ROOT("",
	    "",
	    Value.FOLDER_ROOT,
	    ERole.DEFAULT),
    START("start",
	    "Start",
	    Value.PAGE_START,
	    ERole.USER),
    USERS("users",
	    "Users",
	    Value.PAGE_USERS,
	    ERole.ADMIN),
    VIEW("view",
	    "View",
	    Value.PAGE_VIEW,
	    ERole.DEFAULT);

    private final String f_sName;
    private final String f_sDisplayName;
    private final String f_sPath;
    private final ERole f_aRole;

    private EPage(@Nonnull final String sName, @Nonnull final String sDisplayName, @Nonnull final String sPath, @Nonnull final ERole aRole) {
	f_sName = sName;
	f_sDisplayName = sDisplayName;
	f_sPath = sPath;
	f_aRole = aRole;
    }

    public String getName() {
	return f_sName;
    }

    public String getDisplayName() {
	return f_sDisplayName;
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
