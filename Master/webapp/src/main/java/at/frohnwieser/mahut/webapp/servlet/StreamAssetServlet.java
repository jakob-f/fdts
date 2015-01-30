package at.frohnwieser.mahut.webapp.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.webapp.bean.Credentials;
import at.frohnwieser.mahut.webapp.page.EPage;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.db.manager.AssetManager;
import at.frohnwieser.mahut.webappapi.db.model.Asset;
import at.frohnwieser.mahut.webappapi.db.model.User;

@SuppressWarnings("serial")
public class StreamAssetServlet extends HttpServlet {

    private void _streamFile(final HttpServletResponse aResponse, @Nullable final String sFilePath) throws IOException {
	if (sFilePath != null) {
	    final File aFile = new File(sFilePath);
	    final String sFileName = FilenameUtils.getName(sFilePath);
	    final String sContentType = getServletContext().getMimeType(sFileName);

	    aResponse.reset();
	    aResponse.setBufferSize(10240);
	    aResponse.setContentType(StringUtils.isNotEmpty(sContentType) ? sContentType : "application/octet-stream");
	    aResponse.setHeader("Content-Length", String.valueOf(aFile.length()));
	    aResponse.setHeader("Content-Disposition", "inline; filename=\"" + sFileName + "\"");

	    IOUtils.copy(new FileInputStream(sFilePath), aResponse.getOutputStream());
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
		    if (sRequests.length > 1 && sRequests[1].equals(Value.REQUEST_PARAMETER_THUMBNAIL))
			_streamFile(aResponse, aAsset.getThumbnailFilePath());
		    else
			_streamFile(aResponse, aAsset.getFilePath());

		    return;
		}
	    }
	}

	// resource not found - send redirect
	aResponse.sendRedirect(aRequest.getContextPath() + EPage.ROOT.getPath());
    }
}
