package at.ac.tuwien.media.master.webapp.ws;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.security.auth.login.FailedLoginException;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.MTOM;

import at.ac.tuwien.media.master.webapp.ws.data.AssetData;
import at.ac.tuwien.media.master.webapp.ws.data.SetData;
import at.ac.tuwien.media.master.webappapi.db.manager.impl.AssetManager;
import at.ac.tuwien.media.master.webappapi.db.manager.impl.SetManager;
import at.ac.tuwien.media.master.webappapi.db.manager.impl.UserManager;
import at.ac.tuwien.media.master.webappapi.db.model.Asset;
import at.ac.tuwien.media.master.webappapi.db.model.Set;
import at.ac.tuwien.media.master.webappapi.db.model.User;
import at.ac.tuwien.media.master.webappapi.fs.manager.FSManager;

@MTOM
@WebService(endpointInterface = "at.ac.tuwien.media.master.webapp.ws.IWSEndpoint")
public class WSEndpointImpl implements IWSEndpoint {
    String HEADER_USERNAME = "username";
    String HEADER_PASSWORD = "password";

    @Resource
    WebServiceContext aWSContext;

    @Nonnull
    private User _authenticate() throws FailedLoginException {
	final Map<?, ?> aHeaders = (Map<?, ?>) aWSContext.getMessageContext().get(MessageContext.HTTP_REQUEST_HEADERS);
	final List<?> aUsers = (List<?>) aHeaders.get(HEADER_USERNAME);
	final List<?> aPasswords = (List<?>) aHeaders.get(HEADER_PASSWORD);

	if (aUsers != null && aUsers.size() == 1 && aPasswords != null && aPasswords.size() == 1) {
	    final String sUsername = aUsers.get(0).toString();
	    final String sPassword = aPasswords.get(0).toString();

	    final User aUser = UserManager.getInstance().get(sUsername, sPassword);
	    if (aUser != null)
		return aUser;
	}

	throw new FailedLoginException("wrong username or password");
    }

    @Override
    public boolean uploadAsset(final long nParentSetId, @Nullable final AssetData aAssetData) throws FailedLoginException {
	if (aAssetData != null) {
	    _authenticate();

	    System.out.println("UPLOAD " + aAssetData.getId() + " " + aAssetData.getName() + "\t" + aAssetData.getMetaContent() + "\t"
		    + aAssetData.isMetaContent() + "\tfor " + nParentSetId);

	    final Set aParentSet = SetManager.getInstance().get(nParentSetId);
	    if (aParentSet != null) {
		// TODO better?
		final File aAssetFile = FSManager.save(aParentSet, aAssetData.getName(), aAssetData.getAssetData(), aAssetData.isMetaContent());

		if (aAssetFile != null) {
		    final Asset aAsset = new Asset(aAssetData.getId(), aAssetFile.getAbsolutePath(), aAssetData.getArchiveFilePath(),
			    aAssetData.getMetaContent(), aAssetData.isMetaContent());

		    return AssetManager.getInstance().save(nParentSetId, aAsset);
		}
	    }
	}

	return false;
    }

    @Override
    public boolean createSet(final long nParentSetId, @Nullable final SetData aSetData) throws FailedLoginException {
	if (aSetData != null && _authenticate() != null)
	    return SetManager.getInstance().save(nParentSetId, new Set(aSetData.getId(), aSetData.getName(), aSetData.getMetaContent()));

	return false;
    }

    @Override
    @Nonnull
    public SetData[] getAllSets() throws FailedLoginException {
	final User aUser = _authenticate();
	final Collection<Set> aSets = SetManager.getInstance().allFor(aUser);

	return aSets.stream().map(aSet -> new SetData(aSet)).toArray(SetData[]::new);
    }
}
