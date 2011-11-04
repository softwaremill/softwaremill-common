package pl.softwaremill.common.conf.encoding;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.xml.bind.DatatypeConverter;

/**
 * Encodes and decodes arbitrary chunks of texts using the master password.
 * @author Adam Warski (adam at warski dot org)
 */
public class TextCoder {
    // Don't change!
    private final static String ALGORITHM = "PBEWithMD5AndTripleDES";
    private final static int ITERATION_COUNT = 20;
    private final static byte[] SALT = "9ak12av8".getBytes();

    public String encode(String text) {
        try {
            Cipher cipher = createCipher(Cipher.ENCRYPT_MODE, MasterPasswordStore.getMasterPassword(), SALT);
            byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
            return DatatypeConverter.printBase64Binary(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String decode(String text) {
        try {
            byte[] encrypted = DatatypeConverter.parseBase64Binary(text);
            Cipher cipher = createCipher(Cipher.DECRYPT_MODE, MasterPasswordStore.getMasterPassword(), SALT);
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Cipher createCipher(int mode, String plainPassword, byte[] salt)
            throws Exception {
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, ITERATION_COUNT);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(plainPassword.toCharArray());
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(mode, secretKey, pbeParamSpec);
        return cipher;
    }
}
