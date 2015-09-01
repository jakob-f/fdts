package at.frohnwieser.mahut.webappapi.db.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.commons.IdFactory;
import at.frohnwieser.mahut.commons.TimeStampFactory;

@SuppressWarnings("serial")
public abstract class AbstractResource implements IResource {
    private final String f_sId;
    private final long f_nCreationTimeStamp;
    private final String f_sOwnerId;
    private String m_sName;
    private String m_sHash;
    private long m_nModificationTimeStamp;
    private String m_sMetaContent;
    private long m_nViewCount;
    private String m_sState;

    protected AbstractResource(@Nonnull final String sId, @Nonnull final String sOwnerId, @Nonnull final String sName, @Nullable final String sMetaContent) {
	if (StringUtils.isEmpty(sOwnerId))
	    throw new NullPointerException("ownerid");

	final long aCreationTimeStampMillis = TimeStampFactory.nowMillis();
	f_sId = sId;
	f_nCreationTimeStamp = aCreationTimeStampMillis;
	f_sOwnerId = sOwnerId;
	setName(sName);
	resetHash();
	setModificationTimeStamp(aCreationTimeStampMillis);
	setMetaContent(sMetaContent);
	setViewCount(-1);
	setState(EState.PRIVATE);
    }

    protected AbstractResource(@Nonnull final String sOwnerId, @Nonnull final String sName, @Nullable final String sMetaContent) {
	this(IdFactory.getInstance().getStringId(), sOwnerId, sName, sMetaContent);
    }

    /*
     * (non-Javadoc)
     *
     * @see at.frohnwieser.mahut.webappapi.db.model.IResource#getId()
     */
    @Override
    public String getId() {
	return f_sId;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * at.frohnwieser.mahut.webappapi.db.model.IResource#getCreationTimeStamp()
     */
    @Override
    @Nonnull
    public long getCreationTimeStamp() {
	return f_nCreationTimeStamp;
    }

    /*
     * (non-Javadoc)
     *
     * @see at.frohnwieser.mahut.webappapi.db.model.IResource#
     * getCreationTimeStampFormatted()
     */
    @Override
    @Nonnull
    public String getCreationTimeStampFormatted() {
	return TimeStampFactory.format(f_nCreationTimeStamp);
    }

    /*
     * (non-Javadoc)
     *
     * @see at.frohnwieser.mahut.webappapi.db.model.IResource#getOwnerId()
     */
    @Override
    public String getOwnerId() {
	return f_sOwnerId;
    }

    /*
     * (non-Javadoc)
     *
     * @see at.frohnwieser.mahut.webappapi.db.model.IResource#getName()
     */
    @Override
    @Nonnull
    public String getName() {
	return m_sName;
    }

    /*
     * (non-Javadoc)
     *
     * @see at.frohnwieser.mahut.webappapi.db.model.te#setName(java.lang.String)
     */
    @Override
    public void setName(@Nonnull final String sName) {
	m_sName = sName;
    }

    /*
     * (non-Javadoc)
     *
     * @see at.frohnwieser.mahut.webappapi.db.model.IResource#getHash()
     */
    @Override
    @Nonnull
    public String getHash() {
	return m_sHash;
    }

    /*
     * (non-Javadoc)
     *
     * @see at.frohnwieser.mahut.webappapi.db.model.IResource#resetHash()
     */
    @Override
    public IResource resetHash() {
	m_sHash = IdFactory.getInstance().getHash();

	return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * at.frohnwieser.mahut.webappapi.db.model.IResource#getModificationTimeStamp
     * ()
     */
    @Override
    @Nonnull
    public long getModificationTimeStamp() {
	return m_nModificationTimeStamp;
    }

    /*
     * (non-Javadoc)
     *
     * @see at.frohnwieser.mahut.webappapi.db.model.IResource#
     * getModificationTimeStampFormatted()
     */
    @Override
    @Nonnull
    public String getModificationTimeStampFormatted() {
	return TimeStampFactory.format(m_nModificationTimeStamp);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * at.frohnwieser.mahut.webappapi.db.model.te#setModificationTimeStamp(long)
     */
    @Override
    public void setModificationTimeStamp(final long nModificationTimeStamp) {
	m_nModificationTimeStamp = nModificationTimeStamp;
    }

    /*
     * (non-Javadoc)
     *
     * @see at.frohnwieser.mahut.webappapi.db.model.IResource#getMetaContent()
     */
    @Override
    @Nullable
    public String getMetaContent() {
	return m_sMetaContent;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * at.frohnwieser.mahut.webappapi.db.model.IResource#getMetaContentFormatted
     * ()
     */
    @Override
    @Nonnull
    public String getMetaContentFormatted() {
	// TODO
	return m_sMetaContent;
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

    /*
     * (non-Javadoc)
     *
     * @see
     * at.frohnwieser.mahut.webappapi.db.model.te#setMetaContent(java.lang.String
     * )
     */
    @Override
    public void setMetaContent(@Nullable final String sMetaContent) {
	m_sMetaContent = sMetaContent;
    }

    /*
     * (non-Javadoc)
     *
     * @see at.frohnwieser.mahut.webappapi.db.model.IResource#getViewCount()
     */
    @Override
    public long getViewCount() {
	return m_nViewCount;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * at.frohnwieser.mahut.webappapi.db.model.IResource#decreaseViewCount()
     */
    @Override
    public long decreaseViewCount() {
	return m_nViewCount--;
    }

    /*
     * (non-Javadoc)
     *
     * @see at.frohnwieser.mahut.webappapi.db.model.te#setViewCount(long)
     */
    @Override
    public IResource setViewCount(final long nViewCount) {
	m_nViewCount = nViewCount;

	return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see at.frohnwieser.mahut.webappapi.db.model.IResource#getState()
     */
    @Override
    @Nonnull
    public EState getState() {
	return EState.valueOf(m_sState);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * at.frohnwieser.mahut.webappapi.db.model.te#setState(at.frohnwieser.mahut
     * .webappapi.db.model.EState)
     */
    @Override
    public IResource setState(@Nonnull final EState aState) {
	if (aState == null)
	    throw new NullPointerException("state");

	m_sState = aState.name();

	return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see at.frohnwieser.mahut.webappapi.db.model.IResource#isValid()
     */
    @Override
    public boolean isValid() {
	// XXX final fields must be set anyway
	return true;
    }

    @Override
    public int compareTo(final AbstractResource aOther) {
	return m_sName.compareTo(aOther.getName());
    }
}
