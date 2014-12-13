package at.ac.tuwien.media.master.webappapi.db.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.commons.IHasId;
import at.ac.tuwien.media.master.commons.IValidate;
import at.ac.tuwien.media.master.commons.IdFactory;

@SuppressWarnings("serial")
public class HashTag implements Serializable, IHasId, IValidate {
    private final long f_nId;
    private final String f_sTag;
    // TODO merge to one?
    private final Collection<Long> m_aAssetIds;
    private final Collection<Long> m_aSetIds;

    public HashTag(@Nonnull final String sTag) {
	if (StringUtils.isEmpty(sTag))
	    throw new NullPointerException("tag name");

	// TODO check name and verify that id does not already exist
	f_nId = IdFactory.getBase36(sTag);
	f_sTag = sTag;
	m_aAssetIds = new HashSet<Long>();
	m_aSetIds = new HashSet<Long>();
    }

    @Override
    public long getId() {
	return f_nId;
    }

    public String getTag() {
	return f_sTag;
    }

    public boolean add(@Nullable final Asset aAsset) {
	if (aAsset != null)
	    return m_aAssetIds.add(aAsset.getId());

	return false;
    }

    public boolean contains(@Nullable final Asset aAsset) {
	if (aAsset != null)
	    return m_aAssetIds.contains(aAsset.getId());

	return false;
    }

    public Collection<Long> getAssetIds() {
	return m_aAssetIds;
    }

    public boolean remove(@Nullable final Asset aAsset) {
	if (aAsset != null)
	    return m_aAssetIds.remove(aAsset.getId());

	return false;
    }

    public boolean add(@Nullable final Set aSet) {
	if (aSet != null)
	    return m_aSetIds.add(aSet.getId());

	return false;
    }

    public boolean contains(@Nullable final Set aSet) {
	if (aSet != null)
	    return m_aSetIds.contains(aSet.getId());

	return false;
    }

    public Collection<Long> getSetIds() {
	return m_aSetIds;
    }

    public boolean remove(@Nullable final Set aSet) {
	if (aSet != null)
	    return m_aSetIds.remove(aSet.getId());

	return false;
    }

    @Override
    public boolean isValid() {
	return true;
    }
}
