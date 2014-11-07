package at.ac.tuwien.media.master.webappui.util;

public interface Value {
    /* FOLDERS */
    String FOLDER_ROOT = "/";
    String FOLDER_RESOURCES = FOLDER_ROOT + "resources/";
    String FOLDER_ASSET = FOLDER_ROOT + "asset/";
    String FOLDER_IMAGES = FOLDER_RESOURCES + "images/";
    String FOLDER_JAVAX = FOLDER_ROOT + "javax.faces.resource/";
    String FOLDER_PAGE = FOLDER_ROOT + "pages/";
    String RES_NOT_FOUND = "/RES_NOT_FOUND";

    /* PAGES */
    String PAGE_ABOUT = FOLDER_PAGE + "about.xhtml";
    String PAGE_ASSETS = FOLDER_PAGE + "assets.xhtml";
    String PAGE_CONTACT = FOLDER_PAGE + "contact.xhtml";
    String PAGE_GROUPS = FOLDER_PAGE + "groups.xhtml";
    String PAGE_LEGAL = FOLDER_PAGE + "legal.xhtml";
    String PAGE_LOGIN = FOLDER_PAGE + "login.xhtml";
    String PAGE_PROJECTS = FOLDER_PAGE + "projects.xhtml";
    String PAGE_START = FOLDER_PAGE + "start.xhtml";
    String PAGE_USERS = FOLDER_PAGE + "users.xhtml";
    String PAGE_VIEW = FOLDER_PAGE + "view.xhtml";

    /* BEANS */
    String BEAN_CREDENTIALS = "credentialsBean";

    /* CONTROLLER */
    String CONTROLLER_ASSETS = "assetsController";
    String CONTROLLER_GROUPS = "groupsController";
    String CONTROLLER_LOGIN = "loginController";
    String CONTROLLER_NAVIGATION = "navigationController";
    String CONTROLLER_PROJECTS = "projectsController";
    String CONTROLLER_USERS = "usersController";
    String CONTROLLER_WALLPAPER = "wallpaperController";

    /* OTHER */
    String REGEX_ALLOWED_RESOURCES = "(css|ico|gif|jpg|js|png)";
    String REGEY_MD5_HEX = "[A-Fa-f0-9]{32}";
    String PARAMETER_ASSET = "a";
    String PARAMETER_REDIRECT = "?faces-redirect=true";
}
