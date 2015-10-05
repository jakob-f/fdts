package at.frohnwieser.mahut.client;

import java.io.File;
import java.security.CodeSource;

import javafx.application.Application;
import javafx.stage.Stage;

import javax.annotation.Nonnull;

import at.frohnwieser.mahut.client.config.Configuration;
import at.frohnwieser.mahut.client.config.Configuration.EField;
import at.frohnwieser.mahut.client.controller.ViewManager;
import at.frohnwieser.mahut.client.util.SceneUtils.EView;
import at.frohnwieser.mahut.client.util.Value;
import at.frohnwieser.mahut.ffmpegwrapper.util.FFMPEGCall;
import at.frohnwieser.mahut.ffmpegwrapper.util.FFPROBECall;

public class MainClientUI extends Application {

    @Override
    public void start(@Nonnull final Stage aPrimaryStage) throws Exception {
	// get View
	EView aView;
	if (new File(Value.FILEPATH_PROPERTIES).exists())
	    aView = EView.MAIN;
	else
	    aView = EView.SETTINGS;

	// TODO migration code
	if (!Configuration.getInstance().getAsBoolean(EField.STATIC_FFMPEG))
	    Configuration.getInstance().set(EField.STATIC_FFMPEG, true);

	if (Configuration.getInstance().getAsBoolean(EField.STATIC_FFMPEG)) {
	    final CodeSource codeSource = MainClientUI.class.getProtectionDomain().getCodeSource();
	    final File jarFile = new File(codeSource.getLocation().toURI().getPath());
	    FFMPEGCall.setAlternativeEnvironment(jarFile.getParentFile().getAbsolutePath() + File.separator + "lib/ffmpeg");
	    FFPROBECall.setAlternativeEnvironment(jarFile.getParentFile().getAbsolutePath() + File.separator + "lib/ffprobe");
	}

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