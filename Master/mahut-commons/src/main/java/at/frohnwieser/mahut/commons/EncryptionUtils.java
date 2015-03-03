package at.frohnwieser.mahut.commons;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class EncryptionUtils {
    // TODO use PBKDF2WithHmacSHA512
    private final static String ALGORITHM_HASH = "PBKDF2WithHmacSHA1";
    private final static String ALGORITHM_RANDOM = "SHA1PRNG";
    private final static int ITERATIONS = 20000;
    private final static int KEY_LENGTH = 128;
    private static final String ENCRYPTION_PASSWORD = "MaHuT$p4sSw0rD";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS7Padding";
    private static final String PROVIDER = "BC";

    static {
	Security.addProvider(new BouncyCastleProvider());
    }

    @Nullable
    private static byte[] _getMD5(@Nullable final String sPassword) {
	try {
	    return MessageDigest.getInstance("MD5").digest(sPassword.getBytes());
	} catch (final NoSuchAlgorithmException aNSException) {
	    aNSException.printStackTrace();
	}

	return null;
    }

    @Nullable
    public static boolean authenticate(@Nullable final String sValue, @Nullable final byte[] aStored, @Nullable final byte[] aSalt) {
	final byte[] aEncrypted = encrypt(sValue, aSalt);

	return aEncrypted != null ? Arrays.equals(aStored, aEncrypted) : null;
    }

    @Nullable
    public static byte[] encrypt(@Nullable final String sValue, @Nullable final byte[] aSalt) {
	if (StringUtils.isNotEmpty(sValue) && aSalt != null)
	    try {
		final KeySpec aKeySpec = new PBEKeySpec(sValue.toCharArray(), aSalt, ITERATIONS, KEY_LENGTH);
		final SecretKeyFactory aSecretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM_HASH);

		return aSecretKeyFactory.generateSecret(aKeySpec).getEncoded();
	    } catch (final NoSuchAlgorithmException aNSAException) {
		aNSAException.printStackTrace();
	    } catch (final InvalidKeySpecException aIKException) {
		aIKException.printStackTrace();
	    }

	return null;
    }

    @Nullable
    public static byte[] encrypt(@Nullable final String sValue, @Nullable final String sPassword) {
	if (StringUtils.isNotEmpty(sValue) && StringUtils.isNotEmpty(sPassword))
	    try {
		final byte[] aValueBytes = sValue.getBytes();
		final SecretKeySpec aKey = new SecretKeySpec(_getMD5(sPassword), ALGORITHM);
		final Cipher aCipher = Cipher.getInstance(TRANSFORMATION, PROVIDER);

		// encrypt
		aCipher.init(Cipher.ENCRYPT_MODE, aKey);
		final byte[] aCipherBytes = new byte[aCipher.getOutputSize(aValueBytes.length)];
		aCipher.doFinal(aCipherBytes, aCipher.update(aValueBytes, 0, aValueBytes.length, aCipherBytes, 0));

		return aCipherBytes;
	    } catch (final Exception aException) {
		aException.printStackTrace();
	    }

	return null;
    }

    @Nullable
    public static byte[] decrypt(@Nullable final byte[] aValueBytes, @Nullable final String sPassword) {
	if (aValueBytes != null && StringUtils.isNotEmpty(sPassword))
	    try {
		final SecretKeySpec aKey = new SecretKeySpec(_getMD5(sPassword), ALGORITHM);
		final Cipher aCipher = Cipher.getInstance(TRANSFORMATION, PROVIDER);

		// decrypt
		aCipher.init(Cipher.DECRYPT_MODE, aKey);
		final byte[] aPlainTextBytes = new byte[aCipher.getOutputSize(aValueBytes.length)];
		aCipher.doFinal(aPlainTextBytes, aCipher.update(aValueBytes, 0, aValueBytes.length, aPlainTextBytes, 0));

		return aPlainTextBytes;
	    } catch (final Exception aException) {
		aException.printStackTrace();
	    }

	return null;
    }

    // generate 128 bit salt
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

    public final static String getPassword() {
	return ENCRYPTION_PASSWORD + "::" + MACAddress.getAsString() + "::" + System.getProperty("user.name");
    }
}
