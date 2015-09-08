package at.frohnwieser.mahut.mahutclient.controller;

import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;
import javafx.stage.Popup;
import javafx.stage.Stage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.frohnwieser.mahut.mahutclient.util.SceneUtils;
import at.frohnwieser.mahut.mahutclient.util.Value;
import at.frohnwieser.mahut.mahutclient.util.SceneUtils.EView;

public class ViewManager {
    public enum EPosition {
	BOTTOM,
	LEFT,
	RIGHT,
	TOP;
    }

    private static ViewManager m_aInstance = new ViewManager();
    private static Stage s_aPrimaryStage;
    private static double s_nWindowPosX;
    private static double s_nWindowPosY;
    private static Popup s_aPopupBottom;
    private static Popup s_aPopupLeft;
    private static Popup s_aPopupRight;
    private static Popup s_aPopupTop;

    private ViewManager() {
	s_aPopupBottom = new Popup();
	s_aPopupLeft = new Popup();
	s_aPopupRight = new Popup();
	s_aPopupTop = new Popup();
    }

    public static ViewManager getInstance() {
	return m_aInstance;
    }

    private void _updatePopupPositions() {
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

    public void setPrimaryStage(@Nonnull final Stage aPrimaryStage) {
	if (aPrimaryStage == null)
	    throw new NullPointerException("primary stage");

	s_aPrimaryStage = aPrimaryStage;
	s_aPrimaryStage.setResizable(false);
	s_aPrimaryStage.getIcons().add(new Image(Value.LOGO_PATH));
	s_aPrimaryStage.setTitle("Transcoder UI");
	// callbacks on window move & resize
	s_aPrimaryStage.xProperty().addListener((ChangeListener<Number>) (aValue, nOldX, nNewX) -> {
	    s_nWindowPosX = (double) nNewX;
	    _updatePopupPositions();
	});
	s_aPrimaryStage.yProperty().addListener((ChangeListener<Number>) (aValue, nOldY, nNewY) -> {
	    s_nWindowPosY = (double) nNewY;
	    _updatePopupPositions();
	});
	s_aPrimaryStage.heightProperty().addListener((ChangeListener<Number>) (aValue, nOldHeight, nNewHeight) -> _updatePopupPositions());
	s_aPrimaryStage.focusedProperty().addListener((ChangeListener<Boolean>) (aValue, bOldFocused, bNewFocused) -> {
	    if (!bNewFocused)
		hideAllPopups();
	});

	s_nWindowPosX = s_aPrimaryStage.getX();
	s_nWindowPosX = s_aPrimaryStage.getY();
    }

    public void setView(@Nonnull final EView aView) {
	if (s_aPrimaryStage == null)
	    throw new NullPointerException("primary stage");

	s_aPrimaryStage.setMaxHeight(Value.WINDOW_HEIGHT_DEFAULT);
	s_aPrimaryStage.setMinHeight(Value.WINDOW_HEIGHT_DEFAULT);
	s_aPrimaryStage.setScene(SceneUtils.getInstance().getScene(aView));
    }

    @Nullable
    private Popup _getPopupForPosition(@Nonnull final EPosition aPosition) {
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

    public void showPopup(@Nonnull final EView aView, @Nonnull final EPosition aPosition) {
	if (s_aPrimaryStage == null)
	    throw new NullPointerException("primary stage");

	final Popup aPopup = _getPopupForPosition(aPosition);

	if (aPopup != null && !aPopup.isShowing()) {
	    aPopup.getContent().clear();
	    // aPopup. // XXX Always on TOP
	    aPopup.getContent().addAll(SceneUtils.getInstance().getPane(aView));
	    aPopup.show(s_aPrimaryStage);

	    _updatePopupPositions();
	}
    }

    public void hidePopup(@Nonnull final EPosition aPosition) {
	final Popup aPopup = _getPopupForPosition(aPosition);

	if (aPopup != null)
	    aPopup.hide();
    }

    public void hideAllPopups() {
	if (s_aPopupBottom != null)
	    s_aPopupBottom.hide();
	if (s_aPopupLeft != null)
	    s_aPopupLeft.hide();
	if (s_aPopupRight != null)
	    s_aPopupRight.hide();
	if (s_aPopupTop != null)
	    s_aPopupTop.hide();
    }

    public boolean isPopupShowing(@Nonnull final EPosition aPosition) {
	final Popup aPopup = _getPopupForPosition(aPosition);

	return aPopup == null || aPopup.isShowing();
    }
}