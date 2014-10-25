package at.ac.tuwien.media.master.webappapi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.model.Project;
import at.ac.tuwien.media.master.webappapi.model.User;

public class DataManager {
    private static DataManager m_aInstance = new DataManager();
    private static ReadWriteLock aRWLock;
    private static List<User> s_aUserList;
    private static List<Project> s_aProjectList;

    static {
	aRWLock = new ReentrantReadWriteLock();

	// TODO
	s_aUserList = new ArrayList<User>();
	s_aUserList.add(new User("admin", "pass"));

	s_aProjectList = new ArrayList<Project>();
	s_aProjectList.add(new Project("Project 1", "admin"));
	s_aProjectList.add(new Project("Project 2", "admin"));
	s_aProjectList.add(new Project("Project 3", ""));
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

	s_aUserList.add(aUser);

	aRWLock.writeLock().unlock();
    }

    @Nonnull
    public List<User> getAllUser() {
	aRWLock.readLock().lock();

	final List<User> aUserList = new ArrayList<User>();
	aUserList.addAll(s_aUserList);

	aRWLock.readLock().unlock();

	return aUserList;
    }

    public boolean isValidUser(@Nullable final String sUsername, @Nullable final String sPassword) {
	boolean bIsValid = false;

	if (StringUtils.isNotEmpty(sUsername) && StringUtils.isNoneEmpty(sPassword)) {
	    aRWLock.readLock().lock();

	    for (final User aUser : s_aUserList)
		if (aUser.getName().equals(sUsername) && aUser.getPassword().equals(sPassword)) {
		    bIsValid = true;
		    break;
		}

	    aRWLock.readLock().unlock();
	}

	return bIsValid;
    }

    public void saveProject(@Nonnull final Project aProject) {
	if (aProject == null)
	    throw new NullPointerException("project");

	aRWLock.writeLock().lock();

	s_aProjectList.add(aProject);

	aRWLock.writeLock().unlock();
    }

    @Nonnull
    public List<Project> getAllProjects() {
	aRWLock.readLock().lock();

	final List<Project> aProjectList = new ArrayList<Project>();
	aProjectList.addAll(s_aProjectList);

	aRWLock.readLock().unlock();

	return aProjectList;
    }

    @Nonnull
    public List<Project> getProjectsForUser(@Nonnull final String sUsername) {
	aRWLock.readLock().lock();

	final List<Project> aProjectList = new ArrayList<Project>();

	for (final Project aProject : s_aProjectList)
	    if (aProject.getUsers().contains(sUsername))
		aProjectList.add(aProject);

	aRWLock.readLock().unlock();

	return aProjectList;
    }
}
