package at.frohnwieser.mahut.webappapi.db.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.commons.IHasId;
import at.frohnwieser.mahut.commons.IValidate;

@SuppressWarnings("serial")
public class HashTag implements Serializable, IHasId, IValidate {
    private final String f_sId;
    // TODO merge to one?
    private final Collection<String> m_aAssetIds;
    private final Collection<String> m_aSetIds;

    public HashTag(@Nonnull final String sTag) {
	if (StringUtils.isEmpty(sTag))
	    throw new NullPointerException("tag name");

	f_sId = sTag;
	m_aAssetIds = new HashSet<String>();
	m_aSetIds = new HashSet<String>();
    }

    @Override
    public String getId() {
	return f_sId;
    }

    public String getTag() {
	return f_sId;
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

    public Collection<String> getAssetIds() {
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

    public Collection<String> getSetIds() {
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

    public String getLink() {
	return "./view?q=" + f_sId; // TODO
    }
}
