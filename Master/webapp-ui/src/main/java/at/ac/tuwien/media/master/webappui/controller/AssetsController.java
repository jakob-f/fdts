package at.ac.tuwien.media.master.webappui.controller;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.db.manager.impl.AssetManager;
import at.ac.tuwien.media.master.webappapi.db.model.Asset;
import at.ac.tuwien.media.master.webappui.util.SessionUtils;
import at.ac.tuwien.media.master.webappui.util.Value;

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
    @Nullable
    public Collection<Asset> save(@Nullable final Asset aEntry) {
	if (aEntry != null) {
	    SessionUtils.getInstance().getManagedBean(Value.CONTROLLER_WALLPAPER, WallpaperController.class)._loadWPs();
	    clear();

	    return _managerInstance().save(aEntry);
	}

	return null;
    }

    @Override
    @Nonnull
    protected Asset _new() {
	// TODO set parameters
	return new Asset("", "");
    }

    @Nullable
    public Asset getAssetFromParamter() {
	final String sRequestParameter = SessionUtils.getInstance().getRequestParameter(Value.REQUEST_PARAMETER_ASSET);

	if (StringUtils.isNotEmpty(sRequestParameter) && sRequestParameter.matches(Value.REGEX_ASSET_HASH))
	    return AssetManager.getInstance().getPublishedAsset(sRequestParameter);

	return null;
    }
}
