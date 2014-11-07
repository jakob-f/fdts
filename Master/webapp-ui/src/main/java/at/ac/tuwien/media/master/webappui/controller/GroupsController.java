package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;
import java.util.Collection;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import at.ac.tuwien.media.master.webappapi.manager.GroupManager;
import at.ac.tuwien.media.master.webappapi.model.Group;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.CONTROLLER_GROUPS)
public class GroupsController implements Serializable {
    private Collection<Group> m_aAllGroups;

    public Collection<Group> getAll() {
	if (m_aAllGroups == null)
	    m_aAllGroups = GroupManager.getInstance().all();

	return m_aAllGroups;
    }
}
