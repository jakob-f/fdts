package at.frohnwieser.mahut.webapp.handler;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import at.frohnwieser.mahut.webapp.page.EPage;
import at.frohnwieser.mahut.webapp.util.SessionUtils;

public class MahutExceptionHandler extends ExceptionHandlerWrapper {
    private final ExceptionHandler m_fExceptionHandler;

    MahutExceptionHandler(final ExceptionHandler aExceptionHandler) {
	m_fExceptionHandler = aExceptionHandler;
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
	    // TODO send email here
	    aIterator.remove();

	    SessionUtils.getInstance().redirect(EPage.ERROR.getName());
	}

	// continue with parent
	getWrapped().handle();
    }
}
