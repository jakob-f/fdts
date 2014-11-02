package at.ac.tuwien.media.master.webappapi.model;

public class IdFactory {
    private static IdFactory s_aInstance = new IdFactory();
    private long m_nCurrentId = 0;

    private IdFactory() {
    }

    public static IdFactory getInstance() {
	return s_aInstance;
    }

    public long getNextId() {
	return m_nCurrentId++;
    }
}
