package at.frohnwieser.mahut.transcoderui.component;

import java.io.File;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import javax.annotation.Nonnegative;

import at.frohnwieser.mahut.commons.IOnRemoveCallback;
import at.frohnwieser.mahut.transcoderui.util.Value;

public class RemoveableFileCellListView extends ListView<File> implements IOnRemoveCallback {
    private final ObservableList<File> f_aItems;

    private void _wrapListAroundElements() {
	final double nListSize = f_aItems.size();
	final double nListHeigth = nListSize >= 7 ? Value.WINDOW_HEIGHT_DEFAULT - 129 : nListSize * 32 + 2;
	setPrefHeight(nListHeigth);
    }

    public RemoveableFileCellListView(final ObservableList<File> aItems) {
	super(aItems);

	f_aItems = aItems;
	setCellFactory(param -> new RemoveableFileListCell(this));

	_wrapListAroundElements();
    }

    @Override
    public void onRemove(@Nonnegative final int nIndex) {
	f_aItems.remove(nIndex);

	_wrapListAroundElements();
    }
}
