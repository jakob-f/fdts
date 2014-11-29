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
    private User m_aUser;
    private boolean m_bIsMarkedForDeletionUser;
    private boolean m_bIsSelectedUser;

    public void clear() {
	m_aUser = null;
	m_bIsMarkedForDeletionUser = false;
	m_bIsSelectedUser = false;
    }

    public void delete() {
	if (m_bIsMarkedForDeletionUser && m_aUser != null) {
	    m_aUsers = UserManager.getInstance().delete(m_aUser);

	    clear();
	}
    }

    public void save(@Nullable final User aUser) {
	if (aUser != null && StringUtils.isNoneEmpty(aUser.getName()) && StringUtils.isNoneEmpty(aUser.getPassword())
	        && StringUtils.isNoneEmpty(aUser.getEmail()) && aUser.getRole() != null)
	    m_aUsers = UserManager.getInstance().save(aUser);
    }

    public void save() {
	save(m_aUser);

	clear();
    }

    public SelectItem[] getRoles() {
	return USER_ROLES;
    }

    public Collection<User> getAll() {
	if (m_aUsers == null)
	    m_aUsers = UserManager.getInstance().all();

	return m_aUsers;
    }

    @Nonnull
    public User getUser() {
	if (m_aUser == null)
	    m_aUser = new User();

	return m_aUser;
    }

    public User getUserSelected() {
	return getUser();
    }

    private boolean _userEquals(@Nullable final User aUser) {
	return m_aUser != null && (m_aUser.getId() == aUser.getId());
    }

    public void setUserSelected(@Nullable final User aUser) {
	if (!m_bIsMarkedForDeletionUser || (m_bIsMarkedForDeletionUser && !_userEquals(aUser))) {
	    m_aUser = aUser;
	    m_bIsSelectedUser = true;
	    m_bIsMarkedForDeletionUser = false;
	}
    }

    public void setUserMarkedForDeletion(@Nullable final User aUser) {
	if (m_aUser == null || m_bIsSelectedUser || (!m_bIsSelectedUser && !_userEquals(aUser)))
	    m_aUser = aUser;
	else
	    m_aUser = null;

	m_bIsMarkedForDeletionUser = m_aUser != null;
	m_bIsSelectedUser = false;
    }

    public boolean isMarkedForDeletionUser() {
	return m_bIsMarkedForDeletionUser;
    }

    public boolean isSelectedUser() {
	return m_bIsSelectedUser;
    }
}
