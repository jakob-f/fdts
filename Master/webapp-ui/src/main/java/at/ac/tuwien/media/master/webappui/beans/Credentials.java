package at.ac.tuwien.media.master.webappui.beans;

import java.io.Serializable;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.joda.time.DateTime;

import at.ac.tuwien.media.master.webappapi.model.ERole;
import at.ac.tuwien.media.master.webappapi.model.User;
import at.ac.tuwien.media.master.webappui.util.EPage;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.BEAN_CREDENTIALS)
public class Credentials implements Serializable {
    private boolean m_bIsLoggedIn;
    private DateTime m_aLoginTime;
    private String m_sUsername;
    private ERole m_aRole;
    private Locale m_aLocale;
    private EPage m_aLastPage;

    public void clear() {
	m_bIsLoggedIn = false;
	m_aLoginTime = null;
	m_sUsername = null;
	m_aRole = null;
	m_aLocale = null;
	m_aLastPage = null;
    }

    public Credentials() {
	clear();
    }

    public void login(@Nonnull final User aUser) {
	if (aUser == null)
	    throw new NullPointerException("user");

	m_bIsLoggedIn = true;
	m_aLoginTime = DateTime.now();
	m_sUsername = aUser.getName();
	m_aRole = aUser.getRole();
	m_aLocale = Locale.ENGLISH;
	m_aLastPage = EPage.START;
    }

    public boolean isLoggedIn() {
	return m_bIsLoggedIn;
    }

    @Nullable
    public DateTime getLoginTime() {
	return m_aLoginTime;
    }

    @Nullable
    public String getUsername() {
	return m_sUsername;
    }

    @Nullable
    public ERole getRole() {
	return m_aRole;
    }

    @Nullable
    public Locale getLocale() {
	return m_aLocale;
    }

    public void setLastPage(@Nullable final EPage aLastPage) {
	m_aLastPage = aLastPage;
    }

    @Nullable
    public EPage getLastPage() {
	return m_aLastPage;
    }
}
