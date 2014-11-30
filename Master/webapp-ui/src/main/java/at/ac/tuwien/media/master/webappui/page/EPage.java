package at.ac.tuwien.media.master.webappui.page;

import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.ac.tuwien.media.master.webappapi.db.model.ERole;
import at.ac.tuwien.media.master.webappui.util.Value;

public enum EPage {
    ABOUT("about",
	    "about",
	    Value.PAGE_ABOUT,
	    ERole.PUBLIC),
    ASSETS("assets",
	    "Assets",
	    Value.PAGE_ASSETS,
	    ERole.ADMIN),
    CONTACT("contact",
	    "contact",
	    Value.PAGE_CONTACT,
	    ERole.PUBLIC),
    GROUPS("groups",
	    "Groups",
	    Value.PAGE_GROUPS,
	    ERole.ADMIN),
    // TODO fix path to ""
    HOME("home",
	    "home",
	    Value.PAGE_HOME,
	    ERole.PUBLIC),
    LEGAL("legal",
	    "legal information",
	    Value.PAGE_LEGAL,
	    ERole.PUBLIC),
    ROOT("",
	    "",
	    Value.FOLDER_ROOT,
	    ERole.PUBLIC),
    START("start",
	    "Start",
	    Value.PAGE_START,
	    ERole.USER),
    SETS("sets",
	    "Sets",
	    Value.PAGE_SETS,
	    ERole.ADMIN),
    USERS("users",
	    "Users",
	    Value.PAGE_USERS,
	    ERole.ADMIN),
    VIEW("view",
	    "View",
	    Value.PAGE_VIEW,
	    ERole.PUBLIC);

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

    private static String _getCleanName(@Nonnull final String sName) {
	return sName.toLowerCase().replaceAll("/", "");
    }

    @Nullable
    public static EPage getFromName(@Nonnull final String sName) {
	final String sCleanName = _getCleanName(sName);

	return Arrays.stream(EPage.values()).filter(aPage -> aPage.getName().equals(sCleanName)).findFirst().orElse(null);
    }

    @Nullable
    public static EPage getFromPath(@Nonnull final String sPath) {
	return Arrays.stream(EPage.values()).filter(aPage -> aPage.getPath().equals(sPath)).findFirst().orElse(null);
    }

    @Nullable
    public static EPage getFromNameOrPath(@Nonnull final String sNameOrPath) {
	final String sCleanName = _getCleanName(sNameOrPath);

	return Arrays.stream(EPage.values()).filter(aPage -> aPage.getName().equals(sCleanName) || aPage.getPath().equals(sNameOrPath)).findFirst()
	        .orElse(null);
    }
}
