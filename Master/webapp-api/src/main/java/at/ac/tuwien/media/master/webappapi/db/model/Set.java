package at.ac.tuwien.media.master.webappapi.db.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.commons.IHasId;
import at.ac.tuwien.media.master.commons.IHasMetaContent;
import at.ac.tuwien.media.master.commons.IHasTimeStamp;
import at.ac.tuwien.media.master.webappapi.util.IdFactory;
import at.ac.tuwien.media.master.webappapi.util.TimeStampFactory;

@SuppressWarnings("serial")
public class Set implements Serializable, IHasId, IHasTimeStamp, IHasMetaContent {
    private final long f_nId;
    private final String f_sTimeStamp;
    private String m_sName;
    private String m_sMetaContent;
    private Collection<Long> m_aAssetIds;
    private Collection<Long> m_aChildSetIds;

    public Set() {
	f_nId = IdFactory.getInstance().getId();
	f_sTimeStamp = TimeStampFactory.getAsString();
    }

    public Set(@Nonnull final String sName, @Nonnull final String sMetaContent) {
	if (StringUtils.isEmpty(sName))
	    throw new NullPointerException("name");
	if (StringUtils.isEmpty(sName))
	    throw new NullPointerException("sDescription");

	f_nId = IdFactory.getInstance().getId();
	f_sTimeStamp = TimeStampFactory.getAsString();
	m_sName = sName;
	m_sMetaContent = sMetaContent;
	m_aAssetIds = new ArrayList<Long>();
	m_aChildSetIds = new ArrayList<Long>();
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

    public Collection<Long> getAssetsIds() {
	return m_aAssetIds;
    }

    public void addAsset(@Nullable final Asset aAsset) {
	if (aAsset != null)
	    m_aAssetIds.add(aAsset.getId());
    }

    public Collection<Long> getChildSetIds() {
	return m_aChildSetIds;
    }

    public void addChildSet(@Nullable final Set aSet) {
	if (aSet != null)
	    m_aChildSetIds.add(aSet.getId());
    }

    // XXX
    private boolean m_bMarkedForDeletion;

    public Set setMarkedForDeletion(final boolean bMarkedForDeletion) {
	m_bMarkedForDeletion = bMarkedForDeletion;

	return this;
    }

    public boolean isMarkedForDeletion() {
	return m_bMarkedForDeletion;
    }
}
