package at.ac.tuwien.media.master.webappapi.db.model;

import java.io.Serializable;
import java.time.Instant;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("serial")
public class User implements Serializable {
    private final long m_nId;
    private String m_sName;
    private String m_sPassword;
    private String m_sEmail;
    private ERole m_aRole;

    public User() {
	m_nId = Instant.now().toEpochMilli();
    }

    public User(@Nullable final String sName, @Nullable final String sPassword, @Nullable final String sEmail, @Nullable final ERole aRole) {
	m_nId = Instant.now().toEpochMilli();
	m_sName = sName;
	m_sPassword = sPassword;
	m_sEmail = sEmail;
	m_aRole = aRole;
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
    public ERole getRole() {
	return m_aRole;
    }

    public void setRole(@Nonnull final ERole aRole) {
	m_aRole = aRole;
    }

    // XXX
    private boolean m_bMarkedForDeletion;

    public void setMarkedForDeletion(final boolean bMarkedForDeletion) {
	m_bMarkedForDeletion = bMarkedForDeletion;
    }

    public boolean isMarkedForDeletion() {
	return m_bMarkedForDeletion;
    }
}
