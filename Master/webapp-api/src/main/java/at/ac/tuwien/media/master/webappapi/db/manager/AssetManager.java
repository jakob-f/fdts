package at.ac.tuwien.media.master.webappapi.db.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.ac.tuwien.media.master.webappapi.db.model.Asset;
import at.ac.tuwien.media.master.webappapi.db.model.EFileType;
import at.ac.tuwien.media.master.webappapi.util.Value;

public class AssetManager {
    private static AssetManager m_aInstance = new AssetManager();
    private static ReadWriteLock aRWLock;
    private static Collection<Asset> s_aAssets;

    static {
	aRWLock = new ReentrantReadWriteLock();

	// s_aAssets =
	// DBManager.getInstance()._getDataBase().getHashSet(Value.DB_COLLECTION_ASSETS);
	s_aAssets = new ArrayList<Asset>();

	s_aAssets.add(new Asset(Value.DATA_PATH_ASSETS + "Louis.webm", "").setPublish(true));
	s_aAssets.add(new Asset(Value.DATA_PATH_ASSETS + "pdf.pdf", "").setPublish(true).setMetadata(true));
	s_aAssets.add(new Asset(Value.DATA_PATH_ASSETS + "CATastrophe.mp4", "").setPublish(true));
	s_aAssets.add(new Asset(Value.DATA_PATH_ASSETS + "big_buck_bunny.webm", ""));
	s_aAssets.add(new Asset(Value.DATA_PATH_ASSETS + "big_buck_bunny.ogv", ""));
	s_aAssets.add(new Asset(Value.DATA_PATH_ASSETS + "big_buck_bunny.mp4", ""));
	s_aAssets.add(new Asset(Value.DATA_PATH_ASSETS + "1.jpg", ""));
	s_aAssets.add(new Asset(Value.DATA_PATH_ASSETS + "2.jpg", ""));
	s_aAssets.add(new Asset(Value.DATA_PATH_ASSETS + "3.jpg", "").setMetadata(true));
	s_aAssets.add(new Asset(Value.DATA_PATH_ASSETS + "4.png", ""));
	s_aAssets.add(new Asset(Value.DATA_PATH_ASSETS + "5.png", "").setMetadata(true));
	s_aAssets.add(new Asset(Value.DATA_PATH_ASSETS + "elephant1.jpg", "").setMetadata(true).setShowOnMainPage(true));
	s_aAssets.add(new Asset(Value.DATA_PATH_ASSETS + "elephant2.jpg", "").setMetadata(true).setShowOnMainPage(true));
	s_aAssets.add(new Asset(Value.DATA_PATH_ASSETS + "elephant3.jpg", "").setMetadata(true).setShowOnMainPage(true));
	s_aAssets.add(new Asset(Value.DATA_PATH_ASSETS + "elephant4.jpg", "").setMetadata(true).setShowOnMainPage(true));
    }

    private AssetManager() {
    }

    public static AssetManager getInstance() {
	return m_aInstance;
    }

    @Nonnull
    public Collection<Asset> all() {
	final Collection<Asset> aAssets = new ArrayList<Asset>();

	aRWLock.readLock().lock();

	aAssets.addAll(s_aAssets);

	aRWLock.readLock().unlock();

	return aAssets;
    }

    @Nonnull
    public Collection<Asset> delete(@Nonnull final Asset aAsset) {
	if (aAsset == null)
	    throw new NullPointerException("asset");

	aRWLock.writeLock().lock();

	s_aAssets.remove(aAsset);

	aRWLock.writeLock().unlock();

	return all();
    }

    @Nonnull
    public Collection<Asset> merge(@Nonnull final Asset aAsset) {
	if (aAsset == null)
	    throw new NullPointerException("asset");

	aRWLock.writeLock().lock();

	s_aAssets.stream().filter(aOld -> aOld.getHash().equals(aAsset.getHash())).findFirst().ifPresent(aOld -> aOld = aAsset);

	aRWLock.writeLock().unlock();

	return all();
    }

    @Nonnull
    public Collection<Asset> read(@Nonnull final Asset aAsset) {
	// TODO
	return null;
    }

    @Nonnull
    public Collection<Asset> save(@Nonnull final Asset aAsset) {
	if (aAsset == null)
	    throw new NullPointerException("asset");

	aRWLock.writeLock().lock();

	s_aAssets.add(aAsset);

	aRWLock.writeLock().unlock();

	return all();
    }

    @Nullable
    public Asset getPublishedAsset(@Nonnull final String sHash) {
	if (sHash == null)
	    throw new NullPointerException("hash");

	aRWLock.readLock().lock();

	final Asset aFoundAsset = s_aAssets.stream().filter(aAsset -> aAsset.isPublish() && aAsset.getHash().equals(sHash)).findFirst().orElse(null);

	aRWLock.readLock().unlock();

	return aFoundAsset;
    }

    @Nonnull
    public Collection<Asset> getShowOnMainPageAssets() {
	aRWLock.readLock().lock();

	final Collection<Asset> aAssets = s_aAssets.stream().parallel()
	        .filter(aAsset -> aAsset.isPublish() && aAsset.isShowOnMainPage() && aAsset.getFileType() == EFileType.IMAGE)
	        .collect(Collectors.toCollection(ArrayList::new));

	aRWLock.readLock().unlock();

	return aAssets;
    }
}
