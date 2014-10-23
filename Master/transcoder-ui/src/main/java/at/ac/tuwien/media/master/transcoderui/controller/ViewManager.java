package at.ac.tuwien.media.master.transcoderui.controller;

import java.util.Map;

import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.map.HashedMap;

import at.ac.tuwien.media.master.transcoderui.util.SceneLoader;
import at.ac.tuwien.media.master.transcoderui.util.Value;

public final class ViewManager {
    public enum EView {
	MAIN(Value.FXML_MAIN), SETTINGS(Value.FXML_SETTINGS);

	private final String m_fFXMLLocationURL;

	private EView(@Nonnull final String aFXMLLocationURL) {
	    m_fFXMLLocationURL = aFXMLLocationURL;
	}

	@Nonnull
	public String getFXMLLocationURL() {
	    return m_fFXMLLocationURL;
	}
    }

    private static Stage m_sPrimaryStage;
    private static Map<EView, Scene> m_sSceneList;

    static {
	m_sSceneList = new HashedMap<EView, Scene>();
    }

    private ViewManager() {
    }

    public static void setPrimaryStage(@Nonnull final Stage aPrimaryStage) {
	if (aPrimaryStage == null)
	    throw new NullPointerException("primary stage");

	m_sPrimaryStage = aPrimaryStage;
    }

    private static void _addView(@Nonnull final EView aView) {
	final Scene aScene = SceneLoader.load(ViewManager.class.getClassLoader().getResource(aView.getFXMLLocationURL()));

	m_sSceneList.put(aView, aScene);
    }

    public static void setView(@Nonnull final EView aView) {
	if (m_sPrimaryStage == null)
	    throw new NullPointerException("primary stage");
	if (aView == null)
	    throw new NullPointerException("view");

	if (!m_sSceneList.containsKey(aView))
	    _addView(aView);

	final Scene aScene = m_sSceneList.get(aView);

	m_sPrimaryStage.setMaxHeight(Value.WINDOW_HEIGHT_DEFAULT);
	m_sPrimaryStage.setMinHeight(Value.WINDOW_HEIGHT_DEFAULT);
	m_sPrimaryStage.setScene(aScene);
    }
}
