package at.frohnwieser.mahut.webappui.controller;

import javax.annotation.Nonnull;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import at.frohnwieser.mahut.webapp.util.SessionUtils;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.db.manager.impl.GroupManager;
import at.frohnwieser.mahut.webappapi.db.model.Group;

@SuppressWarnings("serial")
@ViewScoped
@ManagedBean(name = Value.CONTROLLER_GROUPS)
public class GroupsController extends AbstractDBObjectController<Group> {

    @SuppressWarnings("unchecked")
    @Override
    protected GroupManager _managerInstance() {
	return GroupManager.getInstance();
    }

    @Override
    @Nonnull
    protected Group _new() {
	return new Group();
    }

    @Override
    public void reload() {
	super.reload();
	SessionUtils.getInstance().getManagedBean(Value.CONTROLLER_USERS, UsersController.class).reload();
    }
}
