package at.frohnwieser.mahut.webapp.util;

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
    String RES_ROBOTS = FOLDER_ROOT + "robots.txt";

    /* PAGES */
    String PAGE_ABOUT = FOLDER_PAGE + "about.xhtml";
    String PAGE_ACCOUNT = FOLDER_PAGE + "account.xhtml";
    String PAGE_ASSETS = FOLDER_PAGE + "assets.xhtml";
    String PAGE_ASSETS2 = FOLDER_PAGE + "assets2.xhtml";
    String PAGE_CONTACT = FOLDER_PAGE + "contact.xhtml";
    String PAGE_ERROR = FOLDER_PAGE + "error.xhtml";
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
    String CONTROLLER_CONTACT = "contactController";
    String CONTROLLER_FILE_UPLOAD = "fileUploadController";
    String CONTROLLER_GROUPS = "groupsController";
    String CONTROLLER_HASHTAGS = "hashTagsController";
    String CONTROLLER_LOGIN = "loginController";
    String CONTROLLER_NAVIGATION = "navigationController";
    String CONTROLLER_RESOURCES = "resourcesController";
    String CONTROLLER_SETS = "setsController";
    String CONTROLLER_USERS = "usersController";
    String CONTROLLER_WALLPAPER = "wallpaperController";

    /* OTHER */
    String REGEX_ALLOWED_RESOURCES = "(css|ico|gif|jpg|js|png)";
    String REGEX_RESOURCE_HASH = "[A-Za-z0-9=]{28}";
    String REQUEST_PARAMETER_ASSET = "a";
    String REQUEST_PARAMETER_SEARCH = "q";
    String REQUEST_PARAMETER_SET = "s";
    String REQUEST_PARAMETER_USER = "u";
    Object REQUEST_PARAMETER_THUMBNAIL = "thumb";
}
