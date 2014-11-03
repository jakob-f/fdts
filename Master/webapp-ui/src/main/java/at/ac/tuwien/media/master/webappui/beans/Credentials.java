package at.ac.tuwien.media.master.webappui.beans;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import at.ac.tuwien.media.master.webappapi.model.ERole;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.BEAN_CREDENTIALS)
public class Credentials implements Serializable {
    private String m_sUsername;
    private String m_sPassword;
    private ERole m_aRole;

    public Credentials() {
    }

    public boolean isAdmin() {
	return m_aRole != null && m_aRole.equals(ERole.ADMIN);
    }

    @Nullable
    public String getUsername() {
	return m_sUsername;
    }

    public void setUsername(@Nullable final String sUsername) {
	this.m_sUsername = sUsername;
    }

    @Nullable
    public String getPassword() {
	return m_sPassword;
    }

    public void setPassword(@Nullable final String sPassword) {
	this.m_sPassword = sPassword;
    }

    @Nullable
    public ERole getRole() {
	return m_aRole;
    }

    public void setRole(@Nullable final ERole aRole) {
	m_aRole = aRole;
    }
}
