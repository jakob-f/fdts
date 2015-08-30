package at.frohnwieser.mahut.webapp.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.webapp.bean.Credentials;
import at.frohnwieser.mahut.webapp.page.EPage;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.db.manager.AssetManager;
import at.frohnwieser.mahut.webappapi.db.model.Asset;
import at.frohnwieser.mahut.webappapi.db.model.User;
import at.frohnwieser.mahut.webappapi.fs.manager.FSManager;

@SuppressWarnings("serial")
public class StreamAssetServlet extends HttpServlet {

    private void _streamFile(@Nullable final String sAbsoulteFilePath, @Nullable final String sName, @Nonnull final HttpServletResponse aResponse)
	    throws IOException {
	// if a thumbnail does not exist
	if (StringUtils.isNotEmpty(sAbsoulteFilePath) && StringUtils.isNotEmpty(sName)) {
	    final File aFile = new File(sAbsoulteFilePath);
	    if (aFile.isFile()) {
		// TODO change filename
		final String sContentType = getServletContext().getMimeType(sName);

		aResponse.reset();
		aResponse.setBufferSize(10240);
		aResponse.setContentType(StringUtils.isNotEmpty(sContentType) ? sContentType : "application/octet-stream");
		aResponse.setHeader("Content-Length", String.valueOf(aFile.length()));
		aResponse.setHeader("Content-Disposition", "inline; filename=\"" + sName + "\"");

		IOUtils.copy(new FileInputStream(aFile), aResponse.getOutputStream());
	    }
	}
    }

    @Override
    public void doGet(final HttpServletRequest aRequest, final HttpServletResponse aResponse) throws IOException, ServletException {
	final String sRequestedPath = aRequest.getPathInfo();

	if (StringUtils.isNotEmpty(sRequestedPath)) {
	    final String[] sRequests = sRequestedPath.split("&");
	    final String sHash = sRequests[0].substring(1);

	    if (sHash.matches(Value.REGEX_RESOURCE_HASH)) {
		User aUser = null;

		final Credentials aCredentials = (Credentials) aRequest.getSession().getAttribute(Value.BEAN_CREDENTIALS);
		if (aCredentials != null)
		    aUser = aCredentials.getUser();

		final Asset aAsset = AssetManager.getInstance().getFromHash(aUser, sHash);

		if (aAsset != null) {
		    final boolean bIsThumbnail = sRequests.length > 1 && sRequests[1].equals(Value.REQUEST_PARAMETER_THUMBNAIL);
		    final String sAbsouluteFilePath = FSManager.getAbsoluteFilePath(aAsset, bIsThumbnail);

		    _streamFile(sAbsouluteFilePath, aAsset.getName(), aResponse);

		    return;
		}
	    }
	}

	// resource not found - send redirect
	aResponse.sendRedirect(aRequest.getContextPath() + EPage.ROOT.getPath());
    }
}
