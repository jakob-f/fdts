package at.frohnwieser.mahut.webappapi.db;

import java.io.File;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import at.frohnwieser.mahut.webappapi.config.Configuration;
import at.frohnwieser.mahut.webappapi.config.Configuration.EField;
import at.frohnwieser.mahut.webappapi.fs.manager.FSManager;
import at.frohnwieser.mahut.webappapi.util.Value;

public class DBConnector {
    private static DBConnector m_aInstance = new DBConnector();
    private DB m_aDatabase;

    private DBConnector() {
    }

    public static DBConnector getInstance() {
	return m_aInstance;
    }

    private DB _getDataBase() {
	if (m_aDatabase == null) {
	    final File aDBDirectory = FSManager.getDBDirectory();

	    m_aDatabase = DBMaker.newFileDB(new File(aDBDirectory.getAbsolutePath() + File.separator + Value.DB_NAME)).mmapFileEnable()
		    .encryptionEnable(Configuration.getInstance().getAsString(EField.DB_PASSWORD)).checksumEnable().closeOnJvmShutdown().make();
	}

	return m_aDatabase;
    }

    public void commit() {
	_getDataBase().commit();
    }

    public <K, V> HTreeMap<K, V> getCollectionHashMap(@Nonnull final String sCollectionName) {
	if (StringUtils.isEmpty(sCollectionName))
	    throw new NullPointerException("collection name");

	return _getDataBase().getHashMap(sCollectionName);
    }

    public BTreeMap<Object, Object> getCollectionTreeMap(@Nonnull final String sCollectionName) {
	if (StringUtils.isEmpty(sCollectionName))
	    throw new NullPointerException("collection name");

	return _getDataBase().getTreeMap(sCollectionName);
    }

    public void rollback() {
	_getDataBase().rollback();
    }
}
