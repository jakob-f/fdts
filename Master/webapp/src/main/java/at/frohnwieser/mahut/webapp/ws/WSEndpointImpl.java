package at.frohnwieser.mahut.webapp.ws;

import java.io.File;
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

import at.frohnwieser.mahut.webapp.ws.data.AssetData;
import at.frohnwieser.mahut.webapp.ws.data.SetData;
import at.frohnwieser.mahut.webappapi.db.manager.AssetManager;
import at.frohnwieser.mahut.webappapi.db.manager.GroupManager;
import at.frohnwieser.mahut.webappapi.db.manager.SetManager;
import at.frohnwieser.mahut.webappapi.db.manager.UserManager;
import at.frohnwieser.mahut.webappapi.db.model.Asset;
import at.frohnwieser.mahut.webappapi.db.model.ERole;
import at.frohnwieser.mahut.webappapi.db.model.Set;
import at.frohnwieser.mahut.webappapi.db.model.User;
import at.frohnwieser.mahut.webappapi.fs.manager.FSManager;

@MTOM
@WebService(endpointInterface = "at.frohnwieser.mahut.webapp.ws.IWSEndpoint")
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

    private boolean _isWrite(@Nullable final User aUser, final Set aParentSet) {
	return aUser != null && aParentSet != null && (aUser.getRole().is(ERole.ADMIN) || GroupManager.getInstance().isWrite(aUser, aParentSet));
    }

    @Override
    public boolean uploadAsset(final long nParentSetId, @Nullable final AssetData aAssetData) throws FailedLoginException {
	if (aAssetData != null) {
	    final User aUser = _authenticate();
	    final Set aParentSet = SetManager.getInstance().get(nParentSetId);

	    // also allow recently created sets...
	    // TODO check also time -> more secure
	    if (aParentSet.getUserId() == aUser.getId() || _isWrite(aUser, aParentSet)) {
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
	if (aSetData != null) {
	    final User aUser = _authenticate();
	    final Set aParentSet = SetManager.getInstance().get(nParentSetId);

	    if (_isWrite(aUser, aParentSet))
		return SetManager.getInstance().save(nParentSetId, new Set(aSetData.getId(), aSetData.getName(), aSetData.getMetaContent(), aUser.getId()));
	}

	return false;
    }

    @Override
    @Nonnull
    public SetData[] getAllSets() throws FailedLoginException {
	final User aUser = _authenticate();

	return SetManager.getInstance().allWriteFor(aUser).stream().map(aSet -> new SetData(aSet)).toArray(SetData[]::new);
    }
}
