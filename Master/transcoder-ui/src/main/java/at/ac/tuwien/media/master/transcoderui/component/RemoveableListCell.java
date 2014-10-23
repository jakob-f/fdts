package at.ac.tuwien.media.master.transcoderui.component;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.commons.IOnRemoveElementListener;

public class RemoveableListCell extends ListCell<String> {
    private final HBox m_aHBox = new HBox();
    private final Label m_aLabel = new Label();

    public RemoveableListCell(@Nonnull final IOnRemoveElementListener aRemoveElementListener) {
	super();
	final Pane aPane = new Pane();
	HBox.setHgrow(aPane, Priority.ALWAYS);
	final Hyperlink aHyperlink = new Hyperlink("✗");
	aHyperlink.setOnAction(event -> {
	    aRemoveElementListener.onRemoveElement(getIndex());
	});

	m_aHBox.getChildren().addAll(m_aLabel, aPane, aHyperlink);
    }

    @Override
    protected void updateItem(final String sText, final boolean bIsEmpty) {
	super.updateItem(sText, bIsEmpty);
	setText(null);

	if (bIsEmpty)
	    setGraphic(null);
	else {
	    m_aLabel.setText(StringUtils.isNotEmpty(sText) ? sText : "-");
	    setGraphic(m_aHBox);
	}
    }

}
