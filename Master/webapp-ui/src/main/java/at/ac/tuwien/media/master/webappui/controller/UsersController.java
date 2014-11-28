package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.db.manager.UserManager;
import at.ac.tuwien.media.master.webappapi.db.model.ERole;
import at.ac.tuwien.media.master.webappapi.db.model.User;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.CONTROLLER_USERS)
public class UsersController implements Serializable {
    private final static SelectItem[] USER_ROLES = new SelectItem[] { new SelectItem(ERole.ADMIN, ERole.ADMIN.getName()),
	    new SelectItem(ERole.USER, ERole.USER.getName()) };

    private Collection<User> m_aUsers;
    private User m_aSelectedUser;
    private boolean m_bisSelectedUser;

    public void clear() {
	m_aSelectedUser = null;
	m_bisSelectedUser = false;
    }

    public void delete(@Nullable final User aUser) {
	m_aUsers = UserManager.getInstance().delete(aUser);
    }

    public Collection<User> getAll() {
	if (m_aUsers == null)
	    m_aUsers = UserManager.getInstance().all();

	return m_aUsers;
    }

    public SelectItem[] getRoles() {
	return USER_ROLES;
    }

    @Nonnull
    public User getUser() {
	if (m_aSelectedUser == null)
	    m_aSelectedUser = new User();

	return m_aSelectedUser;
    }

    public void setUser(@Nullable final User aUser) {
	m_aSelectedUser = aUser;
	m_bisSelectedUser = true;
    }

    public void save(@Nullable final User aUser) {
	if (aUser != null && StringUtils.isNoneEmpty(aUser.getName()) && StringUtils.isNoneEmpty(aUser.getPassword())
	        && StringUtils.isNoneEmpty(aUser.getEmail()) && aUser.getRole() != null)
	    m_aUsers = UserManager.getInstance().save(aUser);
    }

    public void save() {
	save(m_aSelectedUser);

	clear();
    }

    public boolean isSelectedUser() {
	return m_bisSelectedUser;
    }
}
