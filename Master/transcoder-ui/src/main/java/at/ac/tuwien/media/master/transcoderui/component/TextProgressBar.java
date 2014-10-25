package at.ac.tuwien.media.master.transcoderui.component;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

import at.ac.tuwien.media.master.commons.IOnCompleteNotifyListener;
import at.ac.tuwien.media.master.commons.ISetProgress;
import at.ac.tuwien.media.master.commons.ISetText;
import at.ac.tuwien.media.master.transcoderui.util.Value;

public class TextProgressBar extends Parent implements ISetProgress, ISetText, IOnCompleteNotifyListener {
    private final SetProgressProgressBar f_aProgressBar;
    private final Text f_aProgressPercentageText;
    private final Text f_aProgressStatusText;
    private String m_sInsertableProgressText;
    private String m_sCompletedText;

    public TextProgressBar() {
	f_aProgressBar = new SetProgressProgressBar();
	f_aProgressPercentageText = new Text();
	f_aProgressStatusText = new Text();

	f_aProgressBar.setProgress(0);
	f_aProgressStatusText.getStyleClass().add("text-progressStatus");

	final Pane aStackPane = new StackPane(f_aProgressBar, f_aProgressPercentageText);
	this.getChildren().addAll(new VBox(aStackPane, f_aProgressStatusText));
    }

    public void setCompletedText(@Nullable final String sCompletedText) {
	m_sCompletedText = sCompletedText;
    }

    public void setInsertableProgressText(@Nullable final String sInsertableProgressText) {
	m_sInsertableProgressText = sInsertableProgressText;
    }

    public void setWidth(@Nullable final double nWidth) {
	f_aProgressBar.setMaxWidth(nWidth);
	f_aProgressBar.setMinWidth(nWidth);
    }

    @Override
    public void onThreadComplete(final Thread thread) {
	Platform.runLater(() -> f_aProgressPercentageText.setText("100%"));
	Platform.runLater(() -> f_aProgressStatusText.setText(m_sCompletedText));
    }

    @Override
    public void setText(final String sText) {
	if (m_sInsertableProgressText != null)
	    Platform.runLater(() -> f_aProgressStatusText.setText(m_sInsertableProgressText.replace(Value.PLACEHOLDER, sText)));
    }

    @Override
    public void setProgress(@Nonnegative final double nValue) {
	Platform.runLater(() -> f_aProgressBar.setProgress(nValue));
	Platform.runLater(() -> f_aProgressPercentageText.setText((int) (nValue * 100) + "%"));
    }
}
