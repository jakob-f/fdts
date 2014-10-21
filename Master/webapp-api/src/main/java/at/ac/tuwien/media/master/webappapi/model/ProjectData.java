package at.ac.tuwien.media.master.webappapi.model;

import java.io.File;
import java.util.List;

import javax.annotation.Nonnull;

public class ProjectData {
    private String m_sUsername;
    private File m_aVideoFile;
    private String m_sProject;
    private String m_sMetadata;
    private String m_sDescription;
    private List<File> m_aMetadataFileList;

    public ProjectData() {
    }

    public ProjectData(@Nonnull final String sUsername, @Nonnull final File aVideoFile, @Nonnull final String sProject) {
	m_sUsername = sUsername;
	m_aVideoFile = aVideoFile;
	m_sProject = sProject;
    }

    public ProjectData(@Nonnull final String sUsername, @Nonnull final File aVideoFile, @Nonnull final String sProject, @Nonnull final String sMetadata,
	    @Nonnull final String sDescription, @Nonnull final List<File> aMetadataFileList) {
	this(sUsername, aVideoFile, sProject);

	m_sMetadata = sMetadata;
	m_sDescription = sDescription;
	m_aMetadataFileList = aMetadataFileList;
    }

    public String getUsername() {
	return m_sUsername;
    }

    public void setUsername(@Nonnull final String sUsername) {
	m_sUsername = sUsername;
    }

    public File getVideoFile() {
	return m_aVideoFile;
    }

    public void setVideoFile(@Nonnull final File aVideoFile) {
	m_aVideoFile = aVideoFile;
    }

    public String getProject() {
	return m_sProject;
    }

    public void setProject(@Nonnull final String sProject) {
	m_sProject = sProject;
    }

    public String getMetadata() {
	return m_sMetadata;
    }

    public void setMetadata(@Nonnull final String sMetadata) {
	m_sMetadata = sMetadata;
    }

    public String getDescription() {
	return m_sDescription;
    }

    public void setDescription(@Nonnull final String sDescription) {
	m_sDescription = sDescription;
    }

    public List<File> getMetadataFileList() {
	return m_aMetadataFileList;
    }

    public void setMetadataFileList(@Nonnull final List<File> aMetadataFileList) {
	m_aMetadataFileList = aMetadataFileList;
    }
}