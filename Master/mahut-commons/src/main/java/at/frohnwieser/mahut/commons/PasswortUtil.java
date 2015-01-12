package at.frohnwieser.mahut.commons;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.annotation.Nullable;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.lang3.StringUtils;

public final class PasswortUtil {
    private final static String ALGORITHM_HASH = "PBKDF2WithHmacSHA1";
    private final static String ALGORITHM_RANDOM = "SHA1PRNG";
    private final static int ITERATIONS = 20000;
    private final static int KEY_LENGTH = 128;

    @Nullable
    public static boolean authenticate(@Nullable final String sPassword, @Nullable final byte[] aStored, @Nullable final byte[] aSalt) {
	final byte[] aEncrypted = getEncrypted(sPassword, aSalt);

	return aEncrypted != null ? Arrays.equals(aStored, aEncrypted) : null;
    }

    @Nullable
    public static byte[] getEncrypted(@Nullable final String sPassword, @Nullable final byte[] aSalt) {
	if (StringUtils.isNotEmpty(sPassword) && aSalt != null)
	    try {
		final KeySpec aKeySpec = new PBEKeySpec(sPassword.toCharArray(), aSalt, ITERATIONS, KEY_LENGTH);
		final SecretKeyFactory aSecretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM_HASH);

		return aSecretKeyFactory.generateSecret(aKeySpec).getEncoded();
	    } catch (final NoSuchAlgorithmException aNSAException) {
		aNSAException.printStackTrace();
	    } catch (final InvalidKeySpecException aIKException) {
		aIKException.printStackTrace();
	    }

	return null;
    }

    // generate 126 bit salt
    @Nullable
    public static byte[] generateSalt() {
	try {
	    final SecureRandom aRandom = SecureRandom.getInstance(ALGORITHM_RANDOM);
	    final byte[] aSalt = new byte[16];
	    aRandom.nextBytes(aSalt);

	    return aSalt;
	} catch (final NoSuchAlgorithmException aNSAException) {
	    aNSAException.printStackTrace();
	}

	return null;
    }
}
