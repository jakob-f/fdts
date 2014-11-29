package at.ac.tuwien.media.master.webappapi.db.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.commons.IHasId;

public class HashTag {
    private final String f_sTagName;
    private final Collection<Long> m_aResourceIds;

    public HashTag(@Nonnull final String sTagName) {
	if (StringUtils.isEmpty(sTagName))
	    throw new NullPointerException("tag name");

	f_sTagName = sTagName;
	m_aResourceIds = new ArrayList<Long>();
    }

    public String getTagName() {
	return f_sTagName;
    }

    public Collection<Long> getResourceIds() {
	return m_aResourceIds;
    }

    public void addResource(@Nonnull final IHasId aResource) {
	if (aResource != null)
	    m_aResourceIds.add(aResource.getId());
    }

    public void removeResource(@Nonnull final IHasId aResource) {
	if (aResource != null)
	    m_aResourceIds.remove(aResource.getId());
    }
}
