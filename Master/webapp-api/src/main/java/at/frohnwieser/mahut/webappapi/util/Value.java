package at.frohnwieser.mahut.webappapi.util;

import java.util.concurrent.TimeUnit;

public interface Value {
    /* DB TABLE NAMES */
    String DB_COLLECTION_ASSETS = "assets";
    String DB_COLLECTION_GROUPS = "groups";
    String DB_COLLECTION_HASHTAGS = "hashtags";
    String DB_COLLECTION_LOGINS = "logins";
    String DB_COLLECTION_SETS = "sets";
    String DB_COLLECTION_USERS = "users";

    /* CONFIGURATION */
    String FILEPATH_PROPERTIES = "./properties.xml";
    String DB_NAME = "mahut_db";
    String DB_PASSWORD_DEFAULT = "SuperSecret";
    String ONTOLOGY_NAME_DEFAULT = "animals.owl";

    /* DATA PATHS */
    String DATA_PATH_DEFAULT = "/home/jf/data";
    String DATA_FOLDER_ASSETS = "assets";
    String DATA_FOLDER_THUMBNAILS = "thumbs";
    String DATA_FOLDER_META = "meta";
    String DATA_FOLDER_DB = "db";
    String DATA_FOLDER_ONTOLOGY = "ontology";

    /* MISC */
    String THUMBNAIL_FILETYPE = "jpg";
    String ROOT_SET_ID = "000000";

    int MAX_LOGIN_ATTEMPTS = 5;
    long LOCK_TIME = TimeUnit.MINUTES.toMillis(1);
}
