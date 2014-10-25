package at.ac.tuwien.media.master.transcoderui.component;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import javax.annotation.Nonnegative;

import at.ac.tuwien.media.master.commons.IOnRemoveElementListener;
import at.ac.tuwien.media.master.transcoderui.util.Value;

public class RemoveableCellListView extends ListView<String> implements IOnRemoveElementListener {
    private final ObservableList<String> f_aItems;

    private void _wrapListAroundElements() {
	final double nListSize = f_aItems.size();
	final double nListHeigth = nListSize > 8 ? Value.WINDOW_HEIGHT_DEFAULT - 83 : nListSize * 32 + 2;
	setPrefHeight(nListHeigth);
    }

    public RemoveableCellListView(final ObservableList<String> aItems) {
	super(aItems);

	f_aItems = aItems;
	setCellFactory(param -> new RemoveableListCell(this));

	_wrapListAroundElements();
    }

    @Override
    public void onRemoveElement(@Nonnegative final int nIndex) {
	f_aItems.remove(nIndex);
	setItems(f_aItems);

	_wrapListAroundElements();
    }
}
