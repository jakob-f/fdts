package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.DataManager;
import at.ac.tuwien.media.master.webappapi.model.Project;
import at.ac.tuwien.media.master.webappapi.model.User;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@ViewScoped
@ManagedBean(name = Value.CONTROLLER_PROJECTS)
public class ProjectsController implements Serializable {
    private Collection<Project> m_aAllProjects;
    private String m_sProjectName;
    private String m_sProjectDescription;
    private Collection<User> m_aProjectUsers;
    private Project m_aSelectedProject;

    public Collection<Project> getAllProjects() {
	if (m_aAllProjects == null)
	    m_aAllProjects = DataManager.getInstance().getAllProjects();

	return m_aAllProjects;
    }

    public void saveProject() {
	if (StringUtils.isNotEmpty(m_sProjectName) && StringUtils.isNotEmpty(m_sProjectDescription) && CollectionUtils.isNotEmpty(m_aProjectUsers)) {
	    m_aAllProjects = DataManager.getInstance().saveProject(new Project(m_sProjectName, m_sProjectDescription));

	    m_sProjectName = "";
	    m_aProjectUsers = null;
	}
    }

    @Nullable
    public Project getSelectedProject() {
	return m_aSelectedProject;
    }

    public void setSelectedProject(@Nullable final Project aProject) {
	m_aSelectedProject = aProject;
    }

    public void setDeleteProject(@Nullable final Project aProject) {
	m_aAllProjects = DataManager.getInstance().deleteProject(aProject);
    }

    @Nullable
    public String getProjectName() {
	return m_sProjectName;
    }

    public void setProjectName(@Nullable final String sProjectName) {
	m_sProjectName = sProjectName;
    }

    @Nullable
    public String getProjectDescription() {
	return m_sProjectDescription;
    }

    public void setProjectDescription(@Nullable final String sProjectDescription) {
	m_sProjectDescription = sProjectDescription;
    }

    @Nullable
    public Collection<User> getProjectUsers() {
	return m_aProjectUsers;
    }

    public void setProjectUsers(@Nullable final Collection<User> aProjectUsers) {
	m_aProjectUsers = aProjectUsers;
    }
}
