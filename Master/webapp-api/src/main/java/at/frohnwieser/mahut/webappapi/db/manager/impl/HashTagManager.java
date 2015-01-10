package at.frohnwieser.mahut.webappapi.db.manager.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.commons.CommonValue;
import at.frohnwieser.mahut.commons.IdFactory;
import at.frohnwieser.mahut.webappapi.db.manager.AbstractManager;
import at.frohnwieser.mahut.webappapi.db.model.Asset;
import at.frohnwieser.mahut.webappapi.db.model.HashTag;
import at.frohnwieser.mahut.webappapi.db.model.Set;
import at.frohnwieser.mahut.webappapi.util.Value;

public class HashTagManager extends AbstractManager<HashTag> {
    private static HashTagManager m_aInstance = new HashTagManager();

    // protected final SortedSet<Fun.Tuple2<String, Long>> f_aTags;

    private HashTagManager() {
	super(Value.DB_COLLECTION_HASHTAGS);

	// Bind.secondaryKey(f_aEntries, f_aTags, (aHashTag) ->
	// aHashTag.getTag());
    }

    public static HashTagManager getInstance() {
	return m_aInstance;
    }

    @Nonnull
    public Collection<HashTag> allFor(@Nullable final Asset aAsset) {
	if (aAsset != null) {
	    m_aRWLock.readLock().lock();

	    final Collection<HashTag> aEntries = f_aEntries.values().stream().filter(aHashTag -> aHashTag.contains(aAsset))
		    .collect(Collectors.toCollection(ArrayList::new));

	    m_aRWLock.readLock().unlock();

	    return aEntries;
	}

	return new ArrayList<HashTag>();
    }

    @Nonnull
    public Collection<HashTag> allFor(@Nullable final Set aSet) {
	if (aSet != null) {
	    m_aRWLock.readLock().lock();

	    final Collection<HashTag> aEntries = f_aEntries.values().stream().filter(aHashTag -> aHashTag.contains(aSet))
		    .collect(Collectors.toCollection(ArrayList::new));

	    m_aRWLock.readLock().unlock();

	    return aEntries;
	}

	return new ArrayList<HashTag>();
    }

    @Nullable
    public HashTag get(@Nonnull final String sTagName) {
	final long nId = IdFactory.getBase36(sTagName);
	HashTag aFound = get(nId);

	// (in extremely rare cases) the id of a hash tag might not be unique
	if (aFound != null && !aFound.getTag().equals(sTagName)) {
	    m_aRWLock.readLock().lock();

	    aFound = f_aEntries.values().stream().filter(HashTag -> HashTag.getTag().equals(sTagName)).findFirst().orElse(null);

	    m_aRWLock.readLock().unlock();
	}

	return aFound;
    }

    @Nonnull
    public Collection<HashTag> get(@Nullable final String... sTagNames) {
	if (ArrayUtils.isNotEmpty(sTagNames)) {
	    m_aRWLock.readLock().lock();

	    final Collection<HashTag> aEntries = Arrays.stream(sTagNames).map(sTagName -> get(sTagName)).filter(o -> o != null)
		    .collect(Collectors.toCollection(ArrayList::new));

	    m_aRWLock.readLock().unlock();

	    return aEntries;
	}

	return new ArrayList<HashTag>();
    }

    @Nullable
    private HashTag _getOrCreate(@Nullable final String sText) {
	if (StringUtils.isNotEmpty(sText))
	    if (sText.length() > 1 && sText.startsWith(CommonValue.CHARACTER_HASH)) {
		final String sHashTag = sText.substring(1, sText.length());

		HashTag aHashTag = get(sHashTag);
		if (aHashTag == null)
		    aHashTag = new HashTag(sHashTag);

		return aHashTag;
	    }

	return null;
    }

    public boolean save(@Nullable final Asset aAsset, @Nonnull final String sText) {
	if (aAsset != null) {
	    // remove all old entries
	    if (removeFromAll(aAsset)) {
		if (StringUtils.isNotEmpty(sText)) {
		    final StringTokenizer aTokenizer = new StringTokenizer(sText);

		    // save all new hash tags
		    while (aTokenizer.hasMoreTokens()) {
			final HashTag aHashTag = _getOrCreate(aTokenizer.nextToken());

			if (aHashTag != null) {
			    aHashTag.add(aAsset);
			    save(aHashTag);
			}
		    }
		}

		return true;
	    }
	}

	return false;
    }

    public boolean save(@Nullable final Set aSet, @Nonnull final String sText) {
	if (aSet != null) {
	    // remove all old entries
	    if (removeFromAll(aSet)) {
		if (StringUtils.isNotEmpty(sText)) {
		    final StringTokenizer aTokenizer = new StringTokenizer(sText);

		    // save all new hash tags
		    while (aTokenizer.hasMoreTokens()) {
			final HashTag aHashTag = _getOrCreate(aTokenizer.nextToken());

			if (aHashTag != null) {
			    aHashTag.add(aSet);
			    save(aHashTag);
			}
		    }
		}

		return true;
	    }
	}

	return false;
    }

    private boolean _checkEmpty(@Nullable final HashTag aHashTag) {
	if (aHashTag != null)
	    if (aHashTag.getAssetIds().isEmpty() && aHashTag.getSetIds().isEmpty())
		return delete(aHashTag);

	return false;
    }

    public boolean removeFromAll(@Nullable final Asset aAsset) {
	if (aAsset != null) {
	    all().stream().filter(aHashTag -> aHashTag.remove(aAsset)).forEach(aHashTag -> {
		if (!_checkEmpty(aHashTag))
		    save(aHashTag);
	    });

	    return true;
	}

	return false;
    }

    public boolean removeFromAll(@Nullable final Set aSet) {
	if (aSet != null) {
	    all().stream().filter(aHashTag -> aHashTag.remove(aSet)).forEach(aHashTag -> {
		if (!_checkEmpty(aHashTag))
		    save(aHashTag);
	    });

	    return true;
	}

	return false;
    }
}