package at.ac.tuwien.media.master.webappapi.model;

import javax.annotation.Nullable;

public class Project {
    private String m_sName;
    private String m_sUsers;

    public Project() {
    }

    public Project(@Nullable final String sName, @Nullable final String sUsers) {
	m_sName = sName;
	m_sUsers = sUsers;
    }

    @Nullable
    public String getName() {
	return m_sName;
    }

    public void setName(@Nullable final String sName) {
	m_sName = sName;
    }

    @Nullable
    public String getUsers() {
	return m_sUsers;
    }

    public void setUsers(@Nullable final String users) {
	m_sUsers = users;
    }
}
