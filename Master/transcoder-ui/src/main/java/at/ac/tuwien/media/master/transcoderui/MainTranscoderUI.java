package at.ac.tuwien.media.master.transcoderui;

import java.io.File;
import java.net.URL;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.annotation.Nonnull;

import at.ac.tuwien.media.master.transcoderui.util.SceneLoader;
import at.ac.tuwien.media.master.transcoderui.util.Value;

public class MainTranscoderUI extends Application {
    @Override
    public void start(@Nonnull final Stage aPrimaryStage) throws Exception {
	// load fxml
	URL aFXMLLocationURL = null;
	if (new File(Value.PROPERTIES_PATH).exists())
	    aFXMLLocationURL = getClass().getClassLoader().getResource(Value.FXML_MAIN);
	else
	    aFXMLLocationURL = getClass().getClassLoader().getResource(Value.FXML_SETTINGS);

	// create scene
	final Scene aScene = SceneLoader.load(aFXMLLocationURL);

	if (aScene != null) {
	    // set up primary stage
	    aPrimaryStage.setScene(aScene);
	    aPrimaryStage.setMaxHeight(Value.WINDOW_HEIGHT_DEFAULT);
	    aPrimaryStage.setMinHeight(Value.WINDOW_HEIGHT_DEFAULT);
	    aPrimaryStage.setResizable(false);
	    aPrimaryStage.getIcons().add(new Image("./images/045.jpg"));
	    aPrimaryStage.setTitle("Transcoder UI");
	    aPrimaryStage.show();
	}
    }

    public static void main(@Nonnull final String[] args) {
	launch(args);
    }
}