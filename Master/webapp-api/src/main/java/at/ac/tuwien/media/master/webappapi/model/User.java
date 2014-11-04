package at.ac.tuwien.media.master.webappapi.model;

import java.util.Collection;
import java.util.HashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class User {
    private long m_nId;
    private String m_sName;
    private String m_sPassword;
    private String m_sEmail;
    private ERole m_aRole;
    private Collection<Group> m_aGroups;

    public User() {
    }

    public User(@Nullable final String sName, @Nullable final String sPassword, @Nullable final String sEmail, @Nullable final ERole aRole,
	    @Nullable final Collection<Group> aGroups) {
	m_nId = IdFactory.getInstance().getNextId();
	m_sName = sName;
	m_sPassword = sPassword;
	m_sEmail = sEmail;
	m_aRole = aRole;
	m_aGroups = aGroups;
    }

    public long getId() {
	return m_nId;
    }

    @Nullable
    public String getName() {
	return m_sName;
    }

    public User setName(@Nullable final String sName) {
	m_sName = sName;

	return this;
    }

    @Nullable
    public String getPassword() {
	return m_sPassword;
    }

    public User setPassword(@Nullable final String sPassword) {
	m_sPassword = sPassword;

	return this;
    }

    @Nullable
    public String getEmail() {
	return m_sEmail;
    }

    public User setEmail(@Nullable final String sEmail) {
	m_sEmail = sEmail;

	return this;
    }

    @Nonnull
    public ERole getRole() {
	return m_aRole;
    }

    public User setRole(@Nonnull final ERole aRole) {
	m_aRole = aRole;

	return this;
    }

    @Nonnull
    public Collection<Group> getGroups() {
	if (m_aGroups == null)
	    m_aGroups = new HashSet<Group>();

	return m_aGroups;
    }

    public User addGroup(@Nonnull final Group aGroup) {
	getGroups().add(aGroup);

	return this;
    }
}
