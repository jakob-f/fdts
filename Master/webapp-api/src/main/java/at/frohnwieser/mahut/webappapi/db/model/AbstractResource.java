package at.frohnwieser.mahut.webappapi.db.model;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.commons.IHasId;
import at.frohnwieser.mahut.commons.IValidate;
import at.frohnwieser.mahut.commons.IdFactory;
import at.frohnwieser.mahut.commons.JSONFormatter;
import at.frohnwieser.mahut.commons.TimeStampFactory;

@SuppressWarnings("serial")
public abstract class AbstractResource implements Serializable, IHasId, IValidate, Comparable<AbstractResource> {
    private final String f_sId;
    private final long f_nCreationTimeStamp;
    private final String f_sOwnerId;
    private String m_sName;
    private String m_sHash;
    private long m_nLastUpdatedTimeStamp;
    private String m_sMetaContent;
    private long m_nViewCount;
    private String m_sState;

    protected AbstractResource(@Nonnull final String sId, final long nCreationTimeStamp, @Nonnull final String sOwnerId, @Nonnull final String sName,
	    @Nullable final String sMetaContent) {
	if (StringUtils.isEmpty(sId))
	    throw new NullPointerException("id");
	if (StringUtils.isEmpty(sOwnerId))
	    throw new NullPointerException("ownerid");

	f_sId = sId;
	f_nCreationTimeStamp = nCreationTimeStamp;
	f_sOwnerId = sOwnerId;
	setName(sName);
	resetHash();
	setLastUpdatedTimeStamp(nCreationTimeStamp);
	setMetaContent(sMetaContent);
	setViewCount(-1);
	setState(EState.PRIVATE);
    }

    @Override
    public String getId() {
	return f_sId;
    }

    @Nonnull
    public long getCreationTimeStamp() {
	return f_nCreationTimeStamp;
    }

    @Nonnull
    public String getCreationTimeStampFormatted() {
	return TimeStampFactory.format(f_nCreationTimeStamp);
    }

    public String getOwnerId() {
	return f_sOwnerId;
    }

    @Nonnull
    public String getName() {
	return m_sName;
    }

    public void setName(@Nonnull final String sName) {
	if (StringUtils.isEmpty(sName))
	    throw new NullPointerException("name");

	m_sName = sName;
    }

    @Nonnull
    public String getHash() {
	return m_sHash;
    }

    public AbstractResource resetHash() {
	m_sHash = IdFactory.getInstance().getHash();

	return this;
    }

    @Nonnull
    public long getLastUpdatedTimeStamp() {
	return m_nLastUpdatedTimeStamp;
    }

    @Nonnull
    public String getLastUpdatedTimeStampFormatted() {
	return TimeStampFactory.format(m_nLastUpdatedTimeStamp);
    }

    public void setLastUpdatedTimeStamp(final long nLastUpdatedTimeStamp) {
	m_nLastUpdatedTimeStamp = nLastUpdatedTimeStamp;
    }

    @Nullable
    public String getMetaContent() {
	return m_sMetaContent;
    }

    @Nonnull
    public String getMetaContentFormatted() {
	return JSONFormatter.format(m_sMetaContent);
    }

    // TODO use js
    // @Nonnull
    // public String getMetaContentFormatted() {
    // String sFormatted = m_sMetaContent;
    //
    // for (final String sTag : TagParser.parseHashTags(m_sMetaContent))
    // sFormatted = sFormatted.replaceAll(CommonValue.CHARACTER_HASH + sTag,
    // "<a href=\"./view?q=" + sTag + "\">#" + sTag + "</a>");
    // for (final String sTag : TagParser.parseAtTags(m_sMetaContent))
    // sFormatted = sFormatted.replaceAll(CommonValue.CHARACTER_AT + sTag,
    // "<a href=\"./view?u=" + sTag + "\">@" + sTag + "</a>");
    //
    // return sFormatted;
    // }

    public void setMetaContent(@Nullable final String sMetaContent) {
	m_sMetaContent = sMetaContent;
    }

    public long getViewCount() {
	return m_nViewCount;
    }

    public long decreaseViewCount() {
	return m_nViewCount--;
    }

    public AbstractResource setViewCount(final long nViewCount) {
	m_nViewCount = nViewCount;

	return this;
    }

    @Nonnull
    public EState getState() {
	return EState.valueOf(m_sState);
    }

    public AbstractResource setState(@Nonnull final EState aState) {
	if (aState == null)
	    throw new NullPointerException("state");

	m_sState = aState.name();

	return this;
    }

    @Override
    public boolean isValid() {
	// XXX final fields must be set anyway
	return true;
    }

    @Override
    public int compareTo(final AbstractResource aOther) {
	return (aOther.getLastUpdatedTimeStamp() < m_nLastUpdatedTimeStamp) ? -1 : ((m_nLastUpdatedTimeStamp == aOther.getLastUpdatedTimeStamp()) ? 0 : 1);
    }
}
