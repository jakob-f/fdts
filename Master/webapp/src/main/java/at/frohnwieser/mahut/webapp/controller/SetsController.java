package at.frohnwieser.mahut.webapp.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.webapp.bean.Credentials;
import at.frohnwieser.mahut.webapp.util.SessionUtils;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.db.manager.impl.AssetManager;
import at.frohnwieser.mahut.webappapi.db.manager.impl.SetManager;
import at.frohnwieser.mahut.webappapi.db.model.Asset;
import at.frohnwieser.mahut.webappapi.db.model.Set;
import at.frohnwieser.mahut.webappapi.db.model.User;

@SuppressWarnings("serial")
@ViewScoped
@ManagedBean(name = Value.CONTROLLER_SETS)
public class SetsController extends AbstractDBObjectController<Set> {

    @SuppressWarnings("unchecked")
    @Override
    protected SetManager _managerInstance() {
	return SetManager.getInstance();
    }

    @Override
    @Nonnull
    protected Set _new() {
	return new Set(SessionUtils.getInstance().getManagedBean(Value.BEAN_CREDENTIALS, Credentials.class).getUser().getId());
    }

    @Override
    public void reload() {
	super.reload();
	SessionUtils.getInstance().getManagedBean(Value.CONTROLLER_GROUPS, GroupsController.class).reload();
    }

    public boolean isRead(@Nullable final Set aSet) {
	if (aSet != null)
	    return _managerInstance().isRead(SessionUtils.getInstance().getLoggedInUser(), aSet);

	return false;
    }

    @Nullable
    public Set getFromParamter() {
	if (m_aEntry == null) {
	    final String sRequestParameter = SessionUtils.getInstance().getRequestParameter(Value.REQUEST_PARAMETER_SET);

	    if (StringUtils.isNotEmpty(sRequestParameter) && sRequestParameter.matches(Value.REGEX_RESOURCE_HASH)) {
		final User aUser = SessionUtils.getInstance().getLoggedInUser();

		m_aEntry = _managerInstance().getRead(aUser, sRequestParameter);
	    }
	}

	return m_aEntry;
    }

    @Nonnull
    public String getBgStyle(@Nullable final Set aSet) {
	if (aSet != null) {
	    final User aUser = SessionUtils.getInstance().getLoggedInUser();
	    final List<Asset> aAssets = aSet.getAssetIds().stream().map(nAssetId -> AssetManager.getInstance().getRead(aUser, nAssetId)).filter(o -> o != null)
		    .collect(Collectors.toCollection(ArrayList::new));

	    if (CollectionUtils.isNotEmpty(aAssets)) {
		final int nAssetsSize = aAssets.size();

		final int nRows = nAssetsSize < 4 ? 1 : nAssetsSize == 6 ? 2 : nAssetsSize < 15 ? (nAssetsSize % 3 != 0) ? 2 : 3 : 3;
		final int nColumns = Math.min(nAssetsSize / nRows, 8);
		final int nAssetWdith = 100 / (nColumns > 1 ? nColumns - 1 : 1);
		final int nAssetHeight = nRows == 1 ? 0 : (100 / (nRows - 1));
		int nX = 0;
		int nY = 0;

		final StringBuilder aSB = new StringBuilder();
		aSB.append("background: ");
		for (int i = 0; i < nColumns * nRows; i++) {
		    if (i > 0)
			aSB.append(", ");

		    aSB.append("url(" + aAssets.get(i).getThumbnailStreamURL() + ") no-repeat " + nX + "% " + nY + "%");
		    nX += nAssetWdith;
		    if (nX > 100) {
			nX = 0;
			nY += nAssetHeight;
		    }
		}

		aSB.append("; background-size: " + (Math.ceil(100 / nColumns) + 1) + "% " + (Math.ceil(100 / nRows) + 1) + "%;");

		return aSB.toString();
	    }
	}

	return "";
    }

    @Nonnull
    public Collection<Set> getChilds(@Nullable final Set aSet) {
	if (aSet != null) {
	    final User aUser = SessionUtils.getInstance().getLoggedInUser();

	    return aSet.getChildSetIds().stream().map(nChildSetId -> _managerInstance().getRead(aUser, nChildSetId)).filter(o -> o != null)
		    .collect(Collectors.toCollection(ArrayList::new));
	}

	return new ArrayList<Set>();
    }

    @Nullable
    public Set getParent(@Nullable final Asset aAsset) {
	return _managerInstance().getParent(aAsset);
    }

    @Nullable
    public Set getParent(@Nullable final Set aSet) {
	return _managerInstance().getParent(aSet);
    }

    @Nonnull
    public Collection<Set> getParents(@Nullable final Set aSet) {
	return _managerInstance().getParents(aSet);
    }

    @Nonnull
    public Collection<Set> getRead(@Nullable final User aFor) {
	final User aUser = SessionUtils.getInstance().getLoggedInUser();

	return _managerInstance().allFor(aUser, aFor);
    }
}
