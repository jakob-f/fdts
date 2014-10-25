package at.ac.tuwien.media.master.transcoderui.component;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

import at.ac.tuwien.media.master.commons.IOnCompleteNotifyListener;
import at.ac.tuwien.media.master.commons.ISetProgress;
import at.ac.tuwien.media.master.commons.ISetText;
import at.ac.tuwien.media.master.transcoderui.util.Value;

public class TextProgressBar extends Parent implements ISetProgress, ISetText, IOnCompleteNotifyListener {
    private final ProgressBar f_aProgressBar;
    private final Text f_aPercentageText;
    private final Text f_aStatusText;
    private String m_sInsertableProgressText;
    private String m_sCompletedText;

    public TextProgressBar() {
	f_aProgressBar = new ProgressBar(0);
	f_aPercentageText = new Text();
	f_aStatusText = new Text();

	f_aPercentageText.getStyleClass().add("text-progressBar");
	f_aStatusText.getStyleClass().add("text-progressBar");

	final HBox aStatusTextHBox = new HBox(f_aStatusText);
	HBox.setHgrow(aStatusTextHBox, Priority.ALWAYS);
	aStatusTextHBox.setAlignment(Pos.CENTER_LEFT);
	aStatusTextHBox.setPadding(new Insets(0, 0, 0, 10));

	final HBox aPercentageTextHBox = new HBox(f_aPercentageText);
	HBox.setHgrow(aPercentageTextHBox, Priority.ALWAYS);
	aPercentageTextHBox.setAlignment(Pos.CENTER_RIGHT);
	aPercentageTextHBox.setPadding(new Insets(0, 0, 0, 20));

	this.getChildren().addAll(new HBox(new StackPane(f_aProgressBar, aStatusTextHBox), aPercentageTextHBox));
    }

    public void setCompletedText(@Nullable final String sCompletedText) {
	m_sCompletedText = sCompletedText;
    }

    public void setInsertableProgressText(@Nullable final String sInsertableProgressText) {
	m_sInsertableProgressText = sInsertableProgressText;
    }

    public void setHeight(@Nullable final double nHeight) {
	f_aProgressBar.setMaxHeight(nHeight);
	f_aProgressBar.setMinHeight(nHeight);
    }

    public void setWidth(@Nullable final double nWidth) {
	f_aProgressBar.setMaxWidth(nWidth);
	f_aProgressBar.setMinWidth(nWidth);
    }

    public void setSize(@Nullable final double nWidth, @Nullable final double nHeight) {
	setHeight(nHeight);
	setWidth(nWidth);
    }

    @Override
    public void onThreadComplete(final Thread thread) {
	Platform.runLater(() -> f_aPercentageText.setText("100%"));
	Platform.runLater(() -> f_aStatusText.setText(m_sCompletedText));
    }

    @Override
    public void setText(final String sText) {
	if (m_sInsertableProgressText != null)
	    Platform.runLater(() -> f_aStatusText.setText(m_sInsertableProgressText.replace(Value.PLACEHOLDER, sText)));
    }

    @Override
    public void setProgress(@Nonnegative final double nValue) {
	Platform.runLater(() -> f_aProgressBar.setProgress(nValue));
	Platform.runLater(() -> f_aPercentageText.setText((int) (nValue * 100) + "%"));
    }
}
