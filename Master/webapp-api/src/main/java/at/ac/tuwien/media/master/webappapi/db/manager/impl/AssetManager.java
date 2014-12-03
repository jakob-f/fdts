package at.ac.tuwien.media.master.webappapi.db.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.ac.tuwien.media.master.webappapi.db.manager.AbstractManager;
import at.ac.tuwien.media.master.webappapi.db.model.Asset;
import at.ac.tuwien.media.master.webappapi.db.model.EFileType;
import at.ac.tuwien.media.master.webappapi.db.model.Set;
import at.ac.tuwien.media.master.webappapi.fs.manager.FSManager;
import at.ac.tuwien.media.master.webappapi.util.Value;

public class AssetManager extends AbstractManager<Asset> {
    private static AssetManager m_aInstance = new AssetManager();

    private AssetManager() {
	super(Value.DB_COLLECTION_ASSETS);

	if (f_aEntries.isEmpty()) {
	    save(new Asset(Value.DATA_PATH_ASSETS + "Louis.webm", "").setPublish(true));
	    save(new Asset(Value.DATA_PATH_ASSETS + "pdf.pdf", "").setPublish(true).setMetadata(true));
	    save(new Asset(Value.DATA_PATH_ASSETS + "CATastrophe.mp4", "").setPublish(true));
	    save(new Asset(Value.DATA_PATH_ASSETS + "big_buck_bunny.webm", ""));
	    save(new Asset(Value.DATA_PATH_ASSETS + "big_buck_bunny.ogv", ""));
	    save(new Asset(Value.DATA_PATH_ASSETS + "big_buck_bunny.mp4", ""));
	    save(new Asset(Value.DATA_PATH_ASSETS + "1.jpg", ""));
	    save(new Asset(Value.DATA_PATH_ASSETS + "2.jpg", ""));
	    save(new Asset(Value.DATA_PATH_ASSETS + "3.jpg", "").setMetadata(true));
	    save(new Asset(Value.DATA_PATH_ASSETS + "4.png", ""));
	    save(new Asset(Value.DATA_PATH_ASSETS + "5.png", "").setMetadata(true));
	    save(new Asset(Value.DATA_PATH_ASSETS + "elephant1.jpg", "").setMetadata(true).setShowOnMainPage(true));
	    save(new Asset(Value.DATA_PATH_ASSETS + "elephant2.jpg", "").setMetadata(true).setShowOnMainPage(true));
	    save(new Asset(Value.DATA_PATH_ASSETS + "elephant3.jpg", "").setMetadata(true).setShowOnMainPage(true));
	    save(new Asset(Value.DATA_PATH_ASSETS + "elephant4.jpg", "").setMetadata(true).setShowOnMainPage(true));
	}
    }

    public static AssetManager getInstance() {
	return m_aInstance;
    }

    @Override
    @Nonnull
    public boolean save(@Nullable final Asset aAsset) {
	// new set: create files on file system
	if (!contains(aAsset)) {
	    if (FSManager.save(aAsset))
		return super.save(aAsset);
	    else
		return false;
	}

	// save or update asset
	return super.save(aAsset);
    }

    @Nonnull
    public boolean save(final long nParentSetId, @Nullable final Asset aAsset) {
	final Set aParentSet = SetManager.getInstance().get(nParentSetId);

	// try to add to parent
	if (aParentSet != null && aParentSet.addAsset(aAsset))
	    if (SetManager.getInstance().save(aParentSet))
		return save(aAsset);

	return false;
    }

    @Nullable
    public Asset getPublishedAsset(@Nonnull final String sHash) {
	Asset aFoundAsset = null;

	if (sHash != null) {
	    aRWLock.readLock().lock();

	    aFoundAsset = f_aEntries.values().stream().filter(aAsset -> aAsset.isPublish() && aAsset.getHash().equals(sHash)).findFirst().orElse(null);

	    aRWLock.readLock().unlock();
	}

	return aFoundAsset;
    }

    @Nullable
    public Collection<Asset> getShowOnMainPageAssets() {
	aRWLock.readLock().lock();

	final ArrayList<Asset> aAssets = f_aEntries.values().stream().parallel()
	        .filter(aAsset -> aAsset.isPublish() && aAsset.isShowOnMainPage() && aAsset.getFileType() == EFileType.IMAGE)
	        .collect(Collectors.toCollection(ArrayList::new));

	aRWLock.readLock().unlock();

	return aAssets;
    }
}
