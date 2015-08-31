package at.frohnwieser.mahut.webapp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.webapp.util.SessionUtils;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.db.manager.AssetManager;
import at.frohnwieser.mahut.webappapi.db.manager.SetManager;
import at.frohnwieser.mahut.webappapi.db.model.AbstractResource;
import at.frohnwieser.mahut.webappapi.db.model.Set;
import at.frohnwieser.mahut.webappapi.db.model.User;

@SuppressWarnings("serial")
@RequestScoped
@Named
public class ResourcesController extends AbstractDBObjectController<AbstractResource> {
    @Inject
    private SetsController m_aSetsController;

    @Nullable
    public Set getFromParamter() {
	final String sRequestParameter = SessionUtils.getInstance().getRequestParameter(Value.REQUEST_PARAMETER_SET);
	if (StringUtils.isNotEmpty(sRequestParameter) && sRequestParameter.matches(Value.REGEX_RESOURCE_HASH))
	    return _managerInstance().getFromHash(SessionUtils.getInstance().getLoggedInUser(), sRequestParameter);
	return null;
    }

    @Override
    public void reload() {
	final User aUser = SessionUtils.getInstance().getLoggedInUser();
	final Set aParentSet = m_aSetsController.getCurrentSet();

	// first get child sets
	final List<AbstractResource> aChildren = aParentSet.getChildSetIds().stream().map(sChildSetId -> _managerInstance().getRead(aUser, sChildSetId))
	        .filter(o -> o != null).collect(Collectors.toCollection(ArrayList::new));
	Collections.sort(aChildren);
	// then get contained assets
	final AssetManager aAssetManager = AssetManager.getInstance();
	final List<AbstractResource> aAssets = aParentSet.getAssetIds().stream().map(sAssetId -> aAssetManager.getRead(aUser, sAssetId)).filter(o -> o != null)
	        .collect(Collectors.toCollection(ArrayList::new));
	Collections.sort(aAssets);
	aChildren.addAll(aAssets);

	m_aEntries = aChildren;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected SetManager _managerInstance() {
	return SetManager.getInstance();
    }
}
