package at.frohnwieser.mahut.webappapi.db.model;

import java.util.Collection;
import java.util.HashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.frohnwieser.mahut.commons.IdFactory;
import at.frohnwieser.mahut.commons.TimeStampFactory;

@SuppressWarnings("serial")
public class Set extends AbstractResource {
    private final Collection<String> m_aAssetIds;
    private final Collection<String> m_aChildSetIds;

    private Set(@Nonnull final String sId, final long nCreationTimeStamp, @Nonnull final String sOwnerId, @Nonnull final String sName,
	    @Nullable final String sMetaContent) {
	super(sId, nCreationTimeStamp, sOwnerId, sName, sMetaContent);
	m_aAssetIds = new HashSet<String>();
	m_aChildSetIds = new HashSet<String>();
    }

    public Set(@Nonnull final String sId, @Nonnull final String sOwnerId, @Nonnull final String sName, @Nullable final String sMetaContent) {
	this(sId, TimeStampFactory.nowMillis(), sOwnerId, sName, sMetaContent);
    }

    public Set(@Nonnull final String sOwnerId, @Nullable final String sName, @Nullable final String sMetaContent) {
	this(IdFactory.getInstance().getStringId(), sOwnerId, sName, sMetaContent);
    }

    // TODO empty values - delete?
    public Set(@Nonnull final String sOwnerId) {
	this(sOwnerId, "", "");
    }

    public boolean add(@Nullable final Asset aAsset) {
	if (aAsset != null)
	    return m_aAssetIds.add(aAsset.getId());

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
	    return m_aChildSetIds.add(aSet.getId());

	return false;
    }

    public Collection<String> getChildSetIds() {
	return m_aChildSetIds;
    }

    public boolean remove(@Nullable final Set aSet) {
	if (aSet != null)
	    return m_aChildSetIds.remove(aSet.getId());

	return false;
    }

    @Nonnull
    public String getLink() {
	return "./view?s=" + getHash(); // TODO
    }
}
