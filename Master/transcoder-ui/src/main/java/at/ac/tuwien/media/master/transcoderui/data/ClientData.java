package at.ac.tuwien.media.master.transcoderui.data;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
import at.ac.tuwien.media.master.webapp.SetData;
import at.ac.tuwien.media.master.wsclient.WSClient;

public class ClientData {
    private static ClientData s_aInstance = new ClientData();
    private Collection<SetData> m_aSetDatas;
    private Collection<File> m_aMaterials;
    private Collection<File> m_aMetaContentFiles;

    private ClientData() {
    }

    public static ClientData getInstance() {
	return s_aInstance;
    }

    public boolean hasMaterials() {
	return CollectionUtils.isNotEmpty(m_aMaterials);
    }

    public boolean hasMetaContentFiles() {
	return CollectionUtils.isNotEmpty(m_aMetaContentFiles);
    }

    public boolean isReadyForCopy() {
	return hasMaterials() && getCopyDirectory() != null;
    }

    public boolean isSelectedAndReadyForCopy() {
	return isCopy() && isReadyForCopy();
    }

    public boolean isReadyForUpload() {
	return hasMaterials() && StringUtils.isNotEmpty(getSelectedSet());
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
    public Collection<File> getMaterials() {
	if (m_aMaterials == null)
	    m_aMaterials = new TreeSet<File>();

	return m_aMaterials;
    }

    public boolean addMaterials(@Nullable final List<File> aInFileList) {
	if (CollectionUtils.isNotEmpty(aInFileList)) {
	    // add only supported files once
	    final List<File> aNewVideoFileList = new ArrayList<File>();
	    for (final File aUploadFile : aInFileList)
		if (FFMPEGUtils.isFormatSupportedForDecoding(FilenameUtils.getExtension(aUploadFile.getName())))
		    aNewVideoFileList.add(aUploadFile);
		else
		    ; // TODO: WARNING

	    if (CollectionUtils.isNotEmpty(aNewVideoFileList)) {
		getMaterials().addAll(aNewVideoFileList);

		// save last shown upload folder
		Configuration.set(EField.FILEPATH_MATERIALS, aNewVideoFileList.get(aNewVideoFileList.size() - 1).getParentFile().getAbsolutePath());

		return true;
	    }
	}

	return false;
    }

    @Nullable
    public File getMaterialsDirectory() {
	return Utils.getDirectoryOrNull(Configuration.getAsString(EField.FILEPATH_MATERIALS));
    }

    @Nonnull
    public Collection<File> getMetaContentFiles() {
	if (m_aMetaContentFiles == null)
	    m_aMetaContentFiles = new TreeSet<File>();

	return m_aMetaContentFiles;
    }

    public boolean addMetaContentFiles(@Nullable final List<File> aMetaContentFiles) {
	if (CollectionUtils.isNotEmpty(aMetaContentFiles)) {
	    getMetaContentFiles().addAll(aMetaContentFiles);

	    // save last shown metadata folder
	    Configuration.set(EField.FILEPATH_METACONTENT, aMetaContentFiles.get(aMetaContentFiles.size() - 1).getParentFile().getAbsolutePath());

	    return true;
	}

	return false;
    }

    @Nullable
    public File getMetaContentFilesDirectory() {
	return Utils.getDirectoryOrNull(Configuration.getAsString(EField.FILEPATH_METACONTENT));
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
    private void _loadSetDatas() {
	m_aSetDatas = new ArrayList<SetData>();
	if (_setUpWSClient()) {
	    try {
		m_aSetDatas = WSClient.getInstance().getSets();

		// also set selected project if possible
		if (CollectionUtils.isNotEmpty(m_aSetDatas)) {
		    if (m_aSetDatas.size() == 1)
			// setSelectedSet(m_aSetDatas.iterator().next());
			// FIXME
			setSelectedSet("1");
		} else
		    ; // TODO: WARNING
	    } catch (final FailedLoginException_Exception aFailedLoginException) {
		// TODO: ERROR
	    }
	} else
	    ; // TODO: WARNING
    }

    public boolean hasSets() {
	return CollectionUtils.isNotEmpty(m_aSetDatas);
    }

    public Collection<String> getSets() {
	if (m_aSetDatas == null)
	    _loadSetDatas();

	// FIXME
	return m_aSetDatas.stream().map(aSetData -> String.valueOf(1L)).collect(Collectors.toCollection(ArrayList::new));
    }

    @Nonnull
    public String getSelectedSet() {
	// check if selected project is contained in project list
	final String sSelectedProject = Configuration.getAsStringOrEmpty(EField.SELECTED_SET);
	if (getSets().contains(sSelectedProject))
	    return sSelectedProject;

	return "";
    }

    public void setSelectedSet(@Nullable final String sSetData) {
	// FIXME
	Configuration.set(EField.SELECTED_SET, sSetData);
    }

    public boolean isUpload() {
	return Configuration.getAsBoolean(EField.IS_SELECTED_UPLOAD);
    }

    public void setIsUpload(final boolean bIsUpload) {
	Configuration.set(EField.IS_SELECTED_UPLOAD, bIsUpload);
    }
}