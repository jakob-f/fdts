package at.ac.tuwien.media.master.webappapi.db.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.commons.IHasId;
import at.ac.tuwien.media.master.commons.IValidate;
import at.ac.tuwien.media.master.commons.IdFactory;

@SuppressWarnings("serial")
public class HashTag implements Serializable, IHasId, IValidate {
    private final long f_nId;
    private final String f_sTag;
    private final Collection<Long> m_aResourceIds;

    public HashTag(@Nonnull final String sTag) {
	if (StringUtils.isEmpty(sTag))
	    throw new NullPointerException("tag name");

	// TODO check name and verify that id does not already exist
	f_nId = IdFactory.getBase36(sTag);
	f_sTag = sTag;
	m_aResourceIds = new HashSet<Long>();
    }

    @Override
    public long getId() {
	return f_nId;
    }

    public String getTag() {
	return f_sTag;
    }

    public Collection<Long> getResourceIds() {
	return m_aResourceIds;
    }

    public boolean add(@Nonnull final IHasId aEntry) {
	if (aEntry != null)
	    return m_aResourceIds.add(aEntry.getId());

	return false;
    }

    public boolean contains(@Nonnull final IHasId aEntry) {
	if (aEntry != null)
	    return m_aResourceIds.add(aEntry.getId());

	return false;
    }

    public boolean remove(@Nonnull final IHasId aEntry) {
	if (aEntry != null)
	    return m_aResourceIds.remove(aEntry.getId());

	return false;
    }

    @Override
    public boolean isValid() {

	return false;
    }
}
