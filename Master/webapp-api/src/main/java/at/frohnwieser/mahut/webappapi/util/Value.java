package at.frohnwieser.mahut.webappapi.util;

public interface Value {
    String DB_COLLECTION_ASSETS = "assets";
    String DB_COLLECTION_GROUPS = "groups";
    String DB_COLLECTION_SETS = "sets";
    String DB_COLLECTION_USERS = "users";
    String DB_COLLECTION_HASHTAGS = "hashtags";

    /* CONFIGURATION */
    // String FILEPATH_PROPERTIES = "./conf/mahut.xml";
    String FILEPATH_PROPERTIES = "./properties.xml";
    String DATA_PATH_DEFAULT = "/home/jf/data";
    String DB_PASSWORD_DEFAULT = "SuperSecret";
    String ONTOLOGY_NAME_DEFAULT = "animals.owl";

    String DATA_FOLDER_ASSETS = "assets";
    String DATA_FOLDER_META = "meta";
    String DATA_FOLDER_ONTOLOGY = "ontology";
    String DB_NAME = "/db/mahut_db";
    String FILETYPE_THUMBNAIL = "jpg";
    String SET_FOLDER_META_CONTENT = "meta_content";
    String SET_FOLDER_THUMBNAILS = "thumbs";
    long ROOT_SET_ID = 0;
}
