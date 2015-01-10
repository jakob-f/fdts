package at.frohnwieser.mahut.webapp.ws;

import javax.annotation.Nullable;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.security.auth.login.FailedLoginException;

import at.frohnwieser.mahut.webapp.ws.data.AssetData;
import at.frohnwieser.mahut.webapp.ws.data.SetData;

@WebService
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL)
public interface IWSEndpoint {

    @WebMethod
    @WebResult(name = "success")
    public boolean uploadAsset(@WebParam(name = "nParentSetId") final long nParentSetId, @WebParam(name = "aAssetData") @Nullable final AssetData aAssetData)
	    throws FailedLoginException;

    @WebMethod
    @WebResult(name = "success")
    public boolean createSet(@WebParam(name = "nParentSetId") final long nParentSetId, @WebParam(name = "aSetData") @Nullable final SetData aSetData)
	    throws FailedLoginException;

    @WebMethod
    @WebResult(name = "SetData")
    public SetData[] getAllSets() throws FailedLoginException;
}
