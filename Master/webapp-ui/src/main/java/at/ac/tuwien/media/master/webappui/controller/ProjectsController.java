package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.DataManager;
import at.ac.tuwien.media.master.webappapi.model.Project;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.CONTROLLER_PROJECTS)
public class ProjectsController implements Serializable {
    private String m_sProjectName;
    private String m_sProjectUsers;

    public List<Project> getProjectsList() {
	return DataManager.getAllProjects();
    }

    public void saveProject() {
	if (StringUtils.isNotEmpty(m_sProjectName) && StringUtils.isNotEmpty(m_sProjectUsers)) {
	    DataManager.saveProject(new Project(m_sProjectName, m_sProjectUsers));

	    m_sProjectName = "";
	    m_sProjectUsers = "";
	}
    }

    @Nullable
    public String getProjectName() {
	return m_sProjectName;
    }

    public void setProjectName(@Nullable final String sProjectName) {
	m_sProjectName = sProjectName;
    }

    @Nullable
    public String getProjectUsers() {
	return m_sProjectUsers;
    }

    public void setProjectUsers(@Nullable final String sProjectUsers) {
	m_sProjectUsers = sProjectUsers;
    }
}
