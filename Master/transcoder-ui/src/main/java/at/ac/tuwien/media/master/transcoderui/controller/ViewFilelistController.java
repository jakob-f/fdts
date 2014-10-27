package at.ac.tuwien.media.master.transcoderui.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;

import at.ac.tuwien.media.master.transcoderui.component.RemoveableFileCellListView;

public class ViewFilelistController {
    @FXML
    VBox centerVBox;

    private void _close() {
	((Popup) centerVBox.getScene().getWindow()).hide();
    }

    @FXML
    protected void onClickClose(@Nonnull final ActionEvent aActionEvent) {
	_close();
    }

    public void setFiles(@Nonnull final Collection<File> aFiles) {
	if (CollectionUtils.isEmpty(aFiles))
	    throw new NullPointerException("files");

	final List<File> aFileList = new ArrayList<File>(aFiles);
	final ObservableList<File> aItems = FXCollections.observableList(aFileList);
	// add remove callback
	aItems.addListener((ListChangeListener<File>) aChange -> {
	    while (aChange.next())
		if (aChange.wasRemoved()) {
		    int i = 0;
		    for (final Iterator<File> aIterator = aFiles.iterator(); aIterator.hasNext();) {
			aIterator.next();
			if (i++ == aChange.getFrom()) {
			    aIterator.remove();
			    break;
			}
		    }
		}

	    if (aFileList.size() <= 0)
		_close();
	});

	centerVBox.getChildren().clear();
	centerVBox.getChildren().addAll(new RemoveableFileCellListView(aItems));
    }
}
