package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.db.manager.GroupManager;
import at.ac.tuwien.media.master.webappapi.db.model.Group;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.CONTROLLER_GROUPS)
public class GroupsController implements Serializable {
    private Collection<Group> m_aGroups;
    private Group m_aNewGroup;
    private Group m_aSelectedGroup;

    public Collection<Group> getAll() {
	if (m_aGroups == null)
	    m_aGroups = GroupManager.getInstance().all();

	return m_aGroups;
    }

    public Group getSelectedOrNew() {
	if (m_aSelectedGroup != null)
	    return m_aSelectedGroup;

	if (m_aNewGroup == null)
	    m_aNewGroup = new Group();

	return m_aNewGroup;
    }

    public void clear() {
	m_aNewGroup = null;
	m_aSelectedGroup = null;
    }

    public void update(@Nullable final Group aGroup) {
	if (aGroup != null)
	    m_aGroups = GroupManager.getInstance().merge(aGroup);
    }

    public void save() {
	final Group aGroup = getSelectedOrNew();

	if (StringUtils.isNoneEmpty(aGroup.getName()) && StringUtils.isNoneEmpty(aGroup.getDescription())) {
	    if (m_aSelectedGroup != null) {
		update(aGroup);
		m_aSelectedGroup = null;
	    } else {
		m_aGroups = GroupManager.getInstance().save(aGroup);

		m_aNewGroup = null;
		m_aSelectedGroup = aGroup;
	    }
	}
    }

    @Nullable
    public Group getSelected() {
	return m_aSelectedGroup;
    }

    public void setSelected(@Nullable final Group aGroup) {
	m_aSelectedGroup = aGroup;
    }

    public void delete(@Nullable final Group aGroup) {
	m_aGroups = GroupManager.getInstance().delete(aGroup);
    }
}
