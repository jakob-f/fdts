package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.ac.tuwien.media.master.commons.IHasId;
import at.ac.tuwien.media.master.webappapi.db.manager.IManager;

@SuppressWarnings("serial")
public abstract class AbstractDBObjectController<E extends IHasId> implements Serializable {
    private E m_aEntry;
    private boolean m_bIsMarkedForDeletion;
    private boolean m_bIsSelected;

    abstract protected <T extends IManager<E>> T _managerInstance();

    public void clear() {
	m_aEntry = null;
	m_bIsMarkedForDeletion = false;
	m_bIsSelected = false;
    }

    public void delete() {
	if (m_bIsMarkedForDeletion && m_aEntry != null) {
	    _managerInstance().delete(m_aEntry);

	    clear();
	}
    }

    @Nullable
    abstract public boolean save(@Nullable final E aEntry);

    public void save() {
	save(m_aEntry);
    }

    @Nonnull
    public Collection<E> getAll() {
	return _managerInstance().all();
    }

    @Nonnull
    abstract protected E _new();

    @Nonnull
    public E getEntry() {
	if (m_aEntry == null)
	    m_aEntry = _new();

	return m_aEntry;
    }

    private boolean _equals(@Nullable final E aEntry) {
	return m_aEntry != null && (m_aEntry.getId() == aEntry.getId());
    }

    public void setSelectedEntry(@Nullable final E aEntry) {
	if (!m_bIsMarkedForDeletion || (m_bIsMarkedForDeletion && !_equals(aEntry))) {
	    m_aEntry = aEntry;
	    m_bIsSelected = true;
	    m_bIsMarkedForDeletion = false;
	}
    }

    @Nonnull
    public E getSelectedEntry() {
	return getEntry();
    }

    public void setMarkedForDeletionEntry(@Nullable final E aEntry) {
	if (m_aEntry == null || m_bIsSelected || (!m_bIsSelected && !_equals(aEntry)))
	    m_aEntry = aEntry;
	else
	    m_aEntry = null;

	m_bIsMarkedForDeletion = m_aEntry != null;
	m_bIsSelected = false;
    }

    public boolean isEntryMarkedForDeletion() {
	return m_bIsMarkedForDeletion;
    }

    public boolean isEntrySelected() {
	return m_bIsSelected;
    }
}
