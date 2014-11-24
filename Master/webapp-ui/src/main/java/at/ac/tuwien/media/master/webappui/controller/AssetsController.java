package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.manager.AssetManager;
import at.ac.tuwien.media.master.webappapi.model.Asset;
import at.ac.tuwien.media.master.webappui.util.SessionUtils;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@ViewScoped
@ManagedBean(name = Value.CONTROLLER_ASSETS)
public class AssetsController implements Serializable {
    private Collection<Asset> m_aAllAssets;
    private Asset m_aAsset;
    private String m_sAssetHash;

    public Collection<Asset> getAllAssets() {
	if (m_aAllAssets == null)
	    m_aAllAssets = AssetManager.getInstance().all();

	return m_aAllAssets;
    }

    public void deleteAsset(@Nullable final Asset aAsset) {
	if (aAsset != null)
	    m_aAllAssets = AssetManager.getInstance().delete(aAsset);
    }

    public void updateAsset(@Nullable final Asset aAsset) {
	if (aAsset != null) {
	    m_aAllAssets = AssetManager.getInstance().merge(aAsset);
	    SessionUtils.getInstance().getManagedBean(Value.CONTROLLER_WALLPAPER, WallpaperController.class).loadWPFiles();
	}
    }

    private void _loadAssetFromParamter() {
	final String sRequestParameter = SessionUtils.getInstance().getRequestParameter(Value.REQUEST_PARAMETER_ASSET);

	if (StringUtils.isNotEmpty(sRequestParameter) && sRequestParameter.matches(Value.REGEY_MD5_HEX)) {
	    m_sAssetHash = sRequestParameter;
	    m_aAsset = AssetManager.getInstance().getPublishedAsset(m_sAssetHash);
	}
    }

    public Asset getAsset() {
	if (m_aAsset == null)
	    _loadAssetFromParamter();

	return m_aAsset;
    }
}
