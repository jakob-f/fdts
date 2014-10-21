package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.DataManager;
import at.ac.tuwien.media.master.webappapi.model.User;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.CONTROLLER_USERS)
public class UsersController implements Serializable {
    private String m_sUserName;
    private String m_sUserPassword;

    public List<User> getUsersList() {
	return DataManager.getAllUser();
    }

    public void saveUser() {
	if (StringUtils.isNotEmpty(m_sUserName) && StringUtils.isNotEmpty(m_sUserPassword)) {
	    DataManager.saveUser(new User(m_sUserName, m_sUserPassword));

	    m_sUserName = "";
	    m_sUserPassword = "";
	}
    }

    @Nullable
    public String getUserName() {
	return m_sUserName;
    }

    public void setUserName(@Nullable final String sUserName) {
	this.m_sUserName = sUserName;
    }

    @Nullable
    public String getUserPassword() {
	return m_sUserPassword;
    }

    public void setUserPassword(@Nullable final String sUserPassword) {
	this.m_sUserPassword = sUserPassword;
    }
}
