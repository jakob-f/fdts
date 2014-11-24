package at.ac.tuwien.media.master.webappapi.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nullable;

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
    private Collection<Permission> m_aPermissions;

    public Group(@Nullable final String sName) {
	m_nId = IdFactory.getInstance().getNextId();
	m_sName = sName;
	m_aPermissions = new ArrayList<Permission>();
    }

    public long getId() {
	return m_nId;
    }

    @Nullable
    public String getName() {
	return m_sName;
    }

    public Group setName(@Nullable final String sName) {
	m_sName = sName;

	return this;
    }

    public Collection<Permission> getPermissions() {
	return m_aPermissions;
    }

    public Group setPermissions(final Collection<Permission> aPermissions) {
	m_aPermissions = aPermissions;

	return this;
    }

    @Nullable
    public Permission getPermissionForSet(final Set aSet) {
	for (final Permission aPermission : m_aPermissions)
	    if (aPermission.getSet() == aSet)
		return aPermission;

	return null;
    }

    public void setPermission(final Set aSet, final boolean bIsRead, final boolean bIsWrite) {
	System.out.println(aSet.getName() + "  " + bIsRead + " " + bIsWrite);

	Permission aPermission = getPermissionForSet(aSet);

	if (aPermission == null) {
	    aPermission = new Permission(aSet);
	    m_aPermissions.add(aPermission);
	}

	if (bIsRead)
	    aPermission.setRead(true);
	else if (bIsWrite)
	    aPermission.setWrite(true);
	else
	    aPermission.setRead(false);
    }
}
