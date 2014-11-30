package at.ac.tuwien.media.master.webapp;

import javax.annotation.Nonnull;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.security.auth.login.FailedLoginException;
import javax.xml.bind.annotation.XmlSeeAlso;

import at.ac.tuwien.media.master.webapp.data.AssetData;
import at.ac.tuwien.media.master.webapp.data.SetData;

@WebService(name = "mahut")
@XmlSeeAlso({ AssetData.class, SetData.class })
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL)
public interface IWSEndpoint {
    @WebMethod
    @WebResult(name = "success")
    public boolean upload(@WebParam(name = "nSetId") final long nSetId, @WebParam(name = "aAssetData") @Nonnull final AssetData aAssetData)
	    throws FailedLoginException;

    @WebMethod
    @WebResult(name = "setDatas")
    public SetData[] getAllSets() throws FailedLoginException;
}
