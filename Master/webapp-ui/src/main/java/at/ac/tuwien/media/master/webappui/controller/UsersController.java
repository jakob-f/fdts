package at.ac.tuwien.media.master.webappui.controller;

import javax.annotation.Nonnull;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import at.ac.tuwien.media.master.webapp.util.Value;
import at.ac.tuwien.media.master.webappapi.db.manager.impl.UserManager;
import at.ac.tuwien.media.master.webappapi.db.model.ERole;
import at.ac.tuwien.media.master.webappapi.db.model.User;

@SuppressWarnings("serial")
@ViewScoped
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
    @Nonnull
    protected User _new() {
	return new User();
    }

    @Nonnull
    public SelectItem[] getRoles() {
	return USER_ROLES;
    }
}