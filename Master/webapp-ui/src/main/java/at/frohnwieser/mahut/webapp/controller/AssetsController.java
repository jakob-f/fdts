package at.frohnwieser.mahut.webapp.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.webapp.util.SessionUtils;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.db.manager.impl.AssetManager;
import at.frohnwieser.mahut.webappapi.db.manager.impl.SetManager;
import at.frohnwieser.mahut.webappapi.db.model.Asset;
import at.frohnwieser.mahut.webappapi.db.model.Set;
import at.frohnwieser.mahut.webappapi.db.model.User;

@SuppressWarnings("serial")
@ViewScoped
@ManagedBean(name = Value.CONTROLLER_ASSETS)
public class AssetsController extends AbstractDBObjectController<Asset> {
    @SuppressWarnings("unchecked")
    @Override
    protected AssetManager _managerInstance() {
	return AssetManager.getInstance();
    }

    @Override
    @Nonnull
    protected Asset _new() {
	// TODO not used
	return new Asset("", "");
    }

    @Nonnull
    public Collection<Asset> allOtherFromSet(@Nullable final Asset aAsset) {
	final Collection<Asset> aAssets = getAssets(SetManager.getInstance().getParent(aAsset));

	return getAssets(SetManager.getInstance().getParent(aAsset));
    }

    @Nonnull
    public Collection<Asset> getAssets(@Nullable final Set aSet) {
	if (aSet != null) {
	    final User aUser = SessionUtils.getInstance().getLoggedInUser();

	    return aSet.getAssetIds().stream().map(nAssetId -> _managerInstance().getRead(aUser, nAssetId)).filter(o -> o != null)
		    .collect(Collectors.toCollection(ArrayList::new));
	}

	return new ArrayList<Asset>();
    }

    @Nullable
    public Asset getFromParamter() {
	if (m_aEntry == null) {
	    final String sRequestParameter = SessionUtils.getInstance().getRequestParameter(Value.REQUEST_PARAMETER_ASSET);

	    if (StringUtils.isNotEmpty(sRequestParameter) && sRequestParameter.matches(Value.REGEX_RESOURCE_HASH)) {
		final User aUser = SessionUtils.getInstance().getLoggedInUser();

		m_aEntry = _managerInstance().getRead(aUser, sRequestParameter);
	    }
	}

	return m_aEntry;
    }
}
