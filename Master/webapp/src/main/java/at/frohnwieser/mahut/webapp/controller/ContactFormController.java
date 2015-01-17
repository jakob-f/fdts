package at.frohnwieser.mahut.webapp.controller;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.commons.MailClient;
import at.frohnwieser.mahut.webapp.util.SessionUtils;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.config.Configuration;
import at.frohnwieser.mahut.webappapi.config.Configuration.EField;

@SuppressWarnings("serial")
@ViewScoped
@ManagedBean(name = Value.CONTROLLER_CONTACT)
public class ContactFormController implements Serializable {
    private String m_sName;
    private String m_sEmail;
    private String m_sMessage;

    public ContactFormController() {
	final Configuration aConfig = Configuration.getInstance();
	MailClient.getInstance().setUp(aConfig.getAsString(EField.MAIL_USERNAME), aConfig.getAsString(EField.MAIL_PASSWORD),
	        aConfig.getAsString(EField.MAIL_HOST), aConfig.getAsString(EField.MAIL_PORT));
    }

    public void send() {
	if (StringUtils.isNotEmpty(m_sName) && StringUtils.isNotEmpty(m_sEmail) && StringUtils.isNotEmpty(m_sMessage)) {
	    MailClient.getInstance().sendMessage(m_sEmail, Configuration.getInstance().getAsString(EField.MAIL_TO_ADDRESS),
		    "[Mahut] new message from " + m_sName + " (" + m_sEmail + ")", m_sMessage);

	    SessionUtils.getInstance().info("message successfully sent", "");
	}
    }

    @Nullable
    public String getName() {
	return m_sName;
    }

    public void setName(@Nullable final String sName) {
	m_sName = sName;
    }

    @Nullable
    public String getEmail() {
	return m_sEmail;
    }

    public void setEmail(@Nullable final String sEmail) {
	m_sEmail = sEmail;
    }

    @Nullable
    public String getMessage() {
	return m_sMessage;
    }

    public void setMessage(@Nullable final String sMessage) {
	m_sMessage = sMessage;
    }
}
