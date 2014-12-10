package at.ac.tuwien.media.master.webapp.servlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webapp.util.Value;
import at.ac.tuwien.media.master.webappapi.db.manager.impl.AssetManager;
import at.ac.tuwien.media.master.webappapi.db.model.Asset;
import at.ac.tuwien.media.master.webappapi.db.model.User;
import at.ac.tuwien.media.master.webappui.bean.Credentials;
import at.ac.tuwien.media.master.webappui.page.EPage;

@SuppressWarnings("serial")
public class StreamAssetServlet extends HttpServlet {

    @Override
    public void doGet(final HttpServletRequest aRequest, final HttpServletResponse aResponse) throws IOException, ServletException {
	final String sRequestedPath = aRequest.getPathInfo();

	if (StringUtils.isNotEmpty(sRequestedPath)) {
	    final String[] sRequests = sRequestedPath.split("&");
	    final String sHash = sRequests[0].substring(1);

	    if (sHash.matches(Value.REGEX_ASSET_HASH)) {
		User aUser = null;

		final Credentials aCredentials = (Credentials) aRequest.getSession().getAttribute(Value.BEAN_CREDENTIALS);
		if (aCredentials != null)
		    aUser = aCredentials.getUser();

		final Asset aAsset = AssetManager.getInstance().getRead(aUser, sHash);

		if (aAsset != null) {
		    final File aFile;
		    if (sRequests.length > 1 && sRequests[1].equals("thumb"))
			aFile = aAsset.getThumbnailFile();
		    else
			aFile = aAsset.getFile();

		    final String sContentType = getServletContext().getMimeType(aFile.getName());

		    aResponse.reset();
		    aResponse.setBufferSize(10240);
		    aResponse.setContentType(StringUtils.isNotEmpty(sContentType) ? sContentType : "application/octet-stream");
		    aResponse.setHeader("Content-Length", String.valueOf(aFile.length()));
		    aResponse.setHeader("Content-Disposition", "inline; filename=\"" + aFile.getName() + "\"");

		    Files.copy(aFile.toPath(), aResponse.getOutputStream());

		    return;
		}
	    }
	}

	// resource not found - send redirect
	aResponse.sendRedirect(aRequest.getContextPath() + EPage.ROOT.getPath());
    }
}
