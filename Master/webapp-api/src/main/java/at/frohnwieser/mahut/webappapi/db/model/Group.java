package at.frohnwieser.mahut.webappapi.db.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.commons.IHasId;
import at.frohnwieser.mahut.commons.IValidate;
import at.frohnwieser.mahut.commons.IdFactory;

@SuppressWarnings("serial")
public class Group implements Serializable, IHasId, IValidate {
    private final String f_sId;
    private String m_sName;
    private String m_sDescription;
    private final Collection<String> m_aUserIds;
    private final Map<String, ReadWrite> m_aPermissions;

    public Group() {
	f_sId = IdFactory.getInstance().getStringId();

	m_aPermissions = new HashMap<String, ReadWrite>();
	m_aUserIds = new HashSet<String>();
    }

    public Group(@Nullable final String sName, @Nullable final String sDescription) {
	this();

	m_sName = sName;
	m_sDescription = sDescription;
    }

    @Override
    public String getId() {
	return f_sId;
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

    public Group addOrRemove(@Nullable final User aUser) {
	if (aUser != null)
	    if (m_aUserIds.contains(aUser.getId()))
		m_aUserIds.remove(aUser.getId());
	    else
		m_aUserIds.add(aUser.getId());

	return this;
    }

    public boolean add(@Nullable final User aUser) {
	if (aUser != null)
	    m_aUserIds.add(aUser.getId());

	return false;
    }

    public boolean contains(@Nullable final User aUser) {
	if (aUser == null)
	    return false;

	return m_aUserIds.contains(aUser.getId());
    }

    public boolean remove(@Nullable final User aUser) {
	if (aUser != null)
	    return m_aUserIds.remove(aUser.getId());

	return false;
    }

    @Nullable
    public boolean contains(@Nullable final Set aSet) {
	if (aSet == null)
	    return false;

	return m_aPermissions.containsKey(aSet.getId());
    }

    @Nullable
    public ReadWrite getPermissionFor(@Nullable final Set aSet) {
	if (aSet == null)
	    return null;

	return m_aPermissions.get(aSet.getId());
    }

    public Collection<String> getReadSetIds() {
	return m_aPermissions.entrySet().stream().filter(aEntry -> aEntry.getValue().isRead()).map(Entry::getKey)
	        .collect(Collectors.toCollection(ArrayList::new));
    }

    public Collection<String> getWriteSetIds() {
	return m_aPermissions.entrySet().stream().filter(aEntry -> aEntry.getValue().isWrite()).map(Entry::getKey)
	        .collect(Collectors.toCollection(ArrayList::new));
    }

    public Group setPermission(@Nonnull final Set aSet, final boolean bIsRead, final boolean bIsWrite) {
	if (!bIsRead && !bIsWrite)
	    m_aPermissions.remove(aSet.getId());
	else
	    m_aPermissions.put(aSet.getId(), new ReadWrite(bIsWrite ? true : bIsRead, bIsWrite));

	return this;
    }

    public boolean remove(@Nullable final Set aSet) {
	if (aSet != null)
	    return m_aPermissions.remove(aSet.getId()) != null;

	return false;
    }

    @Override
    public boolean isValid() {
	// TODO length
	return StringUtils.isNoneEmpty(m_sName) && StringUtils.isNoneEmpty(m_sDescription);
    }
}
