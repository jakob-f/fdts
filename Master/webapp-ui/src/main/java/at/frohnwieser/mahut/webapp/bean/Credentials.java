package at.frohnwieser.mahut.webapp.bean;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import at.frohnwieser.mahut.commons.TimeStampFactory;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.db.model.User;
import at.frohnwieser.mahut.webappui.page.EPage;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.BEAN_CREDENTIALS)
public class Credentials implements Serializable {
    private User m_aUser;
    // TODO move to user
    private Locale m_aLocale;
    private EPage m_aLastPage;
    private LocalDateTime m_aLoginDateTime;

    public void clear() {
	m_aUser = null;
	m_aLocale = null;
	m_aLastPage = null;
	m_aLoginDateTime = null;
    }

    public Credentials() {
	clear();
    }

    public void login(@Nonnull final User aUser) {
	if (aUser == null)
	    throw new NullPointerException("user");

	m_aUser = aUser;
	m_aLocale = Locale.ENGLISH;
	m_aLastPage = EPage.START;
	m_aLoginDateTime = TimeStampFactory.get();
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
    public LocalDateTime getLoginDateTime() {
	return m_aLoginDateTime;
    }
}
