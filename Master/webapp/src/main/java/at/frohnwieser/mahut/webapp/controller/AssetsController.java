package at.frohnwieser.mahut.webapp.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.webapp.util.SessionUtils;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.db.manager.AssetManager;
import at.frohnwieser.mahut.webappapi.db.model.Asset;
import at.frohnwieser.mahut.webappapi.db.model.EState;
import at.frohnwieser.mahut.webappapi.db.model.Set;
import at.frohnwieser.mahut.webappapi.db.model.User;
import at.frohnwieser.mahut.webappapi.fs.manager.FSManager;

@SuppressWarnings("serial")
@RequestScoped
@Named
public class AssetsController extends AbstractDBObjectController<Asset> {
    @Inject
    WallpaperController m_aWallpaperController;

    @SuppressWarnings("unchecked")
    @Override
    protected AssetManager _managerInstance() {
	return AssetManager.getInstance();
    }

    @Override
    public boolean save(final Asset aEntry) {
	final boolean bRet = super.save(aEntry);
	m_aWallpaperController._loadWPs();
	return bRet;
    }

    @Nonnull
    private Collection<Asset> _getFrom(@Nullable final Set aSet, final boolean bIsMetaContent) {
	if (aSet != null) {
	    final User aUser = SessionUtils.getInstance().getLoggedInUser();
	    ArrayList<Asset> aAssets = null;
	    // when set is published show also published assets
	    if (aSet.getState() == EState.PUBLISHED)
		aAssets = aSet.getAssetIds().stream().map(nAssetId -> _managerInstance().getPublished(aUser, nAssetId))
		        .filter(aAsset -> aAsset != null && (aAsset.isMetaContent() ^ !bIsMetaContent)).collect(Collectors.toCollection(ArrayList::new));
	    else
		aAssets = aSet.getAssetIds().stream().map(nAssetId -> _managerInstance().getRead(aUser, nAssetId))
		        .filter(aAsset -> aAsset != null && (aAsset.isMetaContent() ^ !bIsMetaContent)).collect(Collectors.toCollection(ArrayList::new));

	    if (CollectionUtils.isNotEmpty(aAssets)) {
		Collections.sort(aAssets);
		return aAssets;
	    }
	}
	return new ArrayList<Asset>();
    }

    @Nonnull
    public Collection<Asset> getMetaContentFrom(@Nullable final Set aSet) {
	return _getFrom(aSet, true);
    }

    @Nonnull
    public Collection<Asset> getMaterialsFrom(@Nullable final Set aSet) {
	return _getFrom(aSet, false);
    }

    @Nonnull
    public Collection<Asset> getOthersFrom(@Nullable final Set aSet, @Nullable final Asset aAsset) {
	if (aSet != null) {
	    final User aUser = SessionUtils.getInstance().getLoggedInUser();
	    final ArrayList<Asset> aAssets = aSet.getAssetIds().stream().filter(sAssetId -> !sAssetId.equals(aAsset.getId()))
		    .map(sAssetId -> _managerInstance().getRead(aUser, sAssetId)).filter(o -> o != null).collect(Collectors.toCollection(ArrayList::new));
	    Collections.sort(aAssets);
	    return aAssets;
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

    @Nonnull
    public String getFileSize(@Nullable final Asset aAsset) {
	if (aAsset != null)
	    return FileUtils.byteCountToDisplaySize(new File(FSManager.getAbsoluteFilePath(aAsset, false)).length());
	return null;
    }
}
