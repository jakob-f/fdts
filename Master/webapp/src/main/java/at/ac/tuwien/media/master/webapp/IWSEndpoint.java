package at.ac.tuwien.media.master.webapp;

import javax.annotation.Nonnull;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.security.auth.login.FailedLoginException;

import at.ac.tuwien.media.master.webappapi.db.model.Set;

@WebService
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL)
public interface IWSEndpoint {
    @WebMethod
    public boolean upload(@Nonnull final Set aSet) throws FailedLoginException;

    @WebMethod
    public String[] getAllSets() throws FailedLoginException;
}
