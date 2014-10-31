package at.ac.tuwien.media.master.webappapi.model;

import javax.annotation.Nullable;

public class User {
    private String m_sName;
    private String m_sPassword;
    private ERole m_aRole;

    public User() {
    }

    public User(@Nullable final String sName, @Nullable final String sPassword, @Nullable final ERole aRole) {
	m_sName = sName;
	m_sPassword = sPassword;
	m_aRole = aRole;
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

    public ERole getRole() {
	return m_aRole;
    }

    public void setRole(@Nullable final ERole aRole) {
	m_aRole = aRole;
    }
}
