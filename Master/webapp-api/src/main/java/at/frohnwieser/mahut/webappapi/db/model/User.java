package at.frohnwieser.mahut.webappapi.db.model;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.commons.EncryptionUtils;
import at.frohnwieser.mahut.commons.IHasId;
import at.frohnwieser.mahut.commons.IValidate;
import at.frohnwieser.mahut.commons.IdFactory;

@SuppressWarnings("serial")
public class User implements Serializable, IHasId, IValidate {
    private final long f_nId;
    private String m_sName;
    private byte[] m_aSalt;
    private byte[] m_aPassword;
    private String m_sEmail;
    private String m_sRole;

    public User() {
	f_nId = IdFactory.getInstance().getId();
    }

    public User(@Nullable final String sName, @Nullable final String sPassword, @Nullable final String sEmail, @Nonnull final ERole aRole) {
	this();

	m_sName = sName;
	setPassword(sPassword);
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

    // only for JSF
    @Deprecated
    public String getPassword() {
	return "";
    }

    public void setPassword(@Nullable final String sPassword) {
	if (StringUtils.isNotEmpty(sPassword)) {
	    m_aSalt = EncryptionUtils.generateSalt();
	    m_aPassword = EncryptionUtils.encrypt(sPassword, m_aSalt);
	}
    }

    @Nullable
    public String getEmail() {
	return m_sEmail;
    }

    public void setEmail(@Nullable final String sEmail) {
	m_sEmail = sEmail;
    }

    @Nullable
    public ERole getRole() {
	if (StringUtils.isNotEmpty(m_sRole))
	    return ERole.valueOf(m_sRole);
	else
	    return null;
    }

    public void setRole(@Nonnull final ERole aRole) {
	m_sRole = aRole.name();
    }

    @Nullable
    public boolean authenticate(@Nullable final String sPassword) {
	return EncryptionUtils.authenticate(sPassword, m_aPassword, m_aSalt);
    }

    @Override
    public boolean isValid() {
	return StringUtils.isNoneEmpty(m_sName) && m_aPassword != null && StringUtils.isNoneEmpty(m_sEmail) && getRole() != null;
    }

    public String getLink() {
	return "./view?u=" + m_sName; // TODO
    }
}
