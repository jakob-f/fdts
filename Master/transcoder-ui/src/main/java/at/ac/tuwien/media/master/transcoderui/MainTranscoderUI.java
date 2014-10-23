package at.ac.tuwien.media.master.transcoderui;

import java.io.File;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.annotation.Nonnull;

import at.ac.tuwien.media.master.transcoderui.controller.ViewManager;
import at.ac.tuwien.media.master.transcoderui.controller.ViewManager.EView;
import at.ac.tuwien.media.master.transcoderui.util.Value;

public class MainTranscoderUI extends Application {
    @Override
    public void start(@Nonnull final Stage aPrimaryStage) throws Exception {
	// get View
	EView aView;
	if (new File(Value.PROPERTIES_PATH).exists())
	    aView = EView.MAIN;
	else
	    aView = EView.SETTINGS;

	// set up primary stage
	aPrimaryStage.setResizable(false);
	aPrimaryStage.getIcons().add(new Image("./images/045.jpg"));
	aPrimaryStage.setTitle("Transcoder UI");
	ViewManager.setPrimaryStage(aPrimaryStage);
	ViewManager.setView(aView);

	// finally show all
	aPrimaryStage.show();
    }

    public static void main(@Nonnull final String[] args) {
	launch(args);
    }
}