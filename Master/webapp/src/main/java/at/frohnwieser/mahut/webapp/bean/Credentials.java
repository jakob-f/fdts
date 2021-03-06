package at.frohnwieser.mahut.webapp.bean;

import java.io.Serializable;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import at.frohnwieser.mahut.commons.TimeStampFactory;
import at.frohnwieser.mahut.webapp.page.EPage;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.db.model.User;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.BEAN_CREDENTIALS)
public class Credentials implements Serializable {
    private User m_aUser;
    // TODO move to user
    private Locale m_aLocale;
    private EPage m_aLastPage;
    private long m_nLoginMillis;

    public void clear() {
	m_aUser = null;
	m_aLocale = null;
	m_aLastPage = null;
	m_nLoginMillis = -1L;
    }

    public Credentials() {
	clear();
    }

    public void login(@Nonnull final User aUser) {
	if (aUser == null)
	    throw new NullPointerException("user");

	m_aUser = aUser;
	m_aLocale = Locale.ENGLISH;
	m_aLastPage = EPage.ASSETS;
	m_nLoginMillis = TimeStampFactory.nowMillis();
    }

    public boolean isLoggedIn() {
	return m_aUser != null;
    }

    public User getUser() {
	return m_aUser;
    }

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

    @Nullable
    public long getLoginMillis() {
	return m_nLoginMillis;
    }
}
