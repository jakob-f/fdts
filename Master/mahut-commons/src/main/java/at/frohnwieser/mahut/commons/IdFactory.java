package at.frohnwieser.mahut.commons;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * java snowflake code adapted on https://github.com/Predictor/javasnowflake
 *
 * @author jf
 *
 */
public final class IdFactory {
    private static final long DATACENTERID_BITS = 10L;
    private static final long TIMESTAMP_BITS = 41L;
    private static final long DATACENTERID_MAX = -1L ^ (-1L << DATACENTERID_BITS);
    private static final long SEQUENCE_MAX = 4096;
    private static final long DATACENTERID_SHIFT = 64L - DATACENTERID_BITS;
    private static final long TIMESTAMP_SHIFT = 64L - DATACENTERID_BITS - TIMESTAMP_BITS;
    private static final long TWEPOCH = 1288834974657L;

    private static IdFactory m_aInstance = new IdFactory();
    private static final long f_nParticipantId;
    private volatile long m_nLastTimestamp = -1L;
    private volatile long m_nSequence = 0L;

    // generate participant id
    static {
	try {
	    final byte[] aMACAddress = MACAddress.get();

	    f_nParticipantId = ((0x000000FF & (long) aMACAddress[aMACAddress.length - 1]) | (0x0000FF00 & (((long) aMACAddress[aMACAddress.length - 2]) << 8))) >> 6;
	} catch (final Exception aException) {
	    throw new RuntimeException(aException);
	}

	if (f_nParticipantId > DATACENTERID_MAX || f_nParticipantId < 0)
	    throw new RuntimeException("invalid datacenter id");
    }

    private IdFactory() {
    }

    public static IdFactory getInstance() {
	return m_aInstance;
    }

    private long _waitForNextMillis(final long nlastTimestamp) {
	long timestamp = System.currentTimeMillis();

	while (timestamp <= nlastTimestamp)
	    timestamp = System.currentTimeMillis();

	return timestamp;
    }

    private synchronized long _generateSnowflake() {
	long nCurrentTimestamp = System.currentTimeMillis();

	if (nCurrentTimestamp < m_nLastTimestamp)
	    throw new RuntimeException("clock moved backwards");

	if (m_nLastTimestamp == nCurrentTimestamp) {
	    m_nSequence = (m_nSequence + 1) % SEQUENCE_MAX;

	    if (m_nSequence == 0)
		nCurrentTimestamp = _waitForNextMillis(m_nLastTimestamp);
	} else
	    m_nSequence = 0;

	m_nLastTimestamp = nCurrentTimestamp;

	return (((nCurrentTimestamp - TWEPOCH) << TIMESTAMP_SHIFT) | (f_nParticipantId << DATACENTERID_SHIFT) | m_nSequence) * -1L;
    }

    public long getId() {
	return _generateSnowflake();
    }

    // TODO better algo?
    private final SecureRandom f_aSR = new SecureRandom();

    public String getStringId() {
	return new BigInteger(130, f_aSR).toString(32);
    }

    public String getHash() {
	return Base64.getEncoder().encodeToString(String.valueOf(_generateSnowflake()).getBytes());
    }
}
