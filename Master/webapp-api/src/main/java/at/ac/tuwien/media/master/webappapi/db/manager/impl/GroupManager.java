package at.ac.tuwien.media.master.webappapi.db.manager.impl;

import at.ac.tuwien.media.master.webappapi.db.manager.AbstractManager;
import at.ac.tuwien.media.master.webappapi.db.model.Group;
import at.ac.tuwien.media.master.webappapi.util.Value;

public class GroupManager extends AbstractManager<Group> {
    private static GroupManager m_aInstance = new GroupManager();

    private GroupManager() {
	super(Value.DB_COLLECTION_GROUPS);
    }

    public static GroupManager getInstance() {
	return m_aInstance;
    }
}
