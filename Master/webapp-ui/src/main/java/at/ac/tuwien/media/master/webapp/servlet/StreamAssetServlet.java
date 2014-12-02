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
import at.ac.tuwien.media.master.webappui.page.EPage;

@SuppressWarnings("serial")
public class StreamAssetServlet extends HttpServlet {

    @Override
    public void doGet(final HttpServletRequest aRequest, final HttpServletResponse aResponse) throws IOException, ServletException {
	final String sRequestedPath = aRequest.getPathInfo();

	if (StringUtils.isNotEmpty(sRequestedPath)) {
	    final String sHash = sRequestedPath.substring(1);

	    if (sHash.matches(Value.REGEX_ASSET_HASH)) {
		final Asset aAsset = AssetManager.getInstance().getPublishedAsset(sHash);

		if (aAsset != null) {
		    final File aFile = aAsset.getFile();
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
