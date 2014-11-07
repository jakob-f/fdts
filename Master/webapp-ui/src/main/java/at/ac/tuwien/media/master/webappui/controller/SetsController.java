package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.manager.SetManager;
import at.ac.tuwien.media.master.webappapi.model.Group;
import at.ac.tuwien.media.master.webappapi.model.Set;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@ViewScoped
@ManagedBean(name = Value.CONTROLLER_SETS)
public class SetsController implements Serializable {
    private Collection<Set> m_aAllSets;
    private Set m_aNewSet;
    private Set m_aSelectedSet;

    public Collection<Set> getAll() {
	if (m_aAllSets == null)
	    m_aAllSets = SetManager.getInstance().all();

	return m_aAllSets;
    }

    private Set _getSelectedOrNew() {
	if (m_aSelectedSet != null)
	    return m_aSelectedSet;

	if (m_aNewSet == null)
	    m_aNewSet = new Set();

	return m_aNewSet;
    }

    public void save() {
	final Set aSet = _getSelectedOrNew();

	// FIXME m_aSelectedSet always null
	if (StringUtils.isNoneEmpty(aSet.getName()) && StringUtils.isNoneEmpty(aSet.getDescription())) {
	    if (m_aSelectedSet != null)
		m_aAllSets = SetManager.getInstance().merge(aSet);
	    else
		m_aAllSets = SetManager.getInstance().save(aSet);

	    m_aNewSet = null;
	    m_aSelectedSet = null;
	}
    }

    @Nullable
    public Set getSelected() {
	return m_aSelectedSet;
    }

    public void setSelected(@Nullable final Set aSet) {
	m_aSelectedSet = aSet;
    }

    public void setDelete(@Nullable final Set aSet) {
	m_aAllSets = SetManager.getInstance().delete(aSet);
    }

    @Nullable
    public String getName() {
	return _getSelectedOrNew().getName();
    }

    public void setName(@Nullable final String sSetName) {
	_getSelectedOrNew().setName(sSetName);
    }

    @Nullable
    public String getDescription() {
	return _getSelectedOrNew().getDescription();
    }

    public void setDescription(@Nullable final String sSetDescription) {
	_getSelectedOrNew().setDescription(sSetDescription);
    }

    @Nullable
    public Collection<Group> getReadGroups() {
	return _getSelectedOrNew().getReadGroups();
    }

    public void setReadGroups(@Nonnull final Collection<Group> aReadGroups) {
	_getSelectedOrNew().setReadGroups(aReadGroups);
    }

    @Nullable
    public Collection<Group> getReadWriteGroups() {
	return _getSelectedOrNew().getReadWriteGroups();
    }

    public void setReadWriteGroups(@Nonnull final Collection<Group> aReadWriteGroups) {
	_getSelectedOrNew().setReadWriteGroups(aReadWriteGroups);
    }
}
