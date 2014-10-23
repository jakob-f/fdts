package at.ac.tuwien.media.master.transcoderui.controller;

import java.util.Map;

import javafx.scene.Scene;
import javafx.stage.Popup;
import javafx.stage.Stage;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.apache.commons.collections4.map.HashedMap;

import at.ac.tuwien.media.master.transcoderui.util.SceneLoader;
import at.ac.tuwien.media.master.transcoderui.util.Value;

public final class ViewManager {
    public enum EView {
	FILELIST(Value.FXML_FILELIST), MAIN(Value.FXML_MAIN), SETTINGS(Value.FXML_SETTINGS);

	private final String m_fFXMLLocation;

	private EView(@Nonnull final String aFXMLLocation) {
	    m_fFXMLLocation = aFXMLLocation;
	}

	@Nonnull
	public String getFXMLLocation() {
	    return m_fFXMLLocation;
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
	final Scene aScene = SceneLoader.loadAsScene(ViewManager.class.getClassLoader().getResource(aView.getFXMLLocation()));

	m_sSceneList.put(aView, aScene);
    }

    private static Scene _getScene(@Nonnull final EView aView) {
	if (m_sPrimaryStage == null)
	    throw new NullPointerException("primary stage");
	if (aView == null)
	    throw new NullPointerException("view");

	if (!m_sSceneList.containsKey(aView))
	    _addView(aView);

	return m_sSceneList.get(aView);
    }

    public static void setView(@Nonnull final EView aView) {
	if (m_sPrimaryStage == null)
	    throw new NullPointerException("primary stage");

	m_sPrimaryStage.setMaxHeight(Value.WINDOW_HEIGHT_DEFAULT);
	m_sPrimaryStage.setMinHeight(Value.WINDOW_HEIGHT_DEFAULT);
	m_sPrimaryStage.setScene(_getScene(aView));
    }

    public static void showPopup(@Nonnull final EView aView, @Nonnegative final double nX, @Nonnegative final double nY) {
	if (m_sPrimaryStage == null)
	    throw new NullPointerException("primary stage");

	final Popup aPopup = new Popup();
	aPopup.setX(nX + Value.WINDOW_WIDTH);
	aPopup.setY(nY + 23);
	aPopup.getContent().addAll(SceneLoader.loadAsPane(ViewManager.class.getClassLoader().getResource(aView.getFXMLLocation())));
	aPopup.show(m_sPrimaryStage);
    }
}
