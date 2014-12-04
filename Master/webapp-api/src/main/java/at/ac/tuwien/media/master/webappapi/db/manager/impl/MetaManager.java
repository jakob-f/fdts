package at.ac.tuwien.media.master.webappapi.db.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import at.ac.tuwien.media.master.webappapi.db.model.Group;
import at.ac.tuwien.media.master.webappapi.db.model.Set;
import at.ac.tuwien.media.master.webappapi.db.model.User;

public class MetaManager {
    private static MetaManager m_aInstance = new MetaManager();

    private MetaManager() {
    }

    public static MetaManager getInstance() {
	return m_aInstance;
    }

    @Nonnull
    public Collection<Set> allSetsForUser(@Nonnull final User aUser) {
	if (aUser != null) {
	    final Collection<Group> aGroups = GroupManager.getInstance().allForUser(aUser);
	    final Collection<Long> aSetIds = new HashSet<Long>();

	    aGroups.forEach(aGroup -> aGroup.getPermissions().entrySet().stream().filter(aEntry -> aEntry.getValue().isWrite()).map(aEntry -> aEntry.getKey())
		    .collect(Collectors.toCollection(() -> aSetIds)));

	    return aSetIds.stream().map(nId -> SetManager.getInstance().get(nId)).filter(o -> o != null).collect(Collectors.toCollection(ArrayList::new));
	}

	return new ArrayList<Set>();
    }
}
