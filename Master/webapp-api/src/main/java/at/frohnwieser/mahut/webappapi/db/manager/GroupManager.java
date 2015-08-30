package at.frohnwieser.mahut.webappapi.db.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.frohnwieser.mahut.webappapi.db.model.ERole;
import at.frohnwieser.mahut.webappapi.db.model.Group;
import at.frohnwieser.mahut.webappapi.db.model.Set;
import at.frohnwieser.mahut.webappapi.db.model.User;
import at.frohnwieser.mahut.webappapi.util.Value;

public class GroupManager extends AbstractManager<Group> {
    private static GroupManager m_aInstance = new GroupManager();

    private GroupManager() {
	super(Value.DB_COLLECTION_GROUPS);
    }

    public static GroupManager getInstance() {
	return m_aInstance;
    }

    @Nonnull
    protected Collection<Group> _allFor(@Nullable final Set aSet) {
	if (aSet != null)
	    return f_aEntries.values().stream().filter(aGroup -> aGroup.contains(aSet)).collect(Collectors.toCollection(ArrayList::new));

	return new ArrayList<Group>();
    }

    @Nonnull
    protected Collection<Group> _allFor(@Nullable final User aUser) {
	if (aUser != null)
	    return f_aEntries.values().stream().filter(aGroup -> aGroup.contains(aUser)).collect(Collectors.toCollection(ArrayList::new));

	return new ArrayList<Group>();
    }

    @Override
    public boolean delete(@Nullable final Group aEntry) {
	return _deleteCommit(aEntry);
    }

    @Nonnull
    public boolean isRead(@Nullable final User aUser, @Nullable final Set aSet) {
	if (aUser != null && aSet != null)
	    return f_aEntries.values().stream().filter(aGroup -> aGroup.contains(aUser) && aGroup.contains(aSet) && aGroup.getPermissionFor(aSet).isRead())
		    .findFirst().orElse(null) != null;

	return false;
    }

    @Nonnull
    public boolean isWrite(@Nullable final User aUser, @Nullable final Set aSet) {
	if (aUser != null && aSet != null)
	    if (aUser.getId().equals(aSet.getOwnerId()) || aUser.getRole().is(ERole.ADMIN))
		return true;
	    else
		return f_aEntries.values().stream()
		        .filter(aGroup -> aGroup.contains(aUser) && aGroup.contains(aSet) && aGroup.getPermissionFor(aSet).isWrite()).findFirst().orElse(null) != null;

	return false;
    }

    /**
     * does not commit
     */
    protected boolean _removeFromAll(@Nullable final Set aSet) {
	if (aSet != null) {
	    f_aEntries.values().stream().filter(aGroup -> aGroup.remove(aSet));

	    return true;
	}

	return false;
    }

    /**
     * does not commit
     */
    protected boolean _removeFromAll(@Nullable final User aUser) {
	if (aUser != null) {
	    f_aEntries.values().stream().filter(aGroup -> aGroup.remove(aUser));

	    return true;
	}

	return false;
    }

    @Override
    public boolean save(@Nullable final Group aEntry) {
	return _saveCommit(aEntry);
    }
}
