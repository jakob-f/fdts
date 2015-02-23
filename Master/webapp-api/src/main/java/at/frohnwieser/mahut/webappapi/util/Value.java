package at.frohnwieser.mahut.webappapi.util;

import java.util.concurrent.TimeUnit;

public interface Value {
    String DB_COLLECTION_ASSETS = "assets";
    String DB_COLLECTION_GROUPS = "groups";
    String DB_COLLECTION_HASHTAGS = "hashtags";
    String DB_COLLECTION_LOGINS = "logins";
    String DB_COLLECTION_SETS = "sets";
    String DB_COLLECTION_USERS = "users";

    /* CONFIGURATION */
    // String FILEPATH_PROPERTIES = "./conf/mahut.xml";
    String FILEPATH_PROPERTIES = "./properties.xml";
    String DATA_PATH_DEFAULT = "/home/jf/data";
    String DB_PASSWORD_DEFAULT = "SuperSecret";
    String ONTOLOGY_NAME_DEFAULT = "animals.owl";

    String DATA_FOLDER_ASSETS = "assets";
    String DATA_FOLDER_DB = "db";
    String DATA_FOLDER_META = "meta";
    String DATA_FOLDER_ONTOLOGY = "ontology";
    String DB_NAME = "mahut_db";
    String FILETYPE_THUMBNAIL = "jpg";
    String SET_FOLDER_META_CONTENT = "meta_content";
    String SET_FOLDER_THUMBNAILS = "thumbs";
    long ROOT_SET_ID = 0;

    int MAX_LOGIN_ATTEMPTS = 5;
    long LOCK_TIME = TimeUnit.MINUTES.toMillis(1);
}
