package at.frohnwieser.mahut.webapp.handler;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.apache.commons.lang3.exception.ExceptionUtils;

import at.frohnwieser.mahut.commons.MailClient;
import at.frohnwieser.mahut.commons.TimeStampFactory;
import at.frohnwieser.mahut.webapp.page.EPage;
import at.frohnwieser.mahut.webapp.util.SessionUtils;
import at.frohnwieser.mahut.webappapi.config.Configuration;
import at.frohnwieser.mahut.webappapi.config.Configuration.EField;
import at.frohnwieser.mahut.webappapi.db.model.User;

public class MahutExceptionHandler extends ExceptionHandlerWrapper {
    private final ExceptionHandler m_fExceptionHandler;

    public MahutExceptionHandler(final ExceptionHandler aExceptionHandler) {
	m_fExceptionHandler = aExceptionHandler;

	final Configuration aConfig = Configuration.getInstance();
	MailClient.getInstance().setUp(aConfig.getAsString(EField.MAIL_USERNAME), aConfig.getAsString(EField.MAIL_PASSWORD),
	        aConfig.getAsString(EField.MAIL_HOST), aConfig.getAsString(EField.MAIL_PORT));
    }

    @Override
    public ExceptionHandler getWrapped() {
	return m_fExceptionHandler;
    }

    @Override
    public void handle() throws FacesException {
	final Iterator<ExceptionQueuedEvent> aIterator = getUnhandledExceptionQueuedEvents().iterator();
	while (aIterator.hasNext()) {
	    final Throwable aThrowable = ((ExceptionQueuedEventContext) aIterator.next().getSource()).getException();
	    // remove exception
	    aIterator.remove();

	    if (aThrowable instanceof ViewExpiredException) {
		SessionUtils.getInstance().redirect(EPage.HOME.getName());
		SessionUtils.getInstance().info("please login again", "");
	    } else {
		// if test print stack trace
		if (Configuration.getInstance().getAsBoolean(EField.TEST))
		    aThrowable.printStackTrace();
		// send mail
		else {
		    final User aUser = SessionUtils.getInstance().getLoggedInUser();
		    final StringBuilder aSB = new StringBuilder();
		    aSB.append("Time:\t" + TimeStampFactory.nowFormatted() + "\n");
		    aSB.append("User:\t" + aUser != null ? aUser.getName() : "not logged in");
		    aSB.append("\n\n");
		    aSB.append(ExceptionUtils.getStackTrace(aThrowable));

		    MailClient.getInstance().sendMessage(Configuration.getInstance().getAsString(EField.MAIL_TO_ADDRESS), "[Mahut] " + aThrowable.getClass(),
			    aSB.toString());
		}

		// redirect user to error site
		SessionUtils.getInstance().redirect(EPage.ERROR.getName());
	    }
	}

	// continue with parent
	getWrapped().handle();
    }
}
