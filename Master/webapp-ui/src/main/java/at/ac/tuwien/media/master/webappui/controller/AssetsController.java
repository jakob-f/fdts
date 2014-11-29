package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;
import java.util.Collection;

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
public class AssetsController implements Serializable {
    private Collection<Asset> m_aAssets;
    private Asset m_aAsset;
    private String m_sAssetHash;

    public Collection<Asset> getAll() {
	if (m_aAssets == null)
	    m_aAssets = AssetManager.getInstance().all();

	return m_aAssets;
    }

    public void delete(@Nullable final Asset aAsset) {
	if (aAsset != null)
	    m_aAssets = AssetManager.getInstance().delete(aAsset);
    }

    public void update(@Nullable final Asset aAsset) {
	if (aAsset != null)
	    m_aAssets = AssetManager.getInstance().save(aAsset);
    }

    private void _loadAssetFromParamter() {
	final String sRequestParameter = SessionUtils.getInstance().getRequestParameter(Value.REQUEST_PARAMETER_ASSET);

	if (StringUtils.isNotEmpty(sRequestParameter) && sRequestParameter.matches(Value.REGEX_ASSET_HASH)) {
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
