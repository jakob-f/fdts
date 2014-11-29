package at.ac.tuwien.media.master.webappui.bean;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import at.ac.tuwien.media.master.webappapi.db.model.ERole;
import at.ac.tuwien.media.master.webappapi.db.model.User;
import at.ac.tuwien.media.master.webappapi.util.TimeStampFactory;
import at.ac.tuwien.media.master.webappui.page.EPage;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.BEAN_CREDENTIALS)
public class Credentials implements Serializable {
    private boolean m_bIsLoggedIn;
    private LocalDateTime m_aLoginDateTime;
    private long m_nUserId;
    private String m_sUsername;
    private ERole m_aRole;
    private Locale m_aLocale;
    private EPage m_aLastPage;

    public void clear() {
	m_bIsLoggedIn = false;
	m_aLoginDateTime = null;
	m_nUserId = -1;
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
	m_aLoginDateTime = TimeStampFactory.get();
	m_nUserId = aUser.getId();
	m_sUsername = aUser.getName();
	m_aRole = aUser.getRole();
	m_aLocale = Locale.ENGLISH;
	m_aLastPage = EPage.START;
    }

    public boolean isLoggedIn() {
	return m_bIsLoggedIn;
    }

    @Nullable
    public LocalDateTime getLoginDateTime() {
	return m_aLoginDateTime;
    }

    @Nullable
    public long getUserId() {
	return m_nUserId;
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
