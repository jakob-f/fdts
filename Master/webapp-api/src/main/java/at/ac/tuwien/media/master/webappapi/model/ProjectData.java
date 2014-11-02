package at.ac.tuwien.media.master.webappapi.model;

import java.io.File;
import java.util.Collection;
import java.util.TreeSet;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ProjectData {
    private long m_nId;
    private User m_aUploadUser;
    private Project m_aProject;
    private Collection<File> m_aVideoFiles;
    private String m_sMetadata;
    private String m_sDescription;
    private Collection<File> m_aMetadataFiles;

    public ProjectData() {
    }

    public long getId() {
	return m_nId;
    }

    public void setId(@Nonnegative final long nId) {
	m_nId = nId;
    }

    @Nullable
    public User getUploadUser() {
	return m_aUploadUser;
    }

    public void setUploadUser(@Nullable final User aUploadUser) {
	m_aUploadUser = aUploadUser;
    }

    @Nullable
    public Project getProject() {
	return m_aProject;
    }

    public void setProject(@Nullable final Project aProject) {
	m_aProject = aProject;
    }

    @Nonnull
    public Collection<File> getVideoFiles() {
	if (m_aVideoFiles == null)
	    m_aVideoFiles = new TreeSet<File>();

	return m_aVideoFiles;
    }

    public void setVideoFiles(@Nullable final Collection<File> aVideoFiles) {
	m_aVideoFiles = aVideoFiles;
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

    public void setMetadataFiles(@Nullable final Collection<File> aMetadataFiles) {
	m_aMetadataFiles = aMetadataFiles;
    }
}