package at.frohnwieser.mahut.webapp.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.webapp.util.SessionUtils;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.db.manager.SetManager;
import at.frohnwieser.mahut.webappapi.db.model.Asset;
import at.frohnwieser.mahut.webappapi.db.model.Set;
import at.frohnwieser.mahut.webappapi.db.model.User;

@SuppressWarnings("serial")
@ViewScoped
@Named
public class SetsController extends AbstractDBObjectController<Set> {
    @Inject
    private AssetsController m_aAssetsController;
    @Inject
    private GroupsController m_aGroupsController;

    private Set m_aCurrentParent;

    @SuppressWarnings("unchecked")
    @Override
    protected SetManager _managerInstance() {
	return SetManager.getInstance();
    }

    @Override
    @Nonnull
    protected Set _new() {
	return new Set(SessionUtils.getInstance().getLoggedInUser().getId(), "", "");
    }

    @Override
    public void reload() {
	super.reload();
	m_aGroupsController.reload();
    }

    public boolean isRead(@Nullable final Set aSet) {
	return _managerInstance().isRead(SessionUtils.getInstance().getLoggedInUser(), aSet);
    }

    // TODO better?
    @Nonnull
    public String getBgStyle(@Nullable final Set aSet) {
	if (aSet != null) {
	    final List<Asset> aAssets = (List<Asset>) m_aAssetsController.getMaterialsFrom(aSet);

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
    public Collection<Set> getChildren(@Nullable final Set aSet) {
	if (aSet != null) {
	    final User aUser = SessionUtils.getInstance().getLoggedInUser();
	    final List<Set> aChildren = aSet.getChildSetIds().stream().map(sChildSetId -> _managerInstance().getRead(aUser, sChildSetId))
		    .filter(o -> o != null).collect(Collectors.toCollection(ArrayList::new));
	    Collections.sort(aChildren);
	    return aChildren;
	}
	return new ArrayList<Set>();
    }

    @Nullable
    public Set getFromParamter() {
	final String sRequestParameter = SessionUtils.getInstance().getRequestParameter(Value.REQUEST_PARAMETER_SET);
	if (StringUtils.isNotEmpty(sRequestParameter) && sRequestParameter.matches(Value.REGEX_RESOURCE_HASH))
	    return _managerInstance().getFromHash(SessionUtils.getInstance().getLoggedInUser(), sRequestParameter);
	return null;
    }

    @Nonnull
    public Set getCurrentSet() {
	if (SessionUtils.getInstance().hasRequestParameter(Value.REQUEST_PARAMETER_SET))
	    m_aCurrentParent = getFromParamter();
	if (m_aCurrentParent == null)
	    m_aCurrentParent = _managerInstance().get(at.frohnwieser.mahut.webappapi.util.Value.ROOT_SET_ID);
	return m_aCurrentParent;
    }

    public boolean saveInCurrentParent() {
	if (_managerInstance().save(m_aCurrentParent.getId(), m_aEntry)) {
	    setSelectedEntry(m_aEntry);
	    reload();

	    if (!SessionUtils.getInstance().hasMessage())
		SessionUtils.getInstance().info("successfully saved", "");
	    return true;
	}
	return false;
    }

    @Nullable
    public Set getParent(@Nullable final Asset aAsset) {
	return _managerInstance().getParent(aAsset);
    }

    @Nonnull
    public Collection<Set> getParents(@Nullable final Set aSet) {
	return _managerInstance().getParents(aSet);
    }

    @Nonnull
    public Collection<Set> getRead(@Nullable final User aFor) {
	final List<Set> aSets = (List<Set>) _managerInstance().allFor(SessionUtils.getInstance().getLoggedInUser(), aFor);
	Collections.sort(aSets);
	return aSets;
    }
}
