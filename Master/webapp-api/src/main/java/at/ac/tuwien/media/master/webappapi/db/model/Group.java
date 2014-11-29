package at.ac.tuwien.media.master.webappapi.db.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.commons.IHasId;
import at.ac.tuwien.media.master.webappapi.util.IdFactory;

@SuppressWarnings("serial")
public class Group implements Serializable, IHasId {
    private final long f_nId;
    private String m_sName;
    private String m_sDescription;
    private Collection<Long> m_aUserIds;
    private Map<Long, ReadWrite> m_aPermissions;

    private void _init() {
	m_aPermissions = new HashMap<Long, ReadWrite>();
	m_aUserIds = new ArrayList<Long>();
    }

    public Group() {
	f_nId = IdFactory.getInstance().getId();
	_init();
    }

    public Group(@Nonnull final String sName, @Nonnull final String sDescription) {
	if (StringUtils.isEmpty(sName))
	    throw new NullPointerException("name");
	if (StringUtils.isEmpty(sName))
	    throw new NullPointerException("sDescription");

	f_nId = IdFactory.getInstance().getId();
	m_sName = sName;
	m_sDescription = sDescription;
	_init();
    }

    @Override
    public long getId() {
	return f_nId;
    }

    @Nullable
    public String getName() {
	return m_sName;
    }

    public void setName(@Nullable final String sName) {
	m_sName = sName;
    }

    public boolean containsUser(@Nonnull final User aUser) {
	return m_aUserIds.contains(aUser.getId());
    }

    // TODO besser?
    public void addOrRemoveUser(@Nonnull final User aUser) {
	if (containsUser(aUser))
	    m_aUserIds.remove(aUser.getId());
	else
	    m_aUserIds.add(aUser.getId());
    }

    @Nullable
    public String getDescription() {
	return m_sDescription;
    }

    public void setDescription(@Nullable final String sDescription) {
	m_sDescription = sDescription;
    }

    @Nullable
    public ReadWrite getPermissionForSet(@Nonnull final Set aSet) {
	return m_aPermissions.get(aSet.getId());
    }

    public void setPermission(@Nonnull final Set aSet, final boolean bIsRead, final boolean bIsWrite) {
	if (!bIsRead && !bIsWrite)
	    m_aPermissions.remove(aSet.getId());
	else
	    m_aPermissions.put(aSet.getId(), new ReadWrite(bIsWrite ? true : bIsRead, bIsWrite));
    }

    // XXX
    private boolean m_bMarkedForDeletion;

    public Group setMarkedForDeletion(final boolean bMarkedForDeletion) {
	m_bMarkedForDeletion = bMarkedForDeletion;

	return this;
    }

    public boolean isMarkedForDeletion() {
	return m_bMarkedForDeletion;
    }
}
