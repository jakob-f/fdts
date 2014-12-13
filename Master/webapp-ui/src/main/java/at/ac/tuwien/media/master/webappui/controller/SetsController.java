package at.ac.tuwien.media.master.webappui.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webapp.util.SessionUtils;
import at.ac.tuwien.media.master.webapp.util.Value;
import at.ac.tuwien.media.master.webappapi.db.manager.impl.GroupManager;
import at.ac.tuwien.media.master.webappapi.db.manager.impl.SetManager;
import at.ac.tuwien.media.master.webappapi.db.model.Asset;
import at.ac.tuwien.media.master.webappapi.db.model.Set;
import at.ac.tuwien.media.master.webappapi.db.model.User;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.CONTROLLER_SETS)
public class SetsController extends AbstractDBObjectController<Set> {

    @SuppressWarnings("unchecked")
    @Override
    protected SetManager _managerInstance() {
	return SetManager.getInstance();
    }

    @Override
    @Nonnull
    protected Set _new() {
	return new Set();
    }

    @Override
    public void reload() {
	super.reload();
	SessionUtils.getInstance().getManagedBean(Value.CONTROLLER_GROUPS, GroupsController.class).reload();
    }

    public boolean isRead(@Nullable final Set aSet) {
	if (aSet != null) {
	    final User aUser = SessionUtils.getInstance().getLoggedInUser();

	    return GroupManager.getInstance().isRead(aUser, aSet);
	}

	return false;
    }

    @Nullable
    public Set getFromParamter() {
	final String sRequestParameter = SessionUtils.getInstance().getRequestParameter(Value.REQUEST_PARAMETER_SET);

	if (StringUtils.isNotEmpty(sRequestParameter) && sRequestParameter.matches(Value.REGEX_RESOURCE_HASH)) {
	    final User aUser = SessionUtils.getInstance().getLoggedInUser();

	    return _managerInstance().getRead(aUser, sRequestParameter);
	}

	return null;
    }

    @Nonnull
    public Collection<Set> getChilds(@Nullable final Set aSet) {
	if (aSet != null) {
	    final User aUser = SessionUtils.getInstance().getLoggedInUser();
	    final Collection<Long> aChildSetIds = _managerInstance().get(aSet.getId()).getChildSetIds();

	    // TODO use isRead?
	    return aChildSetIds.stream().map(nChildSetId -> _managerInstance().get(nChildSetId))
		    .filter(aChildSet -> GroupManager.getInstance().isRead(aUser, aChildSet)).collect(Collectors.toCollection(ArrayList::new));
	}

	return new ArrayList<Set>();
    }

    @Nullable
    public Set getParent(@Nullable final Asset aAsset) {
	return _managerInstance().getParent(aAsset);
    }

    @Nullable
    public Set getParent(@Nullable final Set aSet) {
	return _managerInstance().getParent(aSet);
    }

    @Nonnull
    public Collection<Set> getParents(@Nullable final Set aSet) {
	return _managerInstance().getParents(aSet);
    }
}
