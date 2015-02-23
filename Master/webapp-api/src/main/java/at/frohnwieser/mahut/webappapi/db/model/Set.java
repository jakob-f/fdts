package at.frohnwieser.mahut.webappapi.db.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.commons.CommonValue;
import at.frohnwieser.mahut.commons.IHasId;
import at.frohnwieser.mahut.commons.IValidate;
import at.frohnwieser.mahut.commons.IdFactory;
import at.frohnwieser.mahut.commons.TimeStampFactory;
import at.frohnwieser.mahut.webappapi.util.TagParser;

@SuppressWarnings("serial")
public class Set implements Serializable, IHasId, IValidate {
    private final long f_nId;
    private final long f_nTimeStamp;
    private String m_sName;
    private String m_sHash;
    private String m_sMetaContent;
    private long m_nOwnerId;
    private String m_sState;
    // TODO merge to one?
    private final Collection<Long> m_aAssetIds;
    private final Collection<Long> m_aChildSetIds;

    private Set(final long nId, final long nTimeStamp, @Nullable final String sName, @Nullable final String sMetaContent, final long nOwnerId) {
	f_nId = nId;
	f_nTimeStamp = nTimeStamp;
	m_sName = sName;
	resetHash();
	m_sMetaContent = sMetaContent;
	m_sState = EState.PRIVATE.name();
	m_nOwnerId = nOwnerId;
	m_aAssetIds = new HashSet<Long>();
	m_aChildSetIds = new HashSet<Long>();
    }

    public Set(final long nId, @Nullable final String sName, @Nullable final String sMetaContent, final long nOwnerId) {
	this(nId, TimeStampFactory.nowMillis(), sName, sMetaContent, nOwnerId);
    }

    public Set(@Nullable final String sName, @Nullable final String sMetaContent, final long nOwnerId) {
	this(IdFactory.getInstance().getId(), sName, sMetaContent, nOwnerId);
    }

    public Set(final long nOwnerId) {
	this("", "", nOwnerId);
    }

    @Override
    public long getId() {
	return f_nId;
    }

    public long getTimeStamp() {
	return f_nTimeStamp;
    }

    @Nonnull
    public String getTimeStampFormatted() {
	return TimeStampFactory.format(getTimeStamp());
    }

    @Nullable
    public String getName() {
	return m_sName;
    }

    public void setName(@Nullable final String sName) {
	m_sName = sName;
    }

    @Nonnull
    public String getHash() {
	return m_sHash;
    }

    public Set resetHash() {
	m_sHash = IdFactory.getInstance().getHash();

	return this;
    }

    @Nullable
    public String getMetaContent() {
	return m_sMetaContent;
    }

    public void setMetaContent(@Nullable final String sMetaContent) {
	m_sMetaContent = sMetaContent;
    }

    public long getOwnerId() {
	return m_nOwnerId;
    }

    public void setOwnerId(final long nOwnerId) {
	m_nOwnerId = nOwnerId;
    }

    @Nullable
    public EState getState() {
	if (StringUtils.isNotEmpty(m_sState))
	    return EState.valueOf(m_sState);
	else
	    return null;
    }

    public Set setState(@Nonnull final EState aState) {
	m_sState = aState == EState.HOME_PAGE ? EState.PUBLISHED.name() : aState.name();

	return this;
    }

    public boolean add(@Nullable final Asset aAsset) {
	if (aAsset != null)
	    return m_aAssetIds.add(aAsset.getId());

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

    @Nonnull
    public String getLink() {
	return "./view?s=" + m_sHash; // TODO
    }

    // TODO use js
    @Nonnull
    public String getMetaContentFormatted() {
	String sFormatted = getMetaContent();

	for (final String sTag : TagParser.parseHashTags(m_sMetaContent))
	    sFormatted = sFormatted.replaceAll(CommonValue.CHARACTER_HASH + sTag, "<a href=\"./view?q=" + sTag + "\">#" + sTag + "</a>");
	for (final String sTag : TagParser.parseAtTags(m_sMetaContent))
	    sFormatted = sFormatted.replaceAll(CommonValue.CHARACTER_AT + sTag, "<a href=\"./view?u=" + sTag + "\">@" + sTag + "</a>");

	return sFormatted;
    }
}
