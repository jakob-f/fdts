package at.frohnwieser.mahut.webappapi.db.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ReadWrite implements Serializable {
    private boolean m_aRead;
    private boolean m_aWrite;

    public ReadWrite(final boolean aRead, final boolean aWrite) {
	m_aRead = aRead;
	m_aWrite = aWrite;
    }

    public boolean isRead() {
	return m_aRead;
    }

    public void setRead(final boolean aRead) {
	m_aRead = aRead;
    }

    public boolean isWrite() {
	return m_aWrite;
    }

    public void setWrite(final boolean aWrite) {
	m_aWrite = aWrite;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (m_aRead ? 1231 : 1237);
	result = prime * result + (m_aWrite ? 1231 : 1237);
	return result;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	final ReadWrite other = (ReadWrite) obj;
	if (m_aRead != other.m_aRead)
	    return false;
	if (m_aWrite != other.m_aWrite)
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "Pair [first=" + m_aRead + ", second=" + m_aWrite + "]";
    }
}
