package at.ac.tuwien.media.master.transcoderui.model;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.ws.WebServiceException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.ffmpegwrapper.util.FFMPEGUtils;
import at.ac.tuwien.media.master.transcoderui.config.Configuration;
import at.ac.tuwien.media.master.transcoderui.config.Configuration.EField;
import at.ac.tuwien.media.master.transcoderui.util.Utils;
import at.ac.tuwien.media.master.webapp.FailedLoginException_Exception;
import at.ac.tuwien.media.master.wsclient.WSClient;

public class TranscoderData {
    private static TranscoderData s_aInstance = new TranscoderData();
    private Collection<String> m_aProjectList;
    private Collection<File> m_aVideoFiles;
    private String m_sMetadata;
    private String m_sDescription;
    private Collection<File> m_aMetadataFiles;

    private TranscoderData() {
    }

    public static TranscoderData getInstance() {
	return s_aInstance;
    }

    public boolean hasMetadataFiles() {
	return CollectionUtils.isNotEmpty(m_aMetadataFiles);
    }

    public boolean hasUploadFiles() {
	return CollectionUtils.isNotEmpty(m_aVideoFiles);
    }

    public boolean isReadyForCopy() {
	return hasUploadFiles() && getCopyDirectory() != null;
    }

    public boolean isSelectedAndReadyForCopy() {
	return isCopy() && isReadyForCopy();
    }

    public boolean isReadyForUpload() {
	return hasUploadFiles() && StringUtils.isNotEmpty(getSelectedProject());
    }

    public boolean isSelectedAndReadyForUpload() {
	return isUpload() && isReadyForUpload();
    }

    public boolean isReadyForStart() {
	return isSelectedAndReadyForCopy() || isSelectedAndReadyForUpload();
    }

    // only getters and setters
    @Nonnull
    public String getUsername() {
	return Configuration.getAsStringOrEmpty(EField.USERNAME);
    }

    public boolean setUsername(@Nonnull final String sUsername) {
	if (StringUtils.isNotEmpty(sUsername)) {
	    Configuration.set(EField.USERNAME, sUsername);

	    return true;
	}

	return false;
    }

    @Nonnull
    public String getPassword() {
	return Configuration.getAsStringOrEmpty(EField.PASSWORD);
    }

    public boolean setPassword(@Nonnull final String sPassword) {
	if (StringUtils.isNotEmpty(sPassword)) {
	    Configuration.set(EField.PASSWORD, sPassword);

	    return true;
	}

	return false;
    }

    @Nullable
    public URL getServerURL() {
	try {
	    return new URL(Configuration.getAsStringOrEmpty(EField.SERVER_URL));
	} catch (final MalformedURLException aMalformedURLException) {
	    // TODO ERROR
	}

	return null;
    }

    public boolean setServerURL(@Nonnull final URL aServerURL) {
	Configuration.set(EField.SERVER_URL, aServerURL.toString());

	return true;
    }

    public boolean setServerURL(@Nonnull final String sServerURL) {
	try {
	    if (StringUtils.isNotEmpty(sServerURL))
		return setServerURL(new URL(sServerURL));
	} catch (final MalformedURLException aMalformedURLException) {
	}

	return false;
    }

    @Nonnull
    public Locale getLocale() {
	return new Locale(Configuration.getAsStringOrEmpty(EField.LOCALE));
    }

    public boolean setLocale(@Nullable final Locale aLocale) {
	if (aLocale != null) {
	    Configuration.set(EField.LOCALE, aLocale.getLanguage());

	    return true;
	}

	return false;
    }

    @Nonnull
    public Collection<File> getVideoFiles() {
	if (m_aVideoFiles == null)
	    m_aVideoFiles = new TreeSet<File>();

	return m_aVideoFiles;
    }

    public boolean addUploadFileList(@Nullable final List<File> aInFileList) {
	if (CollectionUtils.isNotEmpty(aInFileList)) {
	    // add only supported files once
	    final List<File> aNewVideoFileList = new ArrayList<File>();
	    for (final File aUploadFile : aInFileList)
		if (FFMPEGUtils.isFormatSupportedForDecoding(FilenameUtils.getExtension(aUploadFile.getName())))
		    aNewVideoFileList.add(aUploadFile);
		else
		    ; // TODO: WARNING

	    if (CollectionUtils.isNotEmpty(aNewVideoFileList)) {
		getVideoFiles().addAll(aNewVideoFileList);

		// save last shown upload folder
		Configuration.set(EField.FILEPATH_UPLOAD, aNewVideoFileList.get(aNewVideoFileList.size() - 1).getParentFile().getAbsolutePath());

		return true;
	    }
	}

	return false;
    }

    @Nullable
    public File getUploadDirectory() {
	return Utils.getDirectoryOrNull(Configuration.getAsString(EField.FILEPATH_UPLOAD));
    }

    @Nullable
    public String getMetadata() {
	return m_sMetadata;
    }

    public void setMetdata(@Nullable final String sMetadata) {
	m_sMetadata = sMetadata;
    }

    @Nullable
    public String getDescription() {
	return m_sDescription;
    }

    public void setDescription(@Nullable final String sDescription) {
	m_sDescription = sDescription;
    }

    @Nonnull
    public Collection<File> getMetadataFiles() {
	if (m_aMetadataFiles == null)
	    m_aMetadataFiles = new TreeSet<File>();

	return m_aMetadataFiles;
    }

    public boolean addMetadataFileList(@Nullable final List<File> aMetadataFileList) {
	if (CollectionUtils.isNotEmpty(aMetadataFileList)) {
	    getMetadataFiles().addAll(aMetadataFileList);

	    // save last shown metadata folder
	    Configuration.set(EField.FILEPATH_METADATA, aMetadataFileList.get(aMetadataFileList.size() - 1).getParentFile().getAbsolutePath());

	    return true;
	}

	return false;
    }

    @Nullable
    public File getMetadataDirectory() {
	return Utils.getDirectoryOrNull(Configuration.getAsString(EField.FILEPATH_METADATA));
    }

    @Nullable
    public File getCopyDirectory() {
	return Utils.getDirectoryOrNull(Configuration.getAsString(EField.FILEPATH_COPY));
    }

    public boolean setCopyDirectory(@Nullable final File aCopyFile) {
	if (aCopyFile != null && aCopyFile.isDirectory()) {
	    Configuration.set(EField.FILEPATH_COPY, aCopyFile.getAbsolutePath());

	    return true;
	}

	return false;
    }

    public boolean isCopy() {
	return Configuration.getAsBoolean(EField.IS_SELECTED_COPY);
    }

    public void setIsCopy(final boolean bIsCopy) {
	Configuration.set(EField.IS_SELECTED_COPY, bIsCopy);
    }

    private boolean _setUpWSClient() {
	try {
	    WSClient.getInstance().setUsername(getUsername());
	    WSClient.getInstance().setPassword(getPassword());
	    WSClient.getInstance().setWSURL(getServerURL());

	    WSClient.getInstance().createEndpoint();

	    return true;
	} catch (final WebServiceException aConnectException) {
	    // TODO: ERROR
	}

	return false;
    }

    @Nonnull
    private void _loadProjectList() {
	m_aProjectList = new ArrayList<String>();
	if (_setUpWSClient()) {
	    try {
		m_aProjectList = WSClient.getInstance().getProjects();

		// also set selected project if possible
		if (CollectionUtils.isNotEmpty(m_aProjectList)) {
		    if (m_aProjectList.size() == 1)
			setSelectedProject(m_aProjectList.iterator().next());
		} else
		    ; // TODO: WARNING
	    } catch (final FailedLoginException_Exception aFailedLoginException) {
		// TODO: ERROR
	    }
	} else
	    ; // TODO: WARNING
    }

    public Collection<String> getProjectList() {
	if (m_aProjectList == null)
	    _loadProjectList();

	return m_aProjectList;
    }

    @Nonnull
    public String getSelectedProject() {
	// check if selected project is contained in project list
	final String sSelectedProject = Configuration.getAsStringOrEmpty(EField.SELECTED_PROJECT);
	if (getProjectList().contains(sSelectedProject))
	    return sSelectedProject;

	return "";
    }

    public void setSelectedProject(@Nullable final String sSelectedProject) {
	Configuration.set(EField.SELECTED_PROJECT, sSelectedProject);
    }

    public boolean isUpload() {
	return Configuration.getAsBoolean(EField.IS_SELECTED_UPLOAD);
    }

    public void setIsUpload(final boolean bIsUpload) {
	Configuration.set(EField.IS_SELECTED_UPLOAD, bIsUpload);
    }
}
