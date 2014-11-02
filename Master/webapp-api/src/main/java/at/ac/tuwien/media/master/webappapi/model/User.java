package at.ac.tuwien.media.master.webappapi.model;

import java.util.Collection;
import java.util.HashSet;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class User {
    private long m_nId;
    private String m_sName;
    private String m_sPassword;
    private String m_sEmail;
    private Collection<ERole> m_aRoles;
    private Collection<Group> m_aGroups;

    public User() {
    }

    public User(@Nonnegative final long nId, @Nullable final String sName, @Nullable final String sPassword, @Nullable final String sEmail,
	    @Nullable final Collection<ERole> aRoles, @Nullable final Collection<Group> aGroups) {
	m_nId = nId;
	m_sName = sName;
	m_sPassword = sPassword;
	m_sEmail = sEmail;
	m_aRoles = aRoles;
	m_aGroups = aGroups;
    }

    public long getId() {
	return m_nId;
    }

    public void setId(@Nonnegative final long nId) {
	m_nId = nId;
    }

    @Nullable
    public String getName() {
	return m_sName;
    }

    public void setName(@Nullable final String sName) {
	m_sName = sName;
    }

    @Nullable
    public String getPassword() {
	return m_sPassword;
    }

    public void setPassword(@Nullable final String sPassword) {
	m_sPassword = sPassword;
    }

    @Nullable
    public String getEmail() {
	return m_sEmail;
    }

    public void setEmail(@Nullable final String sEmail) {
	m_sEmail = sEmail;
    }

    @Nonnull
    public Collection<ERole> getRoles() {
	if (m_aRoles == null)
	    m_aRoles = new HashSet<ERole>();

	return m_aRoles;
    }

    public void addRole(@Nonnull final ERole aRole) {
	getRoles().add(aRole);
    }

    @Nonnull
    public Collection<Group> getGroups() {
	if (m_aGroups == null)
	    m_aGroups = new HashSet<Group>();

	return m_aGroups;
    }

    public void addGroup(@Nonnull final Group aGroup) {
	getGroups().add(aGroup);
    }
}
