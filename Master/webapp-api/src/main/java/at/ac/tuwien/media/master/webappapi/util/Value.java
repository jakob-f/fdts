package at.ac.tuwien.media.master.webappapi.util;

public interface Value {
    // TODO move to config
    String DATA_PATH_ROOT = "/home/jf/data/";
    String DATA_PATH_ASSETS = DATA_PATH_ROOT + "assets/";
    String DATA_PATH_META = DATA_PATH_ROOT + "meta/";
    String DATE_PATTERN = "yyyy-MM-dd HH:mm";

    String DB_PWD = "sup4S3cr3t";
    String DB_PATH = DATA_PATH_META + "mahut_db";

    String DB_COLLECTION_ASSETS = "assets";
    String DB_COLLECTION_GROUPS = "groups";
    String DB_COLLECTION_SETS = "sets";
    String DB_COLLECTION_USERS = "users";
    String DB_COLLECTION_HASHTAGS = "hashtags";
}
