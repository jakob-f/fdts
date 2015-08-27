package at.frohnwieser.mahut.webappapi.db.model;

import java.io.Serializable;
import java.net.InetAddress;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.frohnwieser.mahut.commons.IHasId;
import at.frohnwieser.mahut.commons.IValidate;
import at.frohnwieser.mahut.commons.IdFactory;
import at.frohnwieser.mahut.commons.TimeStampFactory;

@SuppressWarnings("serial")
public class Login implements Serializable, IHasId, IValidate {
    private final String f_sId;
    private final String f_sUserId;
    private final InetAddress f_aUserIp;
    private int m_nAttempts;
    private long m_nLastTimeStamp;

    public Login(final String sUserId, @Nonnull final InetAddress aUserIp) {
	f_sId = IdFactory.getInstance().getStringId();
	f_sUserId = sUserId;
	f_aUserIp = aUserIp;
	reset();
    }

    @Override
    public String getId() {
	return f_sId;
    }

    public String getUserId() {
	return f_sUserId;
    }

    @Nonnull
    public InetAddress getUserIp() {
	return f_aUserIp;
    }

    public int getAttempts() {
	return m_nAttempts;
    }

    public long getLastTimeStamp() {
	return m_nLastTimeStamp;
    }

    public void attempt() {
	m_nAttempts++;
	m_nLastTimeStamp = TimeStampFactory.nowMillis();
    }

    public void reset() {
	m_nAttempts = 0;
	m_nLastTimeStamp = 0;
    }

    public boolean contains(final String sUserId, @Nullable final InetAddress aUserIp) {
	return aUserIp != null ? f_sUserId == sUserId || f_aUserIp.equals(aUserIp) : false;
    }

    @Override
    public boolean isValid() {
	// not used
	return true;
    }

    @Override
    public String toString() {
	return "Login [f_sId=" + f_sId + ", f_sUserId=" + f_sUserId + ", f_aUserIp=" + f_aUserIp + ", m_nAttempts=" + m_nAttempts + ", m_nLastTimeStamp="
	        + m_nLastTimeStamp + "]";
    }

}
