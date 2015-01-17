package at.frohnwieser.mahut.commons;

import java.util.Properties;

import javax.annotation.Nonnull;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;

public class MailClient {
    private final static MailClient m_aInstance = new MailClient();
    private Properties m_aProperites;
    private String m_sUsername;
    private String m_sPassword;
    private boolean m_bIsReady;

    private MailClient() {
	m_bIsReady = false;
    }

    public static MailClient getInstance() {
	return m_aInstance;
    }

    public void setUp(@Nonnull final String sUsername, @Nonnull final String sPassword, @Nonnull final String sHost, @Nonnull final String sPort) {
	if (StringUtils.isNotEmpty(sUsername) && StringUtils.isNotEmpty(sPassword) && StringUtils.isNotEmpty(sHost) && StringUtils.isNotEmpty(sPort)) {
	    m_sUsername = sUsername;
	    m_sPassword = sPassword;

	    m_aProperites = new Properties();
	    m_aProperites.put("mail.smtp.auth", "true");
	    m_aProperites.put("mail.smtp.starttls.enable", "true");
	    m_aProperites.put("mail.smtp.host", sHost);
	    m_aProperites.put("mail.smtp.port", sPort);

	    m_bIsReady = true;
	}
    }

    private Message _createMessage() {
	if (!m_bIsReady)
	    throw new RuntimeException("mail client not yet set up");

	final Session aSession = Session.getInstance(m_aProperites, new Authenticator() {
	    @Override
	    protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(m_sUsername, m_sPassword);
	    }
	});

	return new MimeMessage(aSession);
    }

    public boolean sendMessage(@Nonnull final String sFrom, @Nonnull final String sTo, @Nonnull final String sSubject, @Nonnull final String sText) {
	try {
	    final Message aMessage = _createMessage();
	    aMessage.setFrom(new InternetAddress(sFrom));
	    aMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sTo));
	    aMessage.setSubject(sSubject);
	    aMessage.setText(sText);

	    Transport.send(aMessage);
	    return true;
	} catch (final Exception aException) {
	    aException.printStackTrace();
	}

	return false;
    }

    public boolean sendMessage(@Nonnull final String sTo, @Nonnull final String sSubject, @Nonnull final String sText) {
	return sendMessage(m_sUsername, sTo, sSubject, sText);
    }
}
