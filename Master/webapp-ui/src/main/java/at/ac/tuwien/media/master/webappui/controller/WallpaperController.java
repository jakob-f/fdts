package at.ac.tuwien.media.master.webappui.controller;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.apache.commons.collections4.CollectionUtils;

import at.ac.tuwien.media.master.webappapi.db.manager.AssetManager;
import at.ac.tuwien.media.master.webappapi.db.model.Asset;
import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@ApplicationScoped
@ManagedBean(name = Value.CONTROLLER_WALLPAPER)
public class WallpaperController implements Serializable {
    private Collection<Asset> m_aAssets;
    private Random m_aRandom;

    @Nullable
    protected void loadWPFiles() {
	m_aAssets = AssetManager.getInstance().getShowOnMainPageAssets();
	m_aRandom = new Random(new Date().getTime());
    }

    @Nonnull
    public String getRandomWP() {
	if (m_aAssets == null)
	    loadWPFiles();

	if (CollectionUtils.isNotEmpty(m_aAssets)) {
	    final int nWPIndex = m_aRandom.nextInt(m_aAssets.size());

	    int i = 0;
	    for (final Asset aAsset : m_aAssets)
		if (i == nWPIndex)
		    return aAsset.getStreamPath();
		else
		    i++;
	}

	return "";
    }
}
