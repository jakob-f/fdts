package at.ac.tuwien.media.master.transcoderui.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;

import at.ac.tuwien.media.master.commons.IOnRemoveCallback;
import at.ac.tuwien.media.master.transcoderui.component.RemoveableFileCellListView;

public class ViewFilelistController {
    @FXML
    Text titleText;
    @FXML
    VBox centerVBox;

    private Collection<IOnRemoveCallback> m_aOnRemoveCallbacks;

    private void _close() {
	((Popup) centerVBox.getScene().getWindow()).hide();
    }

    @FXML
    protected void onClickClose(@Nonnull final ActionEvent aActionEvent) {
	_close();
    }

    public void setTitle(@Nonnull final String sTitle) {
	titleText.setText(sTitle);
    }

    public void addOnRemoveCallback(@Nonnull final IOnRemoveCallback aOnRemoveCallback) {
	if (aOnRemoveCallback == null)
	    throw new NullPointerException("callback");

	if (m_aOnRemoveCallbacks == null)
	    m_aOnRemoveCallbacks = new ArrayList<IOnRemoveCallback>();

	m_aOnRemoveCallbacks.add(aOnRemoveCallback);
    }

    private void _notifyOnRemove(final int nIndex) {
	if (CollectionUtils.isNotEmpty(m_aOnRemoveCallbacks))
	    for (final IOnRemoveCallback aOnRemoveCallback : m_aOnRemoveCallbacks)
		aOnRemoveCallback.onRemove(nIndex);
    }

    public void setFiles(@Nonnull final Collection<File> aFiles) {
	if (CollectionUtils.isEmpty(aFiles))
	    throw new NullPointerException("files");

	final ObservableList<File> aItems = FXCollections.observableList(new ArrayList<File>(aFiles));
	// add remove callback
	aItems.addListener((ListChangeListener<File>) aChange -> {
	    while (aChange.next())
		if (aChange.wasRemoved()) {
		    int i = 0;
		    for (final Iterator<File> aIterator = aFiles.iterator(); aIterator.hasNext();) {
			aIterator.next();
			if (i++ == aChange.getFrom()) {
			    aIterator.remove();
			    _notifyOnRemove(i);

			    break;
			}
		    }
		}

	    if (CollectionUtils.isEmpty(aFiles))
		_close();
	});

	centerVBox.getChildren().clear();
	centerVBox.getChildren().addAll(new RemoveableFileCellListView(aItems));
    }
}
