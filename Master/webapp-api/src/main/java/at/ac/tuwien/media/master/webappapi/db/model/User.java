package at.ac.tuwien.media.master.webappapi.db.model;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.commons.IHasId;
import at.ac.tuwien.media.master.commons.IValidate;
import at.ac.tuwien.media.master.commons.IdFactory;

@SuppressWarnings("serial")
public class User implements Serializable, IHasId, IValidate {
    private final long f_nId;
    private String m_sName;
    private String m_sPassword;
    private String m_sEmail;
    private String m_sRole;

    public User() {
	f_nId = IdFactory.getInstance().getId();
    }

    public User(@Nullable final String sName, @Nullable final String sPassword, @Nullable final String sEmail, @Nullable final ERole aRole) {
	this();

	m_sName = sName;
	m_sPassword = sPassword;
	m_sEmail = sEmail;
	m_sRole = aRole.name();
    }

    @Override
    public long getId() {
	return f_nId;
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
	if (StringUtils.isNotEmpty(m_sRole))
	    return ERole.valueOf(m_sRole);
	else
	    return null;
    }

    public void setRole(@Nonnull final ERole aRole) {
	m_sRole = aRole.name();
    }

    @Override
    public boolean isValid() {
	return StringUtils.isNoneEmpty(m_sName) && StringUtils.isNoneEmpty(m_sPassword) && StringUtils.isNoneEmpty(m_sEmail) && getRole() != null;
    }
}
