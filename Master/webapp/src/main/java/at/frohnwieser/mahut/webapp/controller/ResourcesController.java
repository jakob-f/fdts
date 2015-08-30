package at.frohnwieser.mahut.webapp.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.webapp.util.SessionUtils;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.db.manager.AssetManager;
import at.frohnwieser.mahut.webappapi.db.manager.SetManager;
import at.frohnwieser.mahut.webappapi.db.model.AbstractResource;
import at.frohnwieser.mahut.webappapi.db.model.Set;
import at.frohnwieser.mahut.webappapi.db.model.User;

@SuppressWarnings("serial")
@ViewScoped
@ManagedBean(name = Value.CONTROLLER_RESOURCES)
public class ResourcesController extends AbstractDBObjectController<AbstractResource> {
    private Set m_aCurrentSet;

    @Nullable
    private void _getFromParamter() {
	if (m_aCurrentSet == null) {
	    final String sRequestParameter = SessionUtils.getInstance().getRequestParameter(Value.REQUEST_PARAMETER_SET);
	    if (StringUtils.isNotEmpty(sRequestParameter) && sRequestParameter.matches(Value.REGEX_RESOURCE_HASH))
		m_aCurrentSet = _managerInstance().getFromHash(SessionUtils.getInstance().getLoggedInUser(), sRequestParameter);
	}
    }

    @Nonnull
    public Set getCurrentSet() {
	_getFromParamter();
	if (m_aCurrentSet == null)
	    m_aCurrentSet = _managerInstance().get(at.frohnwieser.mahut.webappapi.util.Value.ROOT_SET_ID);
	return m_aCurrentSet;
    }

    @Nonnull
    public Collection<AbstractResource> getCurrentChildren() {
	getCurrentSet();

	final User aUser = SessionUtils.getInstance().getLoggedInUser();
	// first get child sets
	final List<AbstractResource> aChildren = m_aCurrentSet.getChildSetIds().stream().map(sChildSetId -> _managerInstance().getRead(aUser, sChildSetId))
	        .filter(o -> o != null).collect(Collectors.toCollection(ArrayList::new));
	Collections.sort(aChildren);
	// then get contained assets
	final AssetManager aAssetManager = AssetManager.getInstance();
	final List<AbstractResource> aAssets = m_aCurrentSet.getAssetIds().stream().map(sAssetId -> aAssetManager.getRead(aUser, sAssetId))
	        .filter(o -> o != null).collect(Collectors.toCollection(ArrayList::new));
	Collections.sort(aAssets);

	aChildren.addAll(aAssets);

	return aChildren;
    }

    @Override
    public void reload() {
	m_aEntries = getCurrentChildren();

	System.out.println(m_aEntries.size());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected SetManager _managerInstance() {
	return SetManager.getInstance();
    }
}
