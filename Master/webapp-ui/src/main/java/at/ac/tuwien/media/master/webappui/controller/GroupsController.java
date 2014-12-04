package at.ac.tuwien.media.master.webappui.controller;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webapp.util.Value;
import at.ac.tuwien.media.master.webappapi.db.manager.impl.GroupManager;
import at.ac.tuwien.media.master.webappapi.db.model.Group;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.CONTROLLER_GROUPS)
public class GroupsController extends AbstractDBObjectController<Group> {

    @SuppressWarnings("unchecked")
    @Override
    protected GroupManager _managerInstance() {
	return GroupManager.getInstance();
    }

    @Override
    protected boolean _validateEntry(@Nullable final Group aEntry) {
	return aEntry != null && StringUtils.isNoneEmpty(aEntry.getName()) && StringUtils.isNoneEmpty(aEntry.getDescription());
    }

    @Override
    @Nonnull
    protected Group _new() {
	return new Group();
    }
}
