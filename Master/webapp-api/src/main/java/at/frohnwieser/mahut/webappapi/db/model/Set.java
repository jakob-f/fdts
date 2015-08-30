package at.frohnwieser.mahut.webappapi.db.model;

import java.util.Collection;
import java.util.HashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("serial")
public class Set extends AbstractResource {
    private final Collection<String> m_aAssetIds;
    private final Collection<String> m_aChildSetIds;

    @Deprecated
    public Set(@Nonnull final String sId, @Nonnull final String sOwnerId, @Nonnull final String sName, @Nullable final String sMetaContent) {
	super(sId, sOwnerId, sName, sMetaContent);
	m_aAssetIds = new HashSet<String>();
	m_aChildSetIds = new HashSet<String>();
    }

    public Set(@Nonnull final String sOwnerId, @Nonnull final String sName, @Nullable final String sMetaContent) {
	super(sOwnerId, sName, sMetaContent);
	m_aAssetIds = new HashSet<String>();
	m_aChildSetIds = new HashSet<String>();
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

    @Override
    @Nonnull
    public EFileType getFileType() {
	return EFileType.SET;
    }

    @Nonnull
    public String getLink() {
	return "./view?s=" + getHash(); // TODO
    }

    @Nonnull
    public String getAssetsLink() {
	return "./assets?s=" + getHash(); // TODO
    }
}
