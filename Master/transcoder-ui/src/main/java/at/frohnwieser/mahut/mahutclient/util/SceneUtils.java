package at.frohnwieser.mahut.mahutclient.util;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.frohnwieser.mahut.mahutclient.data.ClientData;

public class SceneUtils {
    public enum EView {
	FILELIST(Value.FXML_FILELIST),
	MAIN(Value.FXML_MAIN),
	PROGRESSBARS(Value.FXML_PROGRESSBARS),
	SETTINGS(Value.FXML_SETTINGS);

	private final String f_sFXMLLocation;

	private EView(@Nonnull final String aFXMLLocation) {
	    f_sFXMLLocation = aFXMLLocation;
	}

	@Nonnull
	public String getFXMLLocation() {
	    return f_sFXMLLocation;
	}
    }

    private class ViewData {
	Object controller;
	Pane pane;
	Scene scene;
    }

    private static SceneUtils m_aInstance = new SceneUtils();
    private final Map<EView, ViewData> f_aViewMap;

    private SceneUtils() {
	f_aViewMap = new HashMap<EView, ViewData>();
    }

    public static SceneUtils getInstance() {
	return m_aInstance;
    }

    @Nullable
    private void _load(@Nonnull final EView aView) {
	try {
	    // create resource bundle
	    final ResourceBundle aResourceBundle = ResourceBundle.getBundle("bundles.application", ClientData.getInstance().getLocale());

	    // create fxml loader...
	    final FXMLLoader aFXMLLoader = new FXMLLoader();
	    // ... and set resources
	    aFXMLLoader.setLocation(SceneUtils.class.getClassLoader().getResource(aView.getFXMLLocation()));
	    aFXMLLoader.setResources(aResourceBundle);

	    // load pane
	    final Pane aPane = (Pane) aFXMLLoader.load();
	    if (aPane != null) {
		// add pane to scene
		final Scene aScene = new Scene(aPane);
		aScene.getStylesheets().add(Value.CSS_APPLICATION);

		// create new view data and set fields
		final ViewData aViewData = new ViewData();
		aViewData.controller = aFXMLLoader.getController();
		aViewData.pane = aPane;
		aViewData.scene = aScene;

		// finally add view to view map
		f_aViewMap.put(aView, aViewData);
	    }
	} catch (final Exception aException) {
	    aException.printStackTrace();
	}
    }

    private ViewData _getViewData(@Nonnull final EView aView) {
	if (!f_aViewMap.containsKey(aView))
	    _load(aView);

	return f_aViewMap.get(aView);
    }

    public Object getController(@Nonnull final EView aView) {
	return _getViewData(aView).controller;
    }

    @Nullable
    public Pane getPane(@Nonnull final EView aView) {
	return _getViewData(aView).pane;
    }

    @Nullable
    public Scene getScene(@Nonnull final EView aView) {
	return _getViewData(aView).scene;
    }

    public void reload() {
	for (final EView aView : EView.values())
	    _load(aView);
    }
}
