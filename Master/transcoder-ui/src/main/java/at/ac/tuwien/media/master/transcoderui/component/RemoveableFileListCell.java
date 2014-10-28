package at.ac.tuwien.media.master.transcoderui.component;

import java.io.File;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.commons.IOnRemoveCallback;

public class RemoveableFileListCell extends ListCell<File> {
    private final HBox f_aHBox = new HBox();
    private final Label f_aLabel = new Label();

    public RemoveableFileListCell(@Nonnull final IOnRemoveCallback aRemoveElementListener) {
	final Pane aPane = new Pane();
	HBox.setHgrow(aPane, Priority.ALWAYS);

	final Hyperlink aHyperlink = new Hyperlink("✗");
	aHyperlink.setOnAction(event -> aRemoveElementListener.onRemove(getIndex()));

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
	    setGraphic(f_aHBox);
	}
    }

}
