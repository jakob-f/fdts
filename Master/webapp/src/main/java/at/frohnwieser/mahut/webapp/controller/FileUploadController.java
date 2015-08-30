package at.frohnwieser.mahut.webapp.controller;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

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
@ViewScoped
@ManagedBean(name = Value.CONTROLLER_FILE_UPLOAD)
public class FileUploadController implements Serializable {

    public void handleFileUpload(@Nonnull final FileUploadEvent aEvent) {
	final User aUser = SessionUtils.getInstance().getCredentials().getUser();
	final Set aParentSet = SessionUtils.getInstance().getResourcesController().getCurrentSet();

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
