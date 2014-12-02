package at.ac.tuwien.media.master.webapp.util;

public interface Value {
    /* FOLDERS */
    String FOLDER_ROOT = "/";
    String FOLDER_RESOURCES = FOLDER_ROOT + "resources/";
    String FOLDER_ASSET = FOLDER_ROOT + "asset/";
    String FOLDER_IMAGES = FOLDER_RESOURCES + "images/";
    String FOLDER_JAVAX = FOLDER_ROOT + "javax.faces.resource/";
    String FOLDER_PAGE = FOLDER_ROOT + "pages/";
    String FOLDER_WS = FOLDER_ROOT + "ws";
    String RES_NOT_FOUND = FOLDER_ROOT + "RES_NOT_FOUND";

    /* PAGES */
    String PAGE_ABOUT = FOLDER_PAGE + "about.xhtml";
    String PAGE_ASSETS = FOLDER_PAGE + "assets.xhtml";
    String PAGE_CONTACT = FOLDER_PAGE + "contact.xhtml";
    String PAGE_GROUPS = FOLDER_PAGE + "groups.xhtml";
    String PAGE_HOME = FOLDER_PAGE + "home.xhtml";
    String PAGE_LEGAL = FOLDER_PAGE + "legal.xhtml";
    String PAGE_START = FOLDER_PAGE + "start.xhtml";
    String PAGE_SETS = FOLDER_PAGE + "sets.xhtml";
    String PAGE_USERS = FOLDER_PAGE + "users.xhtml";
    String PAGE_VIEW = FOLDER_PAGE + "view.xhtml";

    /* BEANS */
    String BEAN_CREDENTIALS = "credentialsBean";

    /* CONTROLLER */
    String CONTROLLER_ASSETS = "assetsController";
    String CONTROLLER_GROUPS = "groupsController";
    String CONTROLLER_LOGIN = "loginController";
    String CONTROLLER_NAVIGATION = "navigationController";
    String CONTROLLER_SETS = "setsController";
    String CONTROLLER_USERS = "usersController";
    String CONTROLLER_WALLPAPER = "wallpaperController";

    /* OTHER */
    String REGEX_ALLOWED_RESOURCES = "(css|ico|gif|jpg|js|png)";
    String REGEX_ASSET_HASH = "[A-Za-z0-9=]{28}";
    String REQUEST_PARAMETER_ASSET = "a";
}
