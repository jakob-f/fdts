package at.frohnwieser.mahut.webapp.controller;

import javax.annotation.Nonnull;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.frohnwieser.mahut.webappapi.db.manager.GroupManager;
import at.frohnwieser.mahut.webappapi.db.model.Group;

@SuppressWarnings("serial")
@ViewScoped
@Named
public class GroupsController extends AbstractDBObjectController<Group> {
    @Inject
    private UsersController m_aUsersController;

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
	m_aUsersController.reload();
    }
}
