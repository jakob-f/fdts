package at.ac.tuwien.media.master.webappapi.db.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.commons.CommonValue;
import at.ac.tuwien.media.master.commons.IHasId;
import at.ac.tuwien.media.master.commons.IdFactory;
import at.ac.tuwien.media.master.webappapi.db.manager.AbstractManager;
import at.ac.tuwien.media.master.webappapi.db.model.HashTag;
import at.ac.tuwien.media.master.webappapi.util.Value;

public class HashManager extends AbstractManager<HashTag> {
    private static HashManager m_aInstance = new HashManager();

    private HashManager() {
	super(Value.DB_COLLECTION_HASHTAGS);
    }

    public static HashManager getInstance() {
	return m_aInstance;
    }

    @Nonnull
    public Collection<HashTag> allFor(@Nullable final IHasId aEntry) {
	if (aEntry != null) {
	    m_aRWLock.readLock().lock();

	    final Collection<HashTag> aEntries = f_aEntries.values().stream().filter(aHashTag -> aHashTag.contains(aEntry))
		    .collect(Collectors.toCollection(ArrayList::new));

	    m_aRWLock.readLock().unlock();

	    return aEntries;
	}

	return new ArrayList<HashTag>();
    }

    @Nullable
    public HashTag get(final String sTagName) {
	final long nId = IdFactory.getBase36(sTagName);
	HashTag aFound = get(nId);

	// (in extremely rare cases) the id of a hash tag might not be unique
	if (aFound != null && !aFound.getTagName().equals(sTagName)) {
	    m_aRWLock.readLock().lock();

	    aFound = f_aEntries.values().stream().filter(HashTag -> HashTag.getTagName().equals(sTagName)).findFirst().orElse(null);

	    m_aRWLock.readLock().unlock();
	}

	return aFound;
    }

    public boolean save(@Nullable final IHasId aEntry, @Nonnull final String sText) {
	if (aEntry != null) {
	    // remove all old entries
	    if (removeFromAll(aEntry)) {
		if (StringUtils.isNotEmpty(sText)) {
		    final StringTokenizer aTokenizer = new StringTokenizer(sText);

		    // save all new hash tags
		    while (aTokenizer.hasMoreTokens()) {
			final String sToken = aTokenizer.nextToken();

			if (sToken.length() > 1 && sToken.startsWith(CommonValue.CHARACTER_AT)) {
			    HashTag aHashTag = get(sToken);
			    if (aHashTag == null)
				aHashTag = new HashTag(sToken);

			    aHashTag.add(aEntry);
			    save(aHashTag);
			}
		    }
		}

		return true;
	    }
	}

	return false;
    }

    public boolean removeFromAll(@Nullable final IHasId aEntry) {
	if (aEntry != null) {
	    all().stream().filter(aHashTag -> aHashTag.remove(aEntry)).forEach(aHashTag -> {
		if (aHashTag.getResourceIds().isEmpty())
		    delete(aHashTag);
		else
		    save(aHashTag);
	    });

	    return true;
	}

	return false;
    }
}