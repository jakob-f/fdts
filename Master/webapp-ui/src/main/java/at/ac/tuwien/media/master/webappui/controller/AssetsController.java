package at.ac.tuwien.media.master.webappui.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webapp.util.SessionUtils;
import at.ac.tuwien.media.master.webapp.util.Value;
import at.ac.tuwien.media.master.webappapi.db.manager.impl.AssetManager;
import at.ac.tuwien.media.master.webappapi.db.manager.impl.SetManager;
import at.ac.tuwien.media.master.webappapi.db.model.Asset;
import at.ac.tuwien.media.master.webappapi.db.model.Set;
import at.ac.tuwien.media.master.webappapi.db.model.User;

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

	System.out.println(aAssets.size());

	return getAssets(SetManager.getInstance().getParent(aAsset));
    }

    @Nonnull
    public Collection<Asset> getAssets(@Nullable final Set aSet) {
	if (aSet != null) {
	    final User aUser = SessionUtils.getInstance().getLoggedInUser();

	    System.out.println(aSet.getAssetIds().stream().map(nAssetId -> _managerInstance().getRead(aUser, nAssetId)).filter(o -> o != null)
		    .collect(Collectors.toCollection(ArrayList::new)).size());

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
