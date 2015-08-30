package at.frohnwieser.mahut.webapp.controller;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import at.frohnwieser.mahut.webapp.bean.Credentials;
import at.frohnwieser.mahut.webapp.util.SessionUtils;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.db.manager.AssetManager;
import at.frohnwieser.mahut.webappapi.db.manager.GroupManager;
import at.frohnwieser.mahut.webappapi.db.manager.SetManager;
import at.frohnwieser.mahut.webappapi.db.model.Asset;
import at.frohnwieser.mahut.webappapi.db.model.Set;
import at.frohnwieser.mahut.webappapi.db.model.User;
import at.frohnwieser.mahut.webappapi.fs.manager.FSManager;

@SuppressWarnings("serial")
@SessionScoped
@Named(Value.CONTROLLER_FILE_UPLOAD)
public class FileUploadController implements Serializable {
    @Inject
    private Credentials m_aCredentials;
    @Inject
    private SetsController m_aSetsController;

    public void handleFileUpload(@Nonnull final FileUploadEvent aEvent) {
	final User aUser = m_aCredentials.getUser();
	final Set aParentSet = m_aSetsController.getCurrentSet();

	// check write credentials
	// also allow it for owners of recently created sets...
	if (GroupManager.getInstance().isWrite(aUser, aParentSet)) {
	    final UploadedFile aUploadedFile = aEvent.getFile();
	    final Asset aAsset = new Asset(aUser.getId(), aUploadedFile.getFileName(), "", false);
	    if (FSManager.save(aAsset, aUploadedFile.getContents())) {
		aParentSet.add(aAsset);
		if (SetManager.getInstance().save(aParentSet))
		    if (AssetManager.getInstance().save(aAsset))
			SessionUtils.getInstance().info("successfully uploaded file", "");
	    }
	} else
	    SessionUtils.getInstance().error("cannot upload file - no write rights", "");
    }
}