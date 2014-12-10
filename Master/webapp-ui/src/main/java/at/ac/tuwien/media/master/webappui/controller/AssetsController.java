package at.ac.tuwien.media.master.webappui.controller;

import java.util.Collection;
import java.util.LinkedList;

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
	final User aUser = SessionUtils.getInstance().getLoggedInUser();
	final LinkedList<Asset> aFilteredAssets = new LinkedList<Asset>();

	// FIXME better
	AssetManager.getInstance().allReadForParent(aUser, aAsset).forEach(aOtherAsset -> {
	    if (aOtherAsset.getId() != aAsset.getId())
		aFilteredAssets.addFirst(aOtherAsset);
	});

	return aFilteredAssets;
    }

    @Nullable
    public Asset getFromParamter() {
	final String sRequestParameter = SessionUtils.getInstance().getRequestParameter(Value.REQUEST_PARAMETER_ASSET);

	if (StringUtils.isNotEmpty(sRequestParameter) && sRequestParameter.matches(Value.REGEX_ASSET_HASH)) {
	    final User aUser = SessionUtils.getInstance().getLoggedInUser();

	    return AssetManager.getInstance().getRead(aUser, sRequestParameter);
	}

	return null;
    }

    // TODO not used
    @Nullable
    public Set getParent() {
	return SetManager.getInstance().getParent(getEntry());
    }
}
