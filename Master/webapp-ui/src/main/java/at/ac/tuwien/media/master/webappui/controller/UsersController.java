package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.manager.UserManager;
import at.ac.tuwien.media.master.webappapi.model.ERole;
import at.ac.tuwien.media.master.webappapi.model.User;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.CONTROLLER_USERS)
public class UsersController implements Serializable {
    private final static SelectItem[] USER_ROLES = new SelectItem[] { new SelectItem(ERole.ADMIN, ERole.ADMIN.getName()),
	    new SelectItem(ERole.USER, ERole.USER.getName()) };
    private Collection<User> m_aUsers;
    private User m_aNewUser;
    private User m_aSelectedUser;

    public Collection<User> getAll() {
	if (m_aUsers == null)
	    m_aUsers = UserManager.getInstance().all();

	return m_aUsers;
    }

    public User getSelectedOrNew() {
	if (m_aSelectedUser != null)
	    return m_aSelectedUser;

	if (m_aNewUser == null)
	    m_aNewUser = new User();

	return m_aNewUser;
    }

    public void clear() {
	m_aSelectedUser = null;
	m_aNewUser = null;
    }

    public void update(@Nullable final User aUser) {
	if (aUser != null)
	    m_aUsers = UserManager.getInstance().merge(aUser);
    }

    public void save() {
	final User aUser = getSelectedOrNew();

	if (StringUtils.isNoneEmpty(aUser.getName()) && StringUtils.isNoneEmpty(aUser.getPassword()) && StringUtils.isNoneEmpty(aUser.getEmail())
	        && aUser.getRole() != null) {
	    if (m_aSelectedUser != null)
		update(aUser);
	    else
		m_aUsers = UserManager.getInstance().save(aUser);

	    clear();
	}
    }

    @Nullable
    public User getSelected() {
	return m_aSelectedUser;
    }

    public void setSelected(@Nullable final User aUser) {
	m_aSelectedUser = aUser;
    }

    public void delete(@Nullable final User aUser) {
	m_aUsers = UserManager.getInstance().delete(aUser);
    }

    public SelectItem[] getRoles() {
	return USER_ROLES;
    }
}
