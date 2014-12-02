package at.ac.tuwien.media.master.webapp.ws;

import javax.annotation.Nonnull;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.security.auth.login.FailedLoginException;

import at.ac.tuwien.media.master.webapp.ws.data.AssetData;
import at.ac.tuwien.media.master.webapp.ws.data.SetData;

@WebService
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL)
public interface IWSEndpoint {

    @WebMethod
    @WebResult(name = "success")
    public boolean uploadAsset(@WebParam(name = "nParentSetId") final long nParentSetId, @WebParam(name = "aAssetData") @Nonnull final AssetData aAssetData)
	    throws FailedLoginException;

    @WebMethod
    @WebResult(name = "success")
    public boolean createSet(@WebParam(name = "nParentSetId") final long nParentSetId, @WebParam(name = "aSetData") @Nonnull final SetData aSetData)
	    throws FailedLoginException;

    @WebMethod
    @WebResult(name = "SetData")
    public SetData[] getAllSets() throws FailedLoginException;
}
