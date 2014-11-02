package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.DataManager;
import at.ac.tuwien.media.master.webappapi.model.IdFactory;
import at.ac.tuwien.media.master.webappapi.model.Project;
import at.ac.tuwien.media.master.webappapi.model.User;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@ViewScoped
@ManagedBean(name = Value.CONTROLLER_PROJECTS)
public class ProjectsController implements Serializable {
    private Collection<Project> m_aProjects;
    private String m_sProjectName;
    private String m_sProjectDescription;
    private Collection<User> m_aProjectUsers;
    private Project m_aSelectedProject;

    public Collection<Project> getProjects() {
	if (m_aProjects == null)
	    m_aProjects = DataManager.getInstance().getAllProjects();

	return m_aProjects;
    }

    public void saveProject() {
	if (StringUtils.isNotEmpty(m_sProjectName) && StringUtils.isNotEmpty(m_sProjectDescription) && CollectionUtils.isNotEmpty(m_aProjectUsers)) {
	    DataManager.getInstance().saveProject(new Project(IdFactory.getInstance().getNextId(), m_sProjectName, m_sProjectDescription));

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
