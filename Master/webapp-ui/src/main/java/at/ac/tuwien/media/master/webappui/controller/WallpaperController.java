package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import at.ac.tuwien.media.master.webappapi.db.manager.impl.AssetManager;
import at.ac.tuwien.media.master.webappapi.db.model.Asset;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@ApplicationScoped
@ManagedBean(name = Value.CONTROLLER_WALLPAPER)
public class WallpaperController implements Serializable {
    @Nonnull
    public String getRandomWPURL() {
	final Asset aRandomShowOnMainPageAsset = AssetManager.getInstance().getRandomShowOnMainPageAsset();

	if (aRandomShowOnMainPageAsset != null)
	    return aRandomShowOnMainPageAsset.getStreamURL();

	return "";
    }
}
