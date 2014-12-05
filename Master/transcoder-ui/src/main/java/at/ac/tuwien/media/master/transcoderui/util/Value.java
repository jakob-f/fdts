package at.ac.tuwien.media.master.transcoderui.util;

public interface Value {
    /* UI */
    double METACONTENTBOX_HEIGHT = 200;
    double WINDOW_WIDTH = 500;
    double WINDOW_HEIGHT_DEFAULT = 354;
    double PADDING = 20;
    // css files
    String CSS_ROOT = "css/";
    String CSS_APPLICATION = CSS_ROOT + "application.css";
    // bundles
    String BUNDLES_ROOT = "bundles/";
    String BUNDLES_APPLICATION_DE = BUNDLES_ROOT + "application_de.properties";
    String BUNDLES_APPLICATION_EN = BUNDLES_ROOT + "application_en.properties";
    // fxml files
    String FXML_ROOT = "fxml/";
    String FXML_FILELIST = FXML_ROOT + "view_filelist.fxml";
    String FXML_MAIN = FXML_ROOT + "view_main.fxml";
    String FXML_PROGRESSBARS = FXML_ROOT + "view_progressbars.fxml";
    String FXML_SETTINGS = FXML_ROOT + "view_settings.fxml";

    /* CONFIGURATION */
    String DEFAULT_EMPTY = "";
    String FILEPATH_DEFAULT = System.getProperty("user.home");
    String FILEPATH_MAHUT = FILEPATH_DEFAULT + "/" + ".mahut";
    String FILEPATH_ROPERTIES = FILEPATH_MAHUT + "/properties.xml";
    String FILEPATH_TMP = FILEPATH_MAHUT + "/" + "tmp";

    boolean DEFAULT_IS_SELECTED = false;

    /* OTHERS */
    String CHARACTER_AT = "@";
    String CHARACTER_HASH = "#";
    int MAX_LENGTH_METACONTENT = 200;
    int MAX_LENGTH_SETNAME = 50;
    String PLACEHOLDER = "__0__";
}
