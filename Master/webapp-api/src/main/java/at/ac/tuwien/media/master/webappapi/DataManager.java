package at.ac.tuwien.media.master.webappapi;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.model.Asset;
import at.ac.tuwien.media.master.webappapi.model.ERole;
import at.ac.tuwien.media.master.webappapi.model.Group;
import at.ac.tuwien.media.master.webappapi.model.Project;
import at.ac.tuwien.media.master.webappapi.model.User;
import at.ac.tuwien.media.master.webappapi.util.Value;

public class DataManager {
    private static DataManager m_aInstance = new DataManager();
    private static ReadWriteLock aRWLock;
    private static Collection<User> s_aUsers;
    private static Collection<Project> s_aProjects;
    private static Collection<Asset> s_aAssets;

    static {
	aRWLock = new ReentrantReadWriteLock();

	// TODO
	final Collection<ERole> aUserRoles = new HashSet<ERole>();
	aUserRoles.add(ERole.ADMIN);

	s_aUsers = new ArrayList<User>();
	s_aUsers.add(new User("admin", "pass", "email", ERole.ADMIN, null));
	s_aUsers.add(new User("user", "pass", "email", ERole.USER, null));

	s_aProjects = new ArrayList<Project>();
	for (int i = 0; i < 25; i++)
	    s_aProjects.add(new Project("Project " + i, "project " + i));

	s_aAssets = new ArrayList<Asset>();
	s_aAssets.add(new Asset(new File(Value.DATA_PATH_ASSETS + "Louis.webm")).setPublish(true));
	s_aAssets.add(new Asset(new File(Value.DATA_PATH_ASSETS + "pdf.pdf")).setPublish(true).setMetadata(true));
	s_aAssets.add(new Asset(new File(Value.DATA_PATH_ASSETS + "big_buck_bunny.webm")));
	s_aAssets.add(new Asset(new File(Value.DATA_PATH_ASSETS + "big_buck_bunny.ogv")));
	s_aAssets.add(new Asset(new File(Value.DATA_PATH_ASSETS + "big_buck_bunny.mp4")));
	s_aAssets.add(new Asset(new File(Value.DATA_PATH_ASSETS + "1.jpg")));
	s_aAssets.add(new Asset(new File(Value.DATA_PATH_ASSETS + "2.jpg")));
	s_aAssets.add(new Asset(new File(Value.DATA_PATH_ASSETS + "3.jpg")).setMetadata(true));
	s_aAssets.add(new Asset(new File(Value.DATA_PATH_ASSETS + "4.png")));
	s_aAssets.add(new Asset(new File(Value.DATA_PATH_ASSETS + "5.png")).setMetadata(true));
	s_aAssets.add(new Asset(new File(Value.DATA_PATH_ASSETS + "elephant1.jpg")).setMetadata(true).setShowOnMainPage(true));
	s_aAssets.add(new Asset(new File(Value.DATA_PATH_ASSETS + "elephant2.jpg")).setMetadata(true).setShowOnMainPage(true));
	s_aAssets.add(new Asset(new File(Value.DATA_PATH_ASSETS + "elephant3.jpg")).setMetadata(true).setShowOnMainPage(true));
	s_aAssets.add(new Asset(new File(Value.DATA_PATH_ASSETS + "elephant4.jpg")).setMetadata(true).setShowOnMainPage(true));
    }

    private DataManager() {
    }

    public static DataManager getInstance() {
	return m_aInstance;
    }

    public void saveUser(@Nonnull final User aUser) {
	if (aUser == null)
	    throw new NullPointerException("user");

	aRWLock.writeLock().lock();

	s_aUsers.add(aUser);

	aRWLock.writeLock().unlock();
    }

    @Nonnull
    public Collection<User> getAllUser() {
	aRWLock.readLock().lock();

	final Collection<User> aUsers = new ArrayList<User>();
	aUsers.addAll(s_aUsers);

	aRWLock.readLock().unlock();

	return aUsers;
    }

    @Nullable
    public User getValidUser(@Nullable final String sUsername, @Nullable final String sPassword) {
	User aFoundUser = null;

	if (StringUtils.isNotEmpty(sUsername) && StringUtils.isNoneEmpty(sPassword)) {
	    aRWLock.readLock().lock();

	    for (final User aUser : s_aUsers)
		if (aUser.getName().equals(sUsername) && aUser.getPassword().equals(sPassword)) {
		    aFoundUser = aUser;
		    break;
		}

	    aRWLock.readLock().unlock();
	}

	return aFoundUser;
    }

    public Collection<Project> saveProject(@Nonnull final Project aProject) {
	if (aProject == null)
	    throw new NullPointerException("project");

	aRWLock.writeLock().lock();

	s_aProjects.add(aProject);

	aRWLock.writeLock().unlock();

	return getAllProjects();
    }

    @Nonnull
    public Collection<Project> deleteProject(@Nonnull final Project aProject) {
	if (aProject == null)
	    throw new NullPointerException("project");

	aRWLock.writeLock().lock();

	s_aProjects.remove(aProject);

	aRWLock.writeLock().unlock();

	return getAllProjects();
    }

    @Nonnull
    public Collection<Project> getAllProjects() {
	aRWLock.readLock().lock();

	final Collection<Project> aProjects = new ArrayList<Project>();
	aProjects.addAll(s_aProjects);

	aRWLock.readLock().unlock();

	return aProjects;
    }

    @Nonnull
    public Collection<Project> getProjectsForUser(@Nonnull final long nUserId) {
	aRWLock.readLock().lock();

	final Collection<Project> aProjects = new ArrayList<Project>();

	// XXX
	for (final User aUser : s_aUsers)
	    if (aUser.getId() == nUserId) {
		for (final Group aGroup : aUser.getGroups())
		    ;
	    }

	aRWLock.readLock().unlock();

	return aProjects;
    }

    @Nonnull
    public Collection<Asset> getAllAssets() {
	final Collection<Asset> aAssets = new ArrayList<Asset>();

	aRWLock.readLock().lock();

	aAssets.addAll(s_aAssets);

	aRWLock.readLock().unlock();

	return aAssets;
    }

    @Nonnull
    public Collection<Asset> deleteAsset(@Nonnull final Asset aAsset) {
	if (aAsset == null)
	    throw new NullPointerException("asset");

	aRWLock.writeLock().lock();

	s_aAssets.remove(aAsset);

	aRWLock.writeLock().unlock();

	return getAllAssets();
    }

    @Nonnull
    public Collection<Asset> updateAsset(@Nonnull final Asset aAsset) {
	if (aAsset == null)
	    throw new NullPointerException("asset");

	aRWLock.writeLock().lock();

	for (Asset aOldAsset : s_aAssets)
	    if (aOldAsset.getHash() == aAsset.getHash()) {
		aOldAsset = aAsset;
		break;
	    }

	aRWLock.writeLock().unlock();

	return getAllAssets();
    }

    @Nullable
    public Asset getPublishedAsset(@Nonnull final String sHash) {
	Asset aFoundAsset = null;

	aRWLock.readLock().lock();

	for (final Asset aAsset : getAllAssets())
	    if (aAsset.isPublish() && aAsset.getHash().equals(sHash))
		aFoundAsset = aAsset;

	aRWLock.readLock().unlock();

	return aFoundAsset;
    }

    @Nonnull
    public Collection<Asset> getShowOnMainPageAssets() {
	final Collection<Asset> aAssets = new ArrayList<Asset>();

	aRWLock.readLock().lock();

	for (final Asset aAsset : getAllAssets())
	    if (aAsset.getFileType().isImage() && aAsset.isShowOnMainPage())
		aAssets.add(aAsset);

	aRWLock.readLock().unlock();

	return aAssets;
    }
}
