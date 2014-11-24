package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.manager.SetManager;
import at.ac.tuwien.media.master.webappapi.model.Set;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.CONTROLLER_SETS)
public class SetsController implements Serializable {
    private Collection<Set> m_aSets;
    private Set m_aNewSet;
    private Set m_aSelectedSet;

    public Collection<Set> getAll() {
	if (m_aSets == null)
	    m_aSets = SetManager.getInstance().all();

	return m_aSets;
    }

    public Set getSelectedOrNew() {
	if (m_aSelectedSet != null)
	    return m_aSelectedSet;

	if (m_aNewSet == null)
	    m_aNewSet = new Set();

	return m_aNewSet;
    }

    public void clear() {
	m_aSelectedSet = null;
	m_aNewSet = null;
    }

    public void save() {
	final Set aSet = getSelectedOrNew();

	if (StringUtils.isNoneEmpty(aSet.getName()) && StringUtils.isNoneEmpty(aSet.getDescription())) {
	    if (m_aSelectedSet != null)
		m_aSets = SetManager.getInstance().merge(aSet);
	    else
		m_aSets = SetManager.getInstance().save(aSet);

	    clear();
	}
    }

    @Nullable
    public Set getSelected() {
	return m_aSelectedSet;
    }

    public void setSelected(@Nullable final Set aSet) {
	m_aSelectedSet = aSet;
    }

    public void delete(@Nullable final Set aSet) {
	m_aSets = SetManager.getInstance().delete(aSet);
    }
}