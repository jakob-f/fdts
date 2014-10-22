package at.ac.tuwien.media.master.webappui.beans;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.BEAN_CREDENTIALS)
public class Credentials implements Serializable {
    private String m_sUsername;
    private String m_sPassword;
    private String m_sRole;

    public Credentials() {
    }

    public boolean isAdmin() {
	return StringUtils.isNoneEmpty(m_sUsername) && m_sUsername.equals("admin");
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
    public String getRole() {
	return m_sRole;
    }

    public void setRole(@Nullable final String sRole) {
	this.m_sRole = sRole;
    }
}
