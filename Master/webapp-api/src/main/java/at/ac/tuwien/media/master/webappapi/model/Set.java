package at.ac.tuwien.media.master.webappapi.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.util.IdFactory;

@SuppressWarnings("serial")
public class Set implements Serializable {
    private final long m_nId;
    private String m_sName;
    private String m_sDescription;
    private Collection<Asset> m_aAssets;
    private Collection<Set> m_aChildren;
    private Collection<Group> m_aReadGroups;
    private Collection<Group> m_aReadWriteGroups;

    public Set() {
	m_nId = IdFactory.getInstance().getNextId();
    }

    public Set(@Nonnull final String sName, @Nonnull final String sDescription) {
	if (StringUtils.isEmpty(sName))
	    throw new NullPointerException("name");
	if (StringUtils.isEmpty(sName))
	    throw new NullPointerException("sDescription");

	m_nId = IdFactory.getInstance().getNextId();
	m_sName = sName;
	m_sDescription = sDescription;

	m_aReadGroups = new ArrayList<Group>();
    }

    public long getId() {
	return m_nId;
    }

    @Nullable
    public String getName() {
	return m_sName;
    }

    public Set setName(@Nullable final String sName) {
	m_sName = sName;

	return this;
    }

    @Nullable
    public String getDescription() {
	return m_sDescription;
    }

    public Set setDescription(@Nullable final String sDescription) {
	m_sDescription = sDescription;

	return this;
    }

    private String _getNames(final Collection<Group> aGroups) {
	final StringBuilder aSB = new StringBuilder();

	boolean bFirst = true;

	// FIXME
	for (final Object aKey : aGroups.toArray()) {
	    if (bFirst)
		bFirst = false;
	    else
		aSB.append(", ");

	    if (aKey instanceof Group)
		aSB.append(((Group) aKey).getName());
	    else
		System.out.println(aKey + "  " + aKey.getClass() + " " + getClass().getName());
	}

	return aSB.toString();
    }

    @Nonnull
    public Collection<Group> getReadGroups() {
	if (m_aReadGroups == null)
	    m_aReadGroups = new ArrayList<Group>();

	return m_aReadGroups;
    }

    @Nonnull
    public String getReadGroupNames() {
	return _getNames(getReadGroups());
    }

    public Set addReadGroup(@Nonnull final Group aReadGroup) {
	getReadGroups().add(aReadGroup);

	return this;
    }

    public Set setReadGroups(@Nonnull final Collection<Group> aReadGroups) {
	m_aReadGroups = aReadGroups;

	return this;
    }

    @Nonnull
    public Collection<Group> getReadWriteGroups() {
	if (m_aReadWriteGroups == null)
	    m_aReadWriteGroups = new ArrayList<Group>();

	return m_aReadWriteGroups;
    }

    @Nonnull
    public String getReadWriteGroupNames() {
	return _getNames(getReadWriteGroups());
    }

    public Set addReadWriteGroup(@Nonnull final Group aReadWriteGroup) {
	getReadWriteGroups().add(aReadWriteGroup);

	return this;
    }

    public Set setReadWriteGroups(@Nonnull final Collection<Group> aReadWriteGroups) {
	m_aReadWriteGroups = aReadWriteGroups;

	return this;
    }
}
