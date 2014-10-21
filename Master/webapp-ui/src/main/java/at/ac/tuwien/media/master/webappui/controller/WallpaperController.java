package at.ac.tuwien.media.master.webappui.controller;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import at.ac.tuwien.media.master.webappui.util.Value;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = "wallpaperController")
public class WallpaperController implements Serializable {
    private static File[] s_aWPFiles;
    private static Random s_aRandom;

    @Nullable
    private void _loadWPFiles() {
	s_aWPFiles = new File(getClass().getClassLoader().getResource(Value.FOLDER_WP).getFile()).listFiles(new FilenameFilter() {
	    @Override
	    public boolean accept(final File file, final String filename) {
		return Pattern.matches(Value.REGEX_IMAGE, filename);
	    }
	});
    }

    public WallpaperController() {
	_loadWPFiles();
	s_aRandom = new Random(new Date().getTime());
    }

    @Nonnull
    public String getRandomWP() {
	if (s_aWPFiles != null) {
	    final int nWPIndex = s_aRandom.nextInt(s_aWPFiles.length);

	    return "." + Value.FOLDER_WP + s_aWPFiles[nWPIndex].getName();
	}

	return "";
    }
}
