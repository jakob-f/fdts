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
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;

import at.ac.tuwien.media.master.commons.IOnRemoveCallback;
import at.ac.tuwien.media.master.transcoderui.component.RemoveableFileCellListView;
import at.ac.tuwien.media.master.transcoderui.util.Value;

public class ViewFilelistController {
    @FXML
    Text titleText;
    @FXML
    VBox centerVBox;
    @FXML
    Text countText;

    private Collection<IOnRemoveCallback> m_aOnRemoveCallbacks;
    private Collection<File> m_aFiles;
    private String m_sInsertableCountText;

    private void _close() {
	((Popup) centerVBox.getScene().getWindow()).hide();
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

    private void _updateCountText() {
	if (m_sInsertableCountText != null)
	    countText.setText(m_sInsertableCountText.replace(Value.PLACEHOLDER, String.valueOf(m_aFiles.size())));
    }

    @FXML
    protected void onClickClose(@Nonnull final ActionEvent aActionEvent) {
	_close();
    }

    @FXML
    protected void onClickClearList(@Nonnull final ActionEvent aActionEvent) {
	m_aFiles.clear();

	_notifyOnRemove(0);
	_close();
    }

    public void setTitleText(@Nonnull final String sTitleText) {
	titleText.setText(sTitleText);
    }

    public void setInsertableCountText(@Nullable final String sInsertableCountText) {
	m_sInsertableCountText = sInsertableCountText;
    }

    public void setFiles(@Nonnull final Collection<File> aFiles) {
	if (CollectionUtils.isEmpty(aFiles))
	    throw new NullPointerException("files");

	m_aFiles = aFiles;

	final ObservableList<File> aItems = FXCollections.observableList(new ArrayList<File>(m_aFiles));
	// add remove callback
	aItems.addListener((ListChangeListener<File>) aChange -> {
	    while (aChange.next())
		if (aChange.wasRemoved()) {
		    int i = 0;
		    for (final Iterator<File> aIterator = m_aFiles.iterator(); aIterator.hasNext();) {
			aIterator.next();
			if (i++ == aChange.getFrom()) {
			    aIterator.remove();
			    _updateCountText();
			    _notifyOnRemove(i);

			    break;
			}
		    }
		}

	    if (CollectionUtils.isEmpty(m_aFiles))
		_close();
	});

	centerVBox.getChildren().clear();
	centerVBox.getChildren().addAll(new RemoveableFileCellListView(aItems));
	_updateCountText();
    }
}
