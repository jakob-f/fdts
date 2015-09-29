package at.frohnwieser.mahut.client.util;

public interface Value {
    /* UI */
    double METACONTENTBOX_HEIGHT = 210;
    double WINDOW_WIDTH = 500;
    double WINDOW_HEIGHT_DEFAULT = 364;
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
    // logo
    String LOGO_PATH = "images/icon.png";

    /* CONFIGURATION */
    String DEFAULT_EMPTY = "";
    String FILEPATH_DEFAULT = System.getProperty("user.home");
    String FILEPATH_MAHUT = FILEPATH_DEFAULT + "/" + ".mahut";
    String FILEPATH_PROPERTIES = FILEPATH_MAHUT + "/properties.xml";
    String FILEPATH_TMP = FILEPATH_MAHUT + "/" + "tmp";

    boolean DEFAULT_IS_SELECTED = false;
    String DEFAULT_SERVER_URL = "http://217.160.184.132:8080/ws?wsdl";
}
