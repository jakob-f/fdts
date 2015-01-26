package at.frohnwieser.mahut.webappapi.db.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.webappapi.config.Configuration;
import at.frohnwieser.mahut.webappapi.config.Configuration.EField;
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

	if (f_aEntries.isEmpty() && Configuration.getInstance().getAsBoolean(EField.TEST)) {
	    final String sAssetsPath = FSManager.createGetAssetsFolder().getAbsolutePath() + File.separator;

	    save(new Asset(sAssetsPath + "Louis.webm", "", Value.ROOT_SET_ID).setState(EState.PUBLISHED));
	    save(new Asset(sAssetsPath + "pdf.pdf", "", Value.ROOT_SET_ID).setMetaContent(true).setState(EState.PUBLISHED));
	    save(new Asset(sAssetsPath + "elephant1.jpg", "", Value.ROOT_SET_ID).setMetaContent(true).setState(EState.MAIN_PAGE));
	    save(new Asset(sAssetsPath + "elephant2.jpg", "", Value.ROOT_SET_ID).setMetaContent(true).setState(EState.MAIN_PAGE));
	    save(new Asset(sAssetsPath + "elephant3.jpg", "", Value.ROOT_SET_ID).setMetaContent(true).setState(EState.MAIN_PAGE));
	    save(new Asset(sAssetsPath + "elephant4.jpg", "", Value.ROOT_SET_ID).setMetaContent(true).setState(EState.MAIN_PAGE));
	}
    }

    public static AssetManager getInstance() {
	return m_aInstance;
    }

    @Nonnull
    public Collection<Asset> allMainPage() {
	m_aRWLock.readLock().lock();

	final ArrayList<Asset> aAssets = f_aEntries.values().stream().parallel()
	        .filter(aAsset -> aAsset.getState().is(EState.MAIN_PAGE) && aAsset.getFileType() == EFileType.IMAGE)
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

    private Asset _checkUserOrReturnNull(@Nullable final User aUser, @Nullable final Asset aAsset) {
	if (aUser != null && aAsset != null) {
	    if (aUser.getRole().is(ERole.ADMIN))
		return aAsset;

	    final Set aParentSet = SetManager.getInstance().getParent(aAsset);

	    if (aParentSet != null && GroupManager.getInstance().isRead(aUser, aParentSet)) {
		return aAsset;
	    }
	}

	return null;
    }

    @Nullable
    private Asset _getRead(@Nullable final User aUser, @Nullable final Asset aAsset) {
	return aAsset != null ? aAsset.getState() == EState.PUBLIC || aAsset.getState() == EState.MAIN_PAGE ? aAsset : _checkUserOrReturnNull(aUser, aAsset)
	        : null;
    }

    @Nullable
    private Asset _getPublished(@Nullable final User aUser, @Nullable final Asset aAsset) {
	return aAsset != null ? aAsset.getState().is(EState.PUBLISHED) ? aAsset : _getRead(aUser, aAsset) : null;
    }

    @Nullable
    public Asset getPublished(@Nullable final User aUser, @Nullable final long nId) {
	return _getPublished(aUser, get(nId));
    }

    @Nullable
    public Asset getFromHash(@Nullable final User aUser, @Nullable final String sHash) {
	return _getPublished(aUser, _getFromHash(sHash));
    }

    @Nullable
    public Asset getRead(@Nullable final User aUser, @Nullable final long nId) {
	return _getRead(aUser, get(nId));
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
