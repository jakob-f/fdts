package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.DataManager;
import at.ac.tuwien.media.master.webappapi.model.Asset;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@ViewScoped
@ManagedBean(name = Value.CONTROLLER_ASSETS)
public class AssetsController implements Serializable {
    private Collection<Asset> m_aAllAssets;
    private Asset m_aAsset;

    public Collection<Asset> getAllAssets() {
	if (m_aAllAssets == null)
	    m_aAllAssets = DataManager.getInstance().getAllAssets();

	return m_aAllAssets;
    }

    public void setDeleteAsset(@Nullable final Asset aAsset) {
	if (aAsset != null)
	    m_aAllAssets = DataManager.getInstance().deleteAsset(aAsset);
    }

    public void setUpdateAsset(@Nullable final Asset aAsset) {
	if (aAsset != null)
	    m_aAllAssets = DataManager.getInstance().updateAsset(aAsset);
    }

    private void _loadAssetFromParamter() {
	final String sHashParamter = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(Value.PARAMETER_ASSET);

	if (StringUtils.isNotEmpty(sHashParamter) && sHashParamter.matches(Value.REGEY_MD5_HEX))
	    m_aAsset = DataManager.getInstance().getPublishedAsset(sHashParamter);
    }

    public Asset getAsset() {
	if (m_aAsset == null)
	    _loadAssetFromParamter();

	return m_aAsset;
    }
}
