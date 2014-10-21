package at.ac.tuwien.media.master.webapp;

import javax.annotation.Nonnull;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.security.auth.login.FailedLoginException;

import at.ac.tuwien.media.master.webappapi.model.ProjectData;

@WebService
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL)
public interface IWSEndpoint {
    @WebMethod
    public boolean upload(@Nonnull final ProjectData aData) throws FailedLoginException;

    @WebMethod
    public String[] getProjects() throws FailedLoginException;
}
