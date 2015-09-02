package at.frohnwieser.mahut.webapp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.frohnwieser.mahut.webapp.util.SessionUtils;
import at.frohnwieser.mahut.webappapi.db.manager.AssetManager;
import at.frohnwieser.mahut.webappapi.db.manager.SetManager;
import at.frohnwieser.mahut.webappapi.db.model.AbstractResource;
import at.frohnwieser.mahut.webappapi.db.model.Asset;
import at.frohnwieser.mahut.webappapi.db.model.Set;
import at.frohnwieser.mahut.webappapi.db.model.User;

@SuppressWarnings("serial")
@ViewScoped
@Named
public class ResourcesController extends AbstractDBObjectController<AbstractResource> {
    @Inject
    private AssetsController m_aAssetsController;
    @Inject
    private SetsController m_aSetsController;

    @SuppressWarnings("unchecked")
    @Override
    protected SetManager _managerInstance() {
	return SetManager.getInstance();
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

    public boolean saveInCurrentSet() {
	final boolean bIsSuccess = m_aSetsController.saveInCurrentSet();
	reload();
	return bIsSuccess;
    }

    @Override
    @Nullable
    public boolean save(@Nullable final AbstractResource aEntry) {
	if (aEntry instanceof Asset)
	    return m_aAssetsController.save((Asset) aEntry);
	else if (aEntry instanceof Set)
	    return m_aSetsController.save((Set) aEntry);
	return false;
    }
}
