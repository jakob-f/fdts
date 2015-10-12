package at.frohnwieser.mahut.client.controller;

import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.annotation.Nonnull;

import at.frohnwieser.mahut.client.util.SceneUtils;
import at.frohnwieser.mahut.client.util.SceneUtils.EView;
import at.frohnwieser.mahut.client.util.Value;

public class ViewManager {
    private static ViewManager m_aInstance = new ViewManager();
    private static Stage s_aPrimaryStage;

    private ViewManager() {
    }

    public static ViewManager getInstance() {
	return m_aInstance;
    }

    public void setPrimaryStage(@Nonnull final Stage aPrimaryStage) {
	if (aPrimaryStage == null)
	    throw new NullPointerException("primary stage");

	s_aPrimaryStage = aPrimaryStage;
	s_aPrimaryStage.setResizable(false);
	s_aPrimaryStage.getIcons().add(new Image(Value.LOGO_PATH));
	s_aPrimaryStage.setTitle("Mahut Client");
    }

    public void setView(@Nonnull final EView aView) {
	if (s_aPrimaryStage == null)
	    throw new NullPointerException("primary stage");

	s_aPrimaryStage.setScene(SceneUtils.getInstance().getScene(aView));
    }
}
