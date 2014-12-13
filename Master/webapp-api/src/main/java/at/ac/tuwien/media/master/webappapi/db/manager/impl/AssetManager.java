package at.ac.tuwien.media.master.webappapi.db.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.db.manager.AbstractManager;
import at.ac.tuwien.media.master.webappapi.db.model.Asset;
import at.ac.tuwien.media.master.webappapi.db.model.EFileType;
import at.ac.tuwien.media.master.webappapi.db.model.Group;
import at.ac.tuwien.media.master.webappapi.db.model.Set;
import at.ac.tuwien.media.master.webappapi.db.model.User;
import at.ac.tuwien.media.master.webappapi.fs.manager.FSManager;
import at.ac.tuwien.media.master.webappapi.util.Value;

public class AssetManager extends AbstractManager<Asset> {
    private static AssetManager m_aInstance = new AssetManager();

    private AssetManager() {
	super(Value.DB_COLLECTION_ASSETS);

	if (f_aEntries.isEmpty()) {
	    save(new Asset(Value.DATA_PATH_ASSETS + "Louis.webm", "").setPublish(true));
	    save(new Asset(Value.DATA_PATH_ASSETS + "pdf.pdf", "").setPublish(true).setMetadata(true));
	    save(new Asset(Value.DATA_PATH_ASSETS + "elephant1.jpg", "").setMetadata(true).setShowOnMainPage(true));
	    save(new Asset(Value.DATA_PATH_ASSETS + "elephant2.jpg", "").setMetadata(true).setShowOnMainPage(true));
	    save(new Asset(Value.DATA_PATH_ASSETS + "elephant3.jpg", "").setMetadata(true).setShowOnMainPage(true));
	    save(new Asset(Value.DATA_PATH_ASSETS + "elephant4.jpg", "").setMetadata(true).setShowOnMainPage(true));
	}
    }

    public static AssetManager getInstance() {
	return m_aInstance;
    }

    @Nonnull
    public Collection<Asset> allReadForParent(@Nonnull final User aUser, @Nonnull final Asset aAsset) {
	final Set aParentSet = SetManager.getInstance().getParent(aAsset);

	if (aParentSet != null) {
	    final Collection<Group> aGroups = GroupManager.getInstance().allFor(aUser, aParentSet);

	    if (CollectionUtils.isNotEmpty(aGroups)) {
		final boolean bHasReadRights = aGroups.stream().filter(aGroup -> aGroup.getPermissionFor(aParentSet).isRead()).findFirst().orElse(null) != null;

		if (bHasReadRights)
		    return aParentSet.getAssetsIds().stream().map(nId -> get(nId)).collect(Collectors.toCollection(ArrayList::new));
	    }
	}

	return new ArrayList<Asset>();
    }

    @Nonnull
    public Collection<Asset> allShowOnMainPage() {
	m_aRWLock.readLock().lock();

	final ArrayList<Asset> aAssets = f_aEntries.values().stream().parallel()
	        .filter(aAsset -> aAsset.isPublish() && aAsset.isShowOnMainPage() && aAsset.getFileType() == EFileType.IMAGE)
	        .collect(Collectors.toCollection(ArrayList::new));

	m_aRWLock.readLock().unlock();

	return aAssets;
    }

    @Nullable
    private Asset _getFromHash(@Nonnull final String sHash) {
	Asset aFound = null;

	if (StringUtils.isNotEmpty(sHash)) {
	    m_aRWLock.readLock().lock();

	    aFound = f_aEntries.values().stream().filter(aAsset -> aAsset.getHash().equals(sHash)).findFirst().orElse(null);

	    m_aRWLock.readLock().unlock();
	}

	return aFound;
    }

    private Asset _returnReadOrNull(@Nullable final User aUser, @Nullable final Asset aAsset) {
	if (aAsset != null)
	    if (aAsset.is_Public() || aAsset.isPublish())
		return aAsset;
	    else if (aUser != null) {
		final Set aParentSet = SetManager.getInstance().getParent(aAsset);

		if (aParentSet != null && GroupManager.getInstance().isRead(aUser, aParentSet))
		    return aAsset;
	    }

	return null;
    }

    @Nullable
    public Asset getRead(@Nullable final User aUser, @Nullable final long nId) {
	return _returnReadOrNull(aUser, get(nId));
    }

    @Nullable
    public Asset getRead(@Nullable final User aUser, @Nullable final String sHash) {
	return _returnReadOrNull(aUser, _getFromHash(sHash));
    }

    @Override
    public boolean delete(@Nullable final Asset aEntry) {
	if (aEntry != null && contains(aEntry))
	    if (SetManager.getInstance().removeFromAll(aEntry) && HashTagManager.getInstance().removeFromAll(aEntry))
		if (FSManager.delete(aEntry))
		    return super.delete(aEntry);

	return false;
    }

    @Nonnull
    public boolean save(final long nParentSetId, @Nullable final Asset aAsset) {
	if (aAsset != null) {
	    final Set aParentSet = SetManager.getInstance().get(nParentSetId);

	    // add to parent
	    if (aParentSet != null && aParentSet.add(aAsset))
		if (SetManager.getInstance().save(aParentSet))
		    return save(aAsset);
	}

	return false;
    }
}
