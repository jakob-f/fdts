package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import at.ac.tuwien.media.master.webappui.util.EPage;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.CONTROLLER_NAVIGATION)
public class NavigationController implements Serializable {
    private final EPage[] PAGES = new EPage[] { EPage.START, EPage.ASSETS, EPage.PROJECTS, EPage.GROUPS, EPage.USERS };

    public EPage[] getPages() {
	return PAGES;
    }
}