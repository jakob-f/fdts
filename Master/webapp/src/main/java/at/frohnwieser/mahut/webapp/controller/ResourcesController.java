package at.frohnwieser.mahut.webapp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.frohnwieser.mahut.webapp.bean.Credentials;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.db.manager.AssetManager;
import at.frohnwieser.mahut.webappapi.db.manager.SetManager;
import at.frohnwieser.mahut.webappapi.db.model.AbstractResource;
import at.frohnwieser.mahut.webappapi.db.model.Set;
import at.frohnwieser.mahut.webappapi.db.model.User;

@SuppressWarnings("serial")
@SessionScoped
@Named(Value.CONTROLLER_RESOURCES)
public class ResourcesController extends AbstractDBObjectController<AbstractResource> {
    @Inject
    private Credentials m_aCredentials;
    @Inject
    private SetsController m_aSetsController;

    @Override
    public void reload() {
	final User aUser = m_aCredentials.getUser();
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
