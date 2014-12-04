package at.ac.tuwien.media.master.webappapi.db.manager.impl;

import at.ac.tuwien.media.master.webappapi.db.manager.AbstractManager;
import at.ac.tuwien.media.master.webappapi.db.model.HashTag;
import at.ac.tuwien.media.master.webappapi.util.Value;

public class HashManager extends AbstractManager<HashTag> {
    private static HashManager m_aInstance = new HashManager();

    private HashManager() {
	super(Value.DB_COLLECTION_HASHTAGS);
    }

    public static HashManager getInstance() {
	return m_aInstance;
    }
}