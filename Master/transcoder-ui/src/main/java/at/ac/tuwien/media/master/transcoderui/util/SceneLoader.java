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
    public final static Pane loadAsPane(@Nonnull final URL aFXMLLocationURL) {
	try {
	    if (aFXMLLocationURL == null)
		throw new NullPointerException("FXML location URL");

	    // create pane
	    final ResourceBundle aResourceBundle = ResourceBundle.getBundle("bundles.application",
		    new Locale(Configuration.getAsString(ConfigurationValue.LANGUAGE)));

	    return (Pane) new FXMLLoader(aFXMLLocationURL, aResourceBundle).load();
	} catch (final Exception aException) {
	    aException.printStackTrace();
	}

	return null;
    }

    @Nullable
    public final static Scene loadAsScene(@Nonnull final URL aFXMLLocationURL) {
	final Pane aPane = loadAsPane(aFXMLLocationURL);

	if (aPane != null) {
	    // add pane to scene
	    final Scene aScene = new Scene(aPane);
	    aScene.getStylesheets().add(Value.CSS_APPLICATION);

	    return aScene;
	}

	return null;
    }
}
