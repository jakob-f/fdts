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
	s_aAssets.add(new Asset(new File("./data/files/Louis.webm")).setPublish(true));
	s_aAssets.add(new Asset(new File("./data/files/045.jpg")).setPublish(true));
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
	aRWLock.readLock().lock();

	final Collection<Asset> aResources = new ArrayList<Asset>();
	aResources.addAll(s_aAssets);

	aRWLock.readLock().unlock();

	return aResources;
    }

    @Nullable
    public Asset getPublishedAssets(@Nonnull final String sHash) {
	for (final Asset aResource : getAllAssets())
	    if (aResource.isPublish() && aResource.getHash().equals(sHash))
		return aResource;

	return null;
    }
}
