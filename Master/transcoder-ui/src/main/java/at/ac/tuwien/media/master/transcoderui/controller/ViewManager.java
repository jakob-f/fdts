package at.ac.tuwien.media.master.transcoderui.controller;

import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Popup;
import javafx.stage.Stage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.map.HashedMap;

import at.ac.tuwien.media.master.transcoderui.util.SceneLoader;
import at.ac.tuwien.media.master.transcoderui.util.Value;

public final class ViewManager {
    public enum EView {
	FILELIST(Value.FXML_FILELIST), MAIN(Value.FXML_MAIN), PROGRESSBARS(Value.FXML_PROGRESSBARS), SETTINGS(Value.FXML_SETTINGS);

	private final String f_sFXMLLocation;

	private EView(@Nonnull final String aFXMLLocation) {
	    f_sFXMLLocation = aFXMLLocation;
	}

	@Nonnull
	public String getFXMLLocation() {
	    return f_sFXMLLocation;
	}
    }

    public enum EPosition {
	BOTTOM, LEFT, RIGHT, TOP;
    }

    private static Stage s_aPrimaryStage;
    private static Map<EView, Scene> s_aSceneList;
    private static double s_nWindowPosX;
    private static double s_nWindowPosY;
    private static Popup s_aPopupBottom;
    private static Popup s_aPopupLeft;
    private static Popup s_aPopupRight;
    private static Popup s_aPopupTop;

    static {
	s_aSceneList = new HashedMap<EView, Scene>();
	s_aPopupBottom = new Popup();
	s_aPopupLeft = new Popup();
	s_aPopupRight = new Popup();
	s_aPopupTop = new Popup();
    }

    private ViewManager() {
    }

    private static void _updatePopupPositions() {
	if (s_aPopupBottom != null) {
	    s_aPopupBottom.setX(s_nWindowPosX - 5);
	    s_aPopupBottom.setY(s_nWindowPosY + s_aPrimaryStage.getHeight());
	}
	if (s_aPopupLeft != null) {
	    s_aPopupLeft.setX(s_nWindowPosX - s_aPopupLeft.getWidth() + 8);
	    s_aPopupLeft.setY(s_nWindowPosY + 23);
	}
	if (s_aPopupRight != null) {
	    s_aPopupRight.setX(s_nWindowPosX + s_aPrimaryStage.getWidth());
	    s_aPopupRight.setY(s_nWindowPosY + 23);
	}
	if (s_aPopupTop != null) {
	    s_aPopupTop.setX(s_nWindowPosX - 5);
	    s_aPopupTop.setY(s_nWindowPosY - s_aPopupTop.getHeight() + 8);
	}
    }

    public static void setPrimaryStage(@Nonnull final Stage aPrimaryStage) {
	if (aPrimaryStage == null)
	    throw new NullPointerException("primary stage");

	s_aPrimaryStage = aPrimaryStage;
	s_aPrimaryStage.setResizable(false);
	s_aPrimaryStage.getIcons().add(new Image("./images/045.jpg"));
	s_aPrimaryStage.setTitle("Transcoder UI");
	// callbacks on window move & resize
	s_aPrimaryStage.xProperty().addListener((ChangeListener<Number>) (value, oldXValue, newXValue) -> {
	    s_nWindowPosX = (double) newXValue;
	    _updatePopupPositions();
	});
	s_aPrimaryStage.yProperty().addListener((ChangeListener<Number>) (value, oldYValue, newYValue) -> {
	    s_nWindowPosY = (double) newYValue;
	    _updatePopupPositions();
	});
	s_aPrimaryStage.heightProperty().addListener((ChangeListener<Number>) (value, oldYValue, newYValue) -> _updatePopupPositions());

	s_nWindowPosX = s_aPrimaryStage.getX();
	s_nWindowPosX = s_aPrimaryStage.getY();
    }

    private static void _addView(@Nonnull final EView aView) {
	final Scene aScene = SceneLoader.loadAsScene(ViewManager.class.getClassLoader().getResource(aView.getFXMLLocation()));

	s_aSceneList.put(aView, aScene);
    }

    private static Scene _getScene(@Nonnull final EView aView) {
	if (s_aPrimaryStage == null)
	    throw new NullPointerException("primary stage");
	if (aView == null)
	    throw new NullPointerException("view");

	if (!s_aSceneList.containsKey(aView))
	    _addView(aView);

	return s_aSceneList.get(aView);
    }

    public static void setView(@Nonnull final EView aView) {
	if (s_aPrimaryStage == null)
	    throw new NullPointerException("primary stage");

	s_aPrimaryStage.setMaxHeight(Value.WINDOW_HEIGHT_DEFAULT);
	s_aPrimaryStage.setMinHeight(Value.WINDOW_HEIGHT_DEFAULT);
	s_aPrimaryStage.setScene(_getScene(aView));
    }

    @Nullable
    private static Popup _getPopupForPosition(@Nonnull final EPosition aPosition) {
	if (aPosition == null)
	    throw new NullPointerException("position");

	switch (aPosition) {
	case BOTTOM:
	    return s_aPopupBottom;
	case LEFT:
	    return s_aPopupLeft;
	case RIGHT:
	    return s_aPopupRight;
	case TOP:
	    return s_aPopupTop;
	default:
	    return null;
	}
    }

    public static void showPopup(@Nonnull final EView aView, @Nonnull final EPosition aPosition) {
	if (s_aPrimaryStage == null)
	    throw new NullPointerException("primary stage");

	final Popup aPopup = _getPopupForPosition(aPosition);

	if (aPopup != null && !aPopup.isShowing()) {
	    aPopup.getContent().clear();
	    // aPopup. // XXX Always on TOP
	    aPopup.getContent().addAll(SceneLoader.loadAsPane(ViewManager.class.getClassLoader().getResource(aView.getFXMLLocation())));
	    aPopup.show(s_aPrimaryStage);

	    _updatePopupPositions();
	}
    }

    public static void closePopup(@Nonnull final EPosition aPosition) {
	final Popup aPopup = _getPopupForPosition(aPosition);

	if (aPopup != null)
	    aPopup.hide();
    }

    public static boolean isPopupShowing(@Nonnull final EPosition aPosition) {
	final Popup aPopup = _getPopupForPosition(aPosition);

	return aPopup == null || aPopup.isShowing();
    }
}
