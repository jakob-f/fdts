package at.frohnwieser.mahut.webappapi.util;

public interface Value {
    String DB_COLLECTION_ASSETS = "assets";
    String DB_COLLECTION_GROUPS = "groups";
    String DB_COLLECTION_SETS = "sets";
    String DB_COLLECTION_USERS = "users";
    String DB_COLLECTION_HASHTAGS = "hashtags";

    /* CONFIGURATION */
    String FILEPATH_PROPERTIES = "./properties.xml";
    String DATA_PATH_ROOT_DEFAULT = "/home/jf/data";
    String DATA_PATH_ASSETS_DEFAULT = DATA_PATH_ROOT_DEFAULT + "/assets";
    String DATA_PATH_META_DEFAULT = DATA_PATH_ROOT_DEFAULT + "/meta";
    String DB_PATH_DEFAULT = DATA_PATH_META_DEFAULT + "/mahut_db";
    String DB_PASSWORD_DEFAULT = "";
    String FILETYPE_THUMBNAIL = "jpg";
    String SET_FOLDER_META_CONTENT_DEFAULT = "meta_content";
    String SET_FOLDER_THUMBNAILS_DEFAULT = "thumbs";

}
