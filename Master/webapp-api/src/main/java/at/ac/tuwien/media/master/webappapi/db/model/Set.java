package at.ac.tuwien.media.master.webappapi.db.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.commons.CommonValue;
import at.ac.tuwien.media.master.commons.IHasId;
import at.ac.tuwien.media.master.commons.IHasMetaContent;
import at.ac.tuwien.media.master.commons.IHasTimeStamp;
import at.ac.tuwien.media.master.commons.IValidate;
import at.ac.tuwien.media.master.commons.IdFactory;
import at.ac.tuwien.media.master.commons.TimeStampFactory;

@SuppressWarnings("serial")
public class Set implements Serializable, IHasId, IValidate, IHasTimeStamp, IHasMetaContent {
    private final long f_nId;
    private final String f_sTimeStamp;
    private String m_sName;
    private String m_sMetaContent;
    private boolean m_bPublic;
    private boolean m_bPublish;
    // TODO merge to one?
    private final Collection<Long> m_aAssetIds;
    private final Collection<Long> m_aChildSetIds;

    private Set(final long nId, @Nonnull final String sTimeStamp, @Nullable final String sName, @Nullable final String sMetaContent) {
	f_nId = nId;
	f_sTimeStamp = sTimeStamp;
	m_sName = sName;
	m_sMetaContent = sMetaContent;
	m_bPublic = false;
	m_bPublish = false;
	m_aAssetIds = new ArrayList<Long>();
	m_aChildSetIds = new ArrayList<Long>();
    }

    public Set(final long nId, @Nullable final String sName, @Nullable final String sMetaContent) {
	this(nId, TimeStampFactory.getAsString(), sName, sMetaContent);
    }

    public Set(@Nullable final String sName, @Nullable final String sMetaContent) {
	this(IdFactory.getInstance().getId(), sName, sMetaContent);
    }

    public Set() {
	this("", "");
    }

    @Override
    public long getId() {
	return f_nId;
    }

    @Override
    @Nonnull
    public String getTimeStamp() {
	return f_sTimeStamp;
    }

    @Nullable
    public String getName() {
	return m_sName;
    }

    public void setName(@Nullable final String sName) {
	m_sName = sName;
    }

    @Override
    @Nullable
    public String getMetaContent() {
	return m_sMetaContent;
    }

    @Override
    public void setMetaContent(@Nullable final String sMetaContent) {
	m_sMetaContent = sMetaContent;
    }

    public Set setPublic(final boolean bPublic) {
	m_bPublic = bPublic;

	return this;
    }

    public boolean isPublic() {
	return m_bPublic;
    }

    public Set setPublish(final boolean bPublish) {
	m_bPublish = bPublish;

	return this;
    }

    public boolean isPublish() {
	return m_bPublish;
    }

    public boolean add(@Nullable final Asset aAsset) {
	if (aAsset != null)
	    return m_aAssetIds.add(aAsset.getId());

	return false;
    }

    public Collection<Long> getAssetsIds() {
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

    public Collection<Long> getChildSetIds() {
	return m_aChildSetIds;
    }

    public boolean remove(@Nullable final Set aSet) {
	if (aSet != null)
	    return m_aChildSetIds.remove(aSet.getId());

	return false;
    }

    @Override
    public boolean isValid() {
	return StringUtils.isNoneEmpty(m_sName) && m_sName.length() <= CommonValue.MAX_LENGTH_NAME
	        && (m_sMetaContent == null || m_sMetaContent.length() <= CommonValue.MAX_LENGTH_METACONTENT);
    }
}
