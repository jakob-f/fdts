package at.frohnwieser.mahut.webappapi.db.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.webappapi.db.model.Asset;
import at.frohnwieser.mahut.webappapi.db.model.EFileType;
import at.frohnwieser.mahut.webappapi.db.model.ERole;
import at.frohnwieser.mahut.webappapi.db.model.EState;
import at.frohnwieser.mahut.webappapi.db.model.Set;
import at.frohnwieser.mahut.webappapi.db.model.User;
import at.frohnwieser.mahut.webappapi.fs.manager.FSManager;
import at.frohnwieser.mahut.webappapi.util.Value;

public class AssetManager extends AbstractManager<Asset> {
    private static AssetManager m_aInstance = new AssetManager();

    private AssetManager() {
	super(Value.DB_COLLECTION_ASSETS);
    }

    public static AssetManager getInstance() {
	return m_aInstance;
    }

    @Nonnull
    public Collection<Asset> allMainPage() {
	return f_aEntries.values().stream().parallel().filter(aAsset -> aAsset.getState().is(EState.HOME_PAGE) && aAsset.getFileType() == EFileType.IMAGE)
	        .collect(Collectors.toCollection(ArrayList::new));
    }

    @Nullable
    private Asset _getFromHash(@Nonnull final String sHash) {
	if (StringUtils.isNotEmpty(sHash))
	    return f_aEntries.values().stream().filter(aAsset -> aAsset.getHash().equals(sHash)).findFirst().orElse(null);

	return null;
    }

    @Nullable
    private Asset _checkUserOrReturnNull(@Nullable final User aUser, @Nullable final Asset aAsset) {
	if (aUser != null && aAsset != null) {
	    if (aUser.getRole().is(ERole.ADMIN))
		return aAsset;

	    final Set aParentSet = SetManager.getInstance().getParent(aAsset);
	    if (aParentSet != null && GroupManager.getInstance().isRead(aUser, aParentSet))
		return aAsset;
	}

	return null;
    }

    @Nullable
    private Asset _getRead(@Nullable final User aUser, @Nullable final Asset aAsset) {
	return aAsset != null ? aAsset.getState() == EState.PUBLIC || aAsset.getState() == EState.HOME_PAGE ? aAsset : _checkUserOrReturnNull(aUser, aAsset)
	        : null;
    }

    @Nullable
    private Asset _getPublished(@Nullable final User aUser, @Nullable final Asset aAsset) {
	// TODO performance ??
	return aAsset != null ? aAsset.getState().is(EState.PUBLISHED) ? aAsset : _getRead(aUser, aAsset) : null;
    }

    @Nullable
    public Asset getPublished(@Nullable final User aUser, @Nullable final String sId) {
	return _getPublished(aUser, get(sId));
    }

    @Nullable
    public Asset getFromHash(@Nullable final User aUser, @Nullable final String sHash) {
	return _getPublished(aUser, _getFromHash(sHash));
    }

    @Nullable
    public Asset getRead(@Nullable final User aUser, @Nullable final String sId) {
	return _getRead(aUser, get(sId));
    }

    @Override
    public boolean delete(@Nullable final Asset aEntry) {
	if (aEntry != null && contains(aEntry))
	    if (SetManager.getInstance()._removeFromAll(aEntry) && HashTagManager.getInstance()._removeFromAll(aEntry) && FSManager.delete(aEntry))
		return _deleteCommit(aEntry);

	return false;
    }

    @Override
    public boolean save(@Nullable final Asset aEntry) {
	return _saveCommit(aEntry);
    }

    /**
     * does not commit
     */
    protected boolean _setStates(@Nullable final Collection<String> aAssetIds, @Nullable final EState aState) {
	if (aState != null) {
	    if (CollectionUtils.isNotEmpty(aAssetIds))
		aAssetIds.parallelStream().map(sAssetId -> get(sAssetId)).filter(o -> o != null)
		        .forEach(aAsset -> _internalSave((Asset) aAsset.setState(aState)));

	    return true;
	}

	return false;
    }
}
