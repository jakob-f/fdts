package at.frohnwieser.mahut.transcoderui;

import java.io.File;

import javafx.application.Application;
import javafx.stage.Stage;

import javax.annotation.Nonnull;

import at.frohnwieser.mahut.transcoderui.controller.ViewManager;
import at.frohnwieser.mahut.transcoderui.util.Value;
import at.frohnwieser.mahut.transcoderui.util.SceneUtils.EView;

public class MainTranscoderUI extends Application {
    @Override
    public void start(@Nonnull final Stage aPrimaryStage) throws Exception {
	// get View
	EView aView;
	if (new File(Value.FILEPATH_ROPERTIES).exists())
	    aView = EView.MAIN;
	else
	    aView = EView.SETTINGS;

	// set up view manager
	ViewManager.getInstance().setPrimaryStage(aPrimaryStage);
	ViewManager.getInstance().setView(aView);
	// finally show all
	aPrimaryStage.show();
    }

    public static void main(@Nonnull final String[] args) {
	launch(args);
    }
}