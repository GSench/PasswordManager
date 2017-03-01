package utils;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
* AES Encoder/Decoder
*/
public class AESEncryptor {

	private static SecretKey stringToKey(String keyString, int size){
		byte[] keyStrict = keyString.getBytes();
		keyStrict = Arrays.copyOf(keyStrict, size);
		return new SecretKeySpec(keyStrict, "AES");
	}

	public static byte[] encrypt(byte[] bytes, String keyStr, int size) throws GeneralSecurityException {
		SecretKey key = stringToKey(keyStr, size);
		String cipherInstance = "AES/ECB/PKCS5Padding";
		Cipher eCipher = Cipher.getInstance(cipherInstance);
		eCipher.init(Cipher.ENCRYPT_MODE, key);
		return eCipher.doFinal(bytes);
	}

	public static byte[] decrypt(byte[] bytes, String keyStr, int size) throws GeneralSecurityException {
		SecretKey key = stringToKey(keyStr, size);
		String cipherInstance = "AES/ECB/PKCS5Padding";
		Cipher dCipher = Cipher.getInstance(cipherInstance);
		dCipher.init(Cipher.DECRYPT_MODE, key);
		return dCipher.doFinal(bytes);
	}

}