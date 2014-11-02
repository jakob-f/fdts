package at.ac.tuwien.media.master.webappapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webappapi.model.ERole;
import at.ac.tuwien.media.master.webappapi.model.Group;
import at.ac.tuwien.media.master.webappapi.model.IdFactory;
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
	final Collection<ERole> aUserRoles = new HashSet<ERole>();
	aUserRoles.add(ERole.ADMIN);

	s_aUserList = new ArrayList<User>();
	s_aUserList.add(new User(IdFactory.getInstance().getNextId(), "admin", "pass", "email", aUserRoles, null));

	s_aProjectList = new ArrayList<Project>();
	s_aProjectList.add(new Project(IdFactory.getInstance().getNextId(), "Project 1", "project one"));
	s_aProjectList.add(new Project(IdFactory.getInstance().getNextId(), "Project 2", "project two"));
	s_aProjectList.add(new Project(IdFactory.getInstance().getNextId(), "Project 3", ""));
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
    public Collection<User> getAllUser() {
	aRWLock.readLock().lock();

	final Collection<User> aUserList = new ArrayList<User>();
	aUserList.addAll(s_aUserList);

	aRWLock.readLock().unlock();

	return aUserList;
    }

    @Nullable
    public User getValidUser(@Nullable final String sUsername, @Nullable final String sPassword) {
	User aFoundUser = null;

	if (StringUtils.isNotEmpty(sUsername) && StringUtils.isNoneEmpty(sPassword)) {
	    aRWLock.readLock().lock();

	    for (final User aUser : s_aUserList)
		if (aUser.getName().equals(sUsername) && aUser.getPassword().equals(sPassword)) {
		    aFoundUser = aUser;
		    break;
		}

	    aRWLock.readLock().unlock();
	}

	return aFoundUser;
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
    public List<Project> getProjectsForUser(@Nonnull final long nUserId) {
	aRWLock.readLock().lock();

	final List<Project> aProjectList = new ArrayList<Project>();

	// XXX
	for (final User aUser : s_aUserList)
	    if (aUser.getId() == nUserId) {
		for (final Group aGroup : aUser.getGroups())
		    ;
	    }

	aRWLock.readLock().unlock();

	return aProjectList;
    }
}
