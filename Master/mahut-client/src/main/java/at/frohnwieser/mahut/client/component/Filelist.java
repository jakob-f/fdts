package at.frohnwieser.mahut.client.component;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;

import at.frohnwieser.mahut.commons.CommonValue;
import at.frohnwieser.mahut.commons.IOnRemoveCallback;

public class Filelist {
    private final HBox m_aHBox;
    private Collection<IOnRemoveCallback> m_aOnRemoveCallbacks;
    private Collection<File> m_aFiles;
    private final String m_sInsertableCountText;
    private final String m_sInsertableClearListText;

    public Filelist(@Nonnull final HBox aHBox, @Nonnull final String sInsertableCountText, @Nonnull final String sInsertableClearListText) {
	m_aHBox = aHBox;
	m_sInsertableCountText = sInsertableCountText;
	m_sInsertableClearListText = sInsertableClearListText;
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
	    m_aOnRemoveCallbacks.forEach(aOnRemoveCallback -> aOnRemoveCallback.onRemove(nIndex));
    }

    protected void clearList() {
	m_aFiles.clear();
	_notifyOnRemove(-1);
    }

    public void setFiles(@Nonnull final Collection<File> aFiles) {
	if (CollectionUtils.isNotEmpty(aFiles)) {
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
				_notifyOnRemove(i);
				break;
			    }
			}
		    }
	    });

	    final RemoveableFileCellListView aList = new RemoveableFileCellListView(aItems);
	    HBox.setHgrow(aList, Priority.ALWAYS);
	    final Text aCountText = new Text(m_sInsertableCountText.replace(CommonValue.PLACEHOLDER, String.valueOf(m_aFiles.size())));
	    aCountText.getStyleClass().add("text-small");
	    final Hyperlink aClearLink = new Hyperlink(m_sInsertableClearListText);
	    aClearLink.setOnMouseClicked(aEvent -> clearList());
	    aClearLink.getStyleClass().add("text-small");
	    final VBox aVBox2 = new VBox(aCountText);
	    HBox.setHgrow(aVBox2, Priority.ALWAYS);
	    final VBox aVBox = new VBox(new HBox(aList), new HBox(aVBox2, aClearLink));
	    HBox.setHgrow(aVBox, Priority.ALWAYS);
	    m_aHBox.getChildren().clear();
	    m_aHBox.getChildren().add(aVBox);
	}
    }
}
