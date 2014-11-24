package at.ac.tuwien.media.master.webappapi.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.util.IdFactory;

@SuppressWarnings("serial")
public class Group implements Serializable {
    public class Permission {
	private final Set m_aSet;
	private boolean m_bIsRead;
	private boolean m_bIsWrite;

	private void _setReadWrite(final boolean bIsRead, final boolean bIsWrite) {
	    m_bIsRead = bIsRead;
	    m_bIsWrite = bIsWrite;
	}

	public Permission(final Set aSet) {
	    m_aSet = aSet;
	    _setReadWrite(false, false);
	}

	public Set getSet() {
	    return m_aSet;
	}

	public boolean isRead() {
	    return m_bIsRead;
	}

	public Permission setRead(final boolean bIsRead) {
	    if (bIsRead)
		_setReadWrite(true, false);
	    else
		_setReadWrite(false, false);

	    return this;
	}

	public boolean isWrite() {
	    return m_bIsWrite;
	}

	public Permission setWrite(final boolean bIsWrite) {
	    if (bIsWrite)
		_setReadWrite(true, true);
	    else
		_setReadWrite(false, false);

	    return this;
	}
    }

    private final long m_nId;
    private String m_sName;
    private String m_sDescription;
    private Collection<User> m_aUsers;
    private Collection<Permission> m_aPermissions;

    private void _init() {
	m_aPermissions = new ArrayList<Permission>();
	m_aUsers = new ArrayList<User>();
    }

    public Group() {
	m_nId = IdFactory.getInstance().getNextId();
	_init();
    }

    public Group(@Nonnull final String sName, @Nonnull final String sDescription) {
	if (StringUtils.isEmpty(sName))
	    throw new NullPointerException("name");
	if (StringUtils.isEmpty(sName))
	    throw new NullPointerException("sDescription");

	m_nId = IdFactory.getInstance().getNextId();
	m_sName = sName;
	m_sDescription = sDescription;
	_init();
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

    public boolean containsUser(@Nonnull final User aUser) {
	return m_aUsers.contains(aUser);
    }

    public void addOrRemoveUser(@Nonnull final User aUser) {
	if (containsUser(aUser))
	    m_aUsers.remove(aUser);
	else
	    m_aUsers.add(aUser);
    }

    @Nullable
    public String getDescription() {
	return m_sDescription;
    }

    public void setDescription(@Nullable final String sDescription) {
	m_sDescription = sDescription;
    }

    @Nullable
    public Permission getPermissionForSet(@Nonnull final Set aSet) {
	for (final Permission aPermission : m_aPermissions)
	    if (aPermission.getSet() == aSet)
		return aPermission;

	return null;
    }

    public void setPermission(@Nonnull final Set aSet, final boolean bIsRead, final boolean bIsWrite) {
	Permission aPermission = getPermissionForSet(aSet);

	if (aPermission != null && !bIsRead && !bIsWrite)
	    m_aPermissions.remove(aPermission);
	else {
	    if (aPermission == null) {
		aPermission = new Permission(aSet);
		m_aPermissions.add(aPermission);
	    }

	    if (bIsWrite)
		aPermission.setWrite(true);
	    else if (bIsRead)
		aPermission.setRead(true);
	}
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
