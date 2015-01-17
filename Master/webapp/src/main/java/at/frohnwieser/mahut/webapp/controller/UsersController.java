package at.frohnwieser.mahut.webapp.controller;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.webapp.util.SessionUtils;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.db.manager.impl.UserManager;
import at.frohnwieser.mahut.webappapi.db.model.ERole;
import at.frohnwieser.mahut.webappapi.db.model.User;

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

    @Nullable
    public User getFromParamter() {
	if (m_aEntry == null) {
	    final String sRequestParameter = SessionUtils.getInstance().getRequestParameter(Value.REQUEST_PARAMETER_USER);

	    if (StringUtils.isNotEmpty(sRequestParameter))
		if ((m_aEntry = _managerInstance().get(sRequestParameter)) == null)
		    try {
			m_aEntry = _managerInstance().get(Long.parseLong(sRequestParameter));
		    } catch (final NumberFormatException aNFException) {
			aNFException.printStackTrace();
		    }
	}

	return m_aEntry;
    }

    @Nonnull
    public SelectItem[] getRoles() {
	return USER_ROLES;
    }
}