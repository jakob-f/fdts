package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.DataManager;
import at.ac.tuwien.media.master.webappapi.model.ERole;
import at.ac.tuwien.media.master.webappapi.model.IdFactory;
import at.ac.tuwien.media.master.webappapi.model.User;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.CONTROLLER_USERS)
public class UsersController implements Serializable {
    private Collection<User> m_aUsers;
    private String m_sUserName;
    private String m_sUserPassword;

    public Collection<User> getUsersList() {
	if (m_aUsers == null)
	    m_aUsers = DataManager.getInstance().getAllUser();

	return m_aUsers;
    }

    public void saveUser() {
	if (StringUtils.isNotEmpty(m_sUserName) && StringUtils.isNotEmpty(m_sUserPassword)) {
	    // TODO
	    final Collection<ERole> aUserRoles = new HashSet<ERole>();
	    aUserRoles.add(ERole.USER);
	    DataManager.getInstance().saveUser(
		    new User(IdFactory.getInstance().getNextId(), m_sUserName, m_sUserPassword, "contact@example.org", aUserRoles, null));

	    m_sUserName = "";
	    m_sUserPassword = "";
	}
    }

    @Nullable
    public String getUserName() {
	return m_sUserName;
    }

    public void setUserName(@Nullable final String sUserName) {
	this.m_sUserName = sUserName;
    }

    @Nullable
    public String getUserPassword() {
	return m_sUserPassword;
    }

    public void setUserPassword(@Nullable final String sUserPassword) {
	this.m_sUserPassword = sUserPassword;
    }
}
