package at.ac.tuwien.media.master.webappui.controller;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.db.manager.impl.UserManager;
import at.ac.tuwien.media.master.webappapi.db.model.ERole;
import at.ac.tuwien.media.master.webappapi.db.model.User;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.CONTROLLER_USERS)
public class UsersController extends AbstractDBObjectController<User> {
    private final static SelectItem[] USER_ROLES = new SelectItem[] { new SelectItem(ERole.ADMIN, ERole.ADMIN.getName()),
	    new SelectItem(ERole.USER, ERole.USER.getName()) };

    @SuppressWarnings("unchecked")
    @Override
    protected UserManager _managerInstance() {
	return UserManager.getInstance();
    }

    @Override
    @Nullable
    public Collection<User> save(@Nullable final User aEntry) {
	if (aEntry != null && StringUtils.isNoneEmpty(aEntry.getName()) && StringUtils.isNoneEmpty(aEntry.getPassword())
	        && StringUtils.isNoneEmpty(aEntry.getEmail()) && aEntry.getRole() != null) {
	    clear();

	    return _managerInstance().save(aEntry);
	}

	return null;
    }

    @Override
    @Nonnull
    protected User _new() {
	return new User();
    }

    public SelectItem[] getRoles() {
	return USER_ROLES;
    }
}