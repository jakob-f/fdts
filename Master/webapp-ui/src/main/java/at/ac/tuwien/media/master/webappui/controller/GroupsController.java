package at.ac.tuwien.media.master.webappui.controller;

import javax.annotation.Nonnull;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import at.ac.tuwien.media.master.webapp.util.SessionUtils;
import at.ac.tuwien.media.master.webapp.util.Value;
import at.ac.tuwien.media.master.webappapi.db.manager.impl.GroupManager;
import at.ac.tuwien.media.master.webappapi.db.model.Group;

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
