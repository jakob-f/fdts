package at.frohnwieser.mahut.client.component;

import java.io.File;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.commons.IOnRemoveCallback;

public class RemoveableFileListCell extends ListCell<File> {
    private final HBox f_aHBox = new HBox();
    private final Label f_aLabel = new Label();

    public RemoveableFileListCell(@Nonnull final IOnRemoveCallback aRemoveElementListener) {
	final Pane aPane = new Pane();
	HBox.setHgrow(aPane, Priority.ALWAYS);

	final Hyperlink aHyperlink = new Hyperlink("✗");
	aHyperlink.setOnAction(aEvent -> aRemoveElementListener.onRemove(getIndex()));

	f_aHBox.getChildren().addAll(f_aLabel, aPane, aHyperlink);
    }

    @Override
    protected void updateItem(final File aFile, final boolean bIsEmpty) {
	super.updateItem(aFile, bIsEmpty);
	setText(null);

	if (bIsEmpty)
	    setGraphic(null);
	else {
	    f_aLabel.setText(aFile != null ? StringUtils.abbreviateMiddle(aFile.getAbsolutePath(), "...", 42) : "-");
	    f_aLabel.setTextFill(Color.BLACK);
	    setGraphic(f_aHBox);
	}
    }

}
