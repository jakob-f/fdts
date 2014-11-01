package at.ac.tuwien.media.master.webappui.util;

public interface Value {
    /* FOLDERS */
    String FOLDER_ROOT = "/";
    String FOLDER_RESOURCES = FOLDER_ROOT + "resources/";
    String FOLDER_IMAGES = FOLDER_RESOURCES + "images/";
    String FOLDER_PAGE = FOLDER_ROOT + "page/";
    String FOLDER_WP = FOLDER_IMAGES + "wp/";

    /* PAGES */
    String PAGE_LOGIN = FOLDER_ROOT + "login.xhtml";
    String PAGE_START = FOLDER_PAGE + "start.xhtml";
    String PAGE_PROJECTS = FOLDER_PAGE + "projects.xhtml";
    String PAGE_USERS = FOLDER_PAGE + "users.xhtml";

    /* BEANS */
    String BEAN_CREDENTIALS = "credentialsBean";

    /* CONTROLLER */
    String CONTROLLER_LOGIN = "loginController";
    String CONTROLLER_NAVIGATION = "navigationController";
    String CONTROLLER_PROJECTS = "projectsController";
    String CONTROLLER_USERS = "usersController";

    /* OTHER */
    String REGEX_IMAGE = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";;
}
