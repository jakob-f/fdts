package at.ac.tuwien.media.master.webappapi.model;

import java.io.Serializable;
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
    private Collection<Set> m_aSets;

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
    }

    public long getId() {
	return m_nId;
    }

    @Nullable
    public String getName() {
	return m_sName;
    }

    public void setName(@Nullable final String sName) {
	m_sName = sName;
    }

    @Nullable
    public String getDescription() {
	return m_sDescription;
    }

    public void setDescription(@Nullable final String sDescription) {
	m_sDescription = sDescription;
    }
}
