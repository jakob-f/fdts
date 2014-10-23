package at.ac.tuwien.media.master.transcoderui.component;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import javax.annotation.Nonnegative;

import at.ac.tuwien.media.master.commons.IOnRemoveElementListener;
import at.ac.tuwien.media.master.transcoderui.util.Value;

public class RemoveableCellListView extends ListView<String> implements IOnRemoveElementListener {
    ObservableList<String> m_aItems;

    private void _wrapListAroundElements() {
	final double aListSize = m_aItems.size();
	final double aListHeigth = aListSize > 8 ? Value.WINDOW_HEIGHT_DEFAULT - 83 : aListSize * 32 + 2;
	setPrefHeight(aListHeigth);
    }

    public RemoveableCellListView(final ObservableList<String> aItems) {
	super(aItems);

	m_aItems = aItems;
	setCellFactory(param -> new RemoveableListCell(this));

	_wrapListAroundElements();
    }

    @Override
    public void onRemoveElement(@Nonnegative final int nIndex) {
	m_aItems.remove(nIndex);
	setItems(m_aItems);

	_wrapListAroundElements();
    }
}
