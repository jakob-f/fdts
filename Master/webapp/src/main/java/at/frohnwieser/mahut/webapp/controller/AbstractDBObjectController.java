package at.frohnwieser.mahut.webapp.controller;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.SerializationUtils;

import at.frohnwieser.mahut.commons.IHasId;
import at.frohnwieser.mahut.commons.IValidate;
import at.frohnwieser.mahut.webapp.util.SessionUtils;
import at.frohnwieser.mahut.webappapi.db.manager.AbstractManager;

@SuppressWarnings("serial")
public abstract class AbstractDBObjectController<E extends IHasId & IValidate> implements Serializable {
    protected Collection<E> m_aEntries;
    protected E m_aEntry;
    private boolean m_bIsMarkedForDeletion;
    private boolean m_bIsSelected;

    abstract protected <T extends AbstractManager<E>> T _managerInstance();

    protected void reload() {
	m_aEntries = _managerInstance().all();
    }

    public void clear() {
	m_aEntry = null;
	m_bIsMarkedForDeletion = false;
	m_bIsSelected = false;
    }

    public void delete() {
	if (m_bIsMarkedForDeletion && m_aEntry != null)
	    if (_managerInstance().delete(m_aEntry)) {
		clear();
		reload();

		SessionUtils.getInstance().info("successfully deleted", "");

		return;
	    }

	SessionUtils.getInstance().error("deletion failed", "");

	return;
    }

    @Nullable
    public boolean save(@Nullable final E aEntry) {
	if (_managerInstance().save(aEntry)) {
	    setSelectedEntry(aEntry);
	    reload();

	    if (!SessionUtils.getInstance().hasMessage())
		SessionUtils.getInstance().info("successfully saved", "");

	    return true;
	}

	SessionUtils.getInstance().error("save failed", "");

	return false;
    }

    public boolean save() {
	return save(m_aEntry);
    }

    @Nonnull
    public Collection<E> getAll() {
	if (m_aEntries == null)
	    reload();

	return m_aEntries;
    }

    @Nullable
    protected E _new() {
	return null;
    }

    @Nonnull
    public E getEntry() {
	if (m_aEntry == null)
	    m_aEntry = _new();

	return m_aEntry;
    }

    @Nullable
    public E getEntry(final String sId) {
	return _managerInstance().get(sId);
    }

    private boolean _equals(@Nullable final E aEntry) {
	return m_aEntry != null && (m_aEntry.getId().equals(aEntry.getId()));
    }

    @SuppressWarnings("unchecked")
    public void setSelectedEntry(@Nullable final E aEntry) {
	if (!m_bIsMarkedForDeletion || (m_bIsMarkedForDeletion && !_equals(aEntry))) {
	    // XXX SerializationUtils is slower than implementing clone()
	    m_aEntry = (E) SerializationUtils.clone((Serializable) aEntry);
	    m_bIsSelected = true;
	    m_bIsMarkedForDeletion = false;
	}
    }

    public void deselectEntry() {
	m_aEntry = null;
	m_bIsSelected = false;
	m_bIsMarkedForDeletion = false;
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
