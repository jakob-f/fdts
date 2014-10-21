package at.ac.tuwien.media.master.webappapi.model;

import javax.annotation.Nullable;

public class User {
    private String m_sName;
    private String m_sPassword;

    public User() {
    }

    public User(@Nullable final String sName, @Nullable final String sPassword) {
	m_sName = sName;
	m_sPassword = sPassword;
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
}
