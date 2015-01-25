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
import at.frohnwieser.mahut.webappapi.db.manager.AssetManager;
import at.frohnwieser.mahut.webappapi.db.model.Asset;
import at.frohnwieser.mahut.webappapi.db.model.EState;
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
    public boolean save(final Asset aEntry) {
	final boolean bRet = super.save(aEntry);
	SessionUtils.getInstance().getManagedBean(Value.CONTROLLER_WALLPAPER, WallpaperController.class)._loadWPs();

	return bRet;
    }

    @Nonnull
    public Collection<Asset> getFrom(@Nullable final Set aSet) {
	if (aSet != null) {
	    final User aUser = SessionUtils.getInstance().getLoggedInUser();

	    if (aSet.getState() == EState.PUBLIC)
		return aSet.getAssetIds().stream().map(nAssetId -> _managerInstance().getRead(aUser, nAssetId)).filter(o -> o != null)
		        .collect(Collectors.toCollection(ArrayList::new));
	    else if (aSet.getState().is(EState.PUBLISHED))
		return aSet.getAssetIds().stream().map(nAssetId -> _managerInstance().getPublished(aUser, nAssetId)).filter(o -> o != null)
		        .collect(Collectors.toCollection(ArrayList::new));
	}

	return new ArrayList<Asset>();
    }

    @Nonnull
    public Collection<Asset> getOthersFrom(@Nullable final Set aSet, @Nullable final Asset aAsset) {
	if (aSet != null) {
	    final User aUser = SessionUtils.getInstance().getLoggedInUser();

	    return aSet.getAssetIds().stream().filter(nAssetId -> nAssetId != aAsset.getId()).map(nAssetId -> _managerInstance().getRead(aUser, nAssetId))
		    .filter(o -> o != null).collect(Collectors.toCollection(ArrayList::new));
	}

	return new ArrayList<Asset>();
    }

    @Nullable
    public Asset getFromParamter() {
	if (m_aEntry == null) {
	    final String sRequestParameter = SessionUtils.getInstance().getRequestParameter(Value.REQUEST_PARAMETER_ASSET);

	    if (StringUtils.isNotEmpty(sRequestParameter) && sRequestParameter.matches(Value.REGEX_RESOURCE_HASH))
		m_aEntry = _managerInstance().getFromHash(SessionUtils.getInstance().getLoggedInUser(), sRequestParameter);
	}

	return m_aEntry;
    }
}
