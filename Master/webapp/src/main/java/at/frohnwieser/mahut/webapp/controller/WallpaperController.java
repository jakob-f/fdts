package at.frohnwieser.mahut.webapp.controller;

import java.io.Serializable;
import java.util.Collection;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.apache.commons.collections4.CollectionUtils;

import at.frohnwieser.mahut.commons.IdFactory;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.db.manager.impl.AssetManager;
import at.frohnwieser.mahut.webappapi.db.model.Asset;

@SuppressWarnings("serial")
@ApplicationScoped
@ManagedBean(name = Value.CONTROLLER_WALLPAPER)
public class WallpaperController implements Serializable {
    private final Random m_aRandom = new Random(IdFactory.getInstance().getId());
    Collection<Asset> m_aAssets;

    protected void _loadWPs() {
	m_aAssets = AssetManager.getInstance().allShowOnMainPage();
    }

    @Nonnull
    public String getRandomWPURL() {
	if (m_aAssets == null)
	    _loadWPs();

	if (CollectionUtils.isNotEmpty(m_aAssets)) {
	    final int nRandomIndex = m_aRandom.nextInt(m_aAssets.size());
	    final Asset aRandomWP = m_aAssets.stream().skip(nRandomIndex).findFirst().orElse(null);

	    if (aRandomWP != null)
		return aRandomWP.getStreamURL();
	}

	return "";
    }
}