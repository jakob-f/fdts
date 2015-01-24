package at.frohnwieser.mahut.webappapi.db.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.commons.IdFactory;
import at.frohnwieser.mahut.webappapi.db.manager.AbstractManager;
import at.frohnwieser.mahut.webappapi.db.model.Asset;
import at.frohnwieser.mahut.webappapi.db.model.HashTag;
import at.frohnwieser.mahut.webappapi.db.model.Set;
import at.frohnwieser.mahut.webappapi.ontology.OntologyManager;
import at.frohnwieser.mahut.webappapi.util.TagParser;
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
	final String sTagNameCaseInsesitive = sTagName.toLowerCase();
	final long nId = IdFactory.getIdFrom(sTagNameCaseInsesitive);
	HashTag aFound = get(nId);

	// (in extremely rare cases) the id of a hash tag might not be unique
	if (aFound != null && !aFound.getTag().equals(sTagNameCaseInsesitive)) {
	    m_aRWLock.readLock().lock();

	    aFound = f_aEntries.values().stream().filter(HashTag -> HashTag.getTag().equals(sTagNameCaseInsesitive)).findFirst().orElse(null);

	    m_aRWLock.readLock().unlock();
	}

	return aFound;
    }

    @Nonnull
    public Collection<HashTag> get(@Nullable final Collection<String> aTags) {
	if (CollectionUtils.isNotEmpty(aTags))
	    return aTags.stream().map(sTagName -> get(sTagName)).filter(o -> o != null).collect(Collectors.toCollection(ArrayList::new));

	return new ArrayList<HashTag>();
    }

    @Nullable
    private HashTag _getOrCreate(@Nullable final String sTag) {
	if (StringUtils.isNotEmpty(sTag)) {
	    HashTag aHashTag = get(sTag);

	    if (aHashTag == null)
		aHashTag = new HashTag(sTag);

	    return aHashTag;
	}

	return null;
    }

    @Nonnull
    public Collection<HashTag> getOthers(@Nullable final Collection<String> aTags) {
	return get(OntologyManager.getInstance().getEqualClasses(aTags));
    }

    public boolean save(@Nullable final Asset aAsset) {
	if (aAsset != null) {
	    // remove all old entries
	    if (removeFromAll(aAsset)) {
		// save all new hash tags
		TagParser.parseHashTags(aAsset.getMetaContent()).forEach(sTag -> {
		    final HashTag aHashTag = _getOrCreate(sTag);
		    if (aHashTag != null) {
			aHashTag.add(aAsset);
			save(aHashTag);
		    }
		});

		return true;
	    }
	}

	return false;
    }

    public boolean save(@Nullable final Set aSet) {
	if (aSet != null) {
	    // remove all old entries
	    if (removeFromAll(aSet)) {
		// save all new hash tags
		TagParser.parseHashTags(aSet.getMetaContent()).forEach(sTag -> {
		    final HashTag aHashTag = _getOrCreate(sTag);
		    if (aHashTag != null) {
			aHashTag.add(aSet);
			save(aHashTag);
		    }
		});

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