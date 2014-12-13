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
import at.ac.tuwien.media.master.webappapi.db.model.Asset;
import at.ac.tuwien.media.master.webappapi.db.model.User;

@SuppressWarnings("serial")
@ViewScoped
@ManagedBean(name = Value.CONTROLLER_ASSETS)
public class AssetsController extends AbstractDBObjectController<Asset> {
    LinkedList<Asset> m_aAssets;

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
	if (m_aAssets != null)
	    return m_aAssets;
	final User aUser = SessionUtils.getInstance().getLoggedInUser();
	m_aAssets = new LinkedList<Asset>();

	// FIXME better
	_managerInstance().allReadForParent(aUser, aAsset).forEach(aOtherAsset -> {
	    if (aOtherAsset.getId() != aAsset.getId())
		m_aAssets.addFirst(aOtherAsset);
	});

	return m_aAssets;
    }

    @Nullable
    public Asset getFromParamter() {
	final String sRequestParameter = SessionUtils.getInstance().getRequestParameter(Value.REQUEST_PARAMETER_ASSET);

	if (StringUtils.isNotEmpty(sRequestParameter) && sRequestParameter.matches(Value.REGEX_RESOURCE_HASH)) {
	    final User aUser = SessionUtils.getInstance().getLoggedInUser();

	    return _managerInstance().getRead(aUser, sRequestParameter);
	}

	return null;
    }
}
