package at.ac.tuwien.media.master.webappui.beans;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.collections4.CollectionUtils;

import at.ac.tuwien.media.master.webappapi.model.ERole;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.BEAN_CREDENTIALS)
public class Credentials implements Serializable {
    private String m_sUsername;
    private String m_sPassword;
    private Collection<ERole> m_aRoles;

    public Credentials() {
    }

    public boolean isAdmin() {
	return CollectionUtils.isNotEmpty(m_aRoles) && m_aRoles.contains(ERole.ADMIN);
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
    public Collection<ERole> getRoles() {
	return m_aRoles;
    }

    public void setRoles(@Nullable final Collection<ERole> aRoles) {
	m_aRoles = aRoles;
    }
}
