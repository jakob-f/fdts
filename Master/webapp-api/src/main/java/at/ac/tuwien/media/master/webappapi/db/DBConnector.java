package at.ac.tuwien.media.master.webappapi.db;

import java.io.File;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import at.ac.tuwien.media.master.webappapi.util.Value;

public class DBConnector {
    private static DBConnector m_aInstance = new DBConnector();
    private DB m_aDatabase;

    private DBConnector() {
    }

    public static DBConnector getInstance() {
	return m_aInstance;
    }

    public DB _getDataBase() {
	if (m_aDatabase == null)
	    m_aDatabase = DBMaker.newFileDB(new File(Value.DB_PATH)).mmapFileEnable().closeOnJvmShutdown().make();

	// .encryptionEnable(Value.DB_PWD).checksumEnabled();

	return m_aDatabase;
    }

    public HTreeMap<Object, Object> getCollectionHashMap(@Nonnull final String sCollectionName) {
	if (StringUtils.isEmpty(sCollectionName))
	    throw new NullPointerException("collection name");

	return _getDataBase().getHashMap(sCollectionName);
    }

    public BTreeMap<Object, Object> getCollectionTreeMap(@Nonnull final String sCollectionName) {
	if (StringUtils.isEmpty(sCollectionName))
	    throw new NullPointerException("collection name");

	return _getDataBase().getTreeMap(sCollectionName);
    }
}
