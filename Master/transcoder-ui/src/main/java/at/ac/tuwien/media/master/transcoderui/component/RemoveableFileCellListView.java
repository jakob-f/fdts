package at.ac.tuwien.media.master.transcoderui.component;

import java.io.File;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import javax.annotation.Nonnegative;

import at.ac.tuwien.media.master.commons.IOnRemoveElementListener;
import at.ac.tuwien.media.master.transcoderui.util.Value;

public class RemoveableFileCellListView extends ListView<File> implements IOnRemoveElementListener {
    private final ObservableList<File> f_aItems;

    private void _wrapListAroundElements() {
	final double nListSize = f_aItems.size();
	final double nListHeigth = nListSize >= 8 ? Value.WINDOW_HEIGHT_DEFAULT - 113 : nListSize * 32 + 2;
	setPrefHeight(nListHeigth);
    }

    public RemoveableFileCellListView(final ObservableList<File> aItems) {
	super(aItems);

	f_aItems = aItems;
	setCellFactory(param -> new RemoveableFileListCell(this));

	_wrapListAroundElements();
    }

    @Override
    public void onRemoveElement(@Nonnegative final int nIndex) {
	f_aItems.remove(nIndex);

	_wrapListAroundElements();
    }
}
