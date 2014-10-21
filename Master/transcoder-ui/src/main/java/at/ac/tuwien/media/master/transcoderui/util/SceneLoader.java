package at.ac.tuwien.media.master.transcoderui.util;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.ac.tuwien.media.master.transcoderui.config.Configuration;
import at.ac.tuwien.media.master.transcoderui.config.ConfigurationValue;

public final class SceneLoader {

    private SceneLoader() {
    }

    @Nullable
    public final static Scene load(@Nonnull final URL aFXMLLocationURL) {
	try {
	    if (aFXMLLocationURL == null)
		throw new NullPointerException("FXML location URL");

	    // create scene
	    final ResourceBundle aResourceBundle = ResourceBundle.getBundle("bundles.application",
		    new Locale(Configuration.getAsString(ConfigurationValue.LANGUAGE)));
	    final Scene aScene = new Scene((Pane) new FXMLLoader(aFXMLLocationURL, aResourceBundle).load());
	    aScene.getStylesheets().add(Value.CSS_APPLICATION);

	    return aScene;
	} catch (final Exception aException) {
	    aException.printStackTrace();
	}

	return null;
    }
}
