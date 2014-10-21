package at.ac.tuwien.media.master.transcoderui.util;

public interface Value {
    /* UI */
    double WINDOW_WIDTH = 500;
    double WINDOW_HEIGHT_DEFAULT = 377;
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
    String FXML_MAIN = FXML_ROOT + "view_main.fxml";
    String FXML_SETTINGS = FXML_ROOT + "view_settings.fxml";

    /* CONFIGURATION */
    String PROPERTIES_PATH = "./properties.xml";
    /* default configuration values */
    String DEFAULT_EMPTY = "";
    String DEFAULT_FILEPATH = "./";
    boolean DEFAULT_IS_SELECTED = false;
}
