package at.frohnwieser.mahut.webapp.controller;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;

import at.frohnwieser.mahut.webapp.util.SessionUtils;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.db.manager.UserManager;
import at.frohnwieser.mahut.webappapi.db.model.ERole;
import at.frohnwieser.mahut.webappapi.db.model.User;

@SuppressWarnings("serial")
@RequestScoped
@Named
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

    @Nullable
    public User getFromParamter() {
	if (m_aEntry == null)
	    m_aEntry = _managerInstance().get(SessionUtils.getInstance().getRequestParameter(Value.REQUEST_PARAMETER_USER));

	return m_aEntry;
    }

    @Nonnull
    public SelectItem[] getRoles() {
	return USER_ROLES;
    }
}