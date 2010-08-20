package pl.softwaremill.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Random;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class StringUtil {
    public static String generateRandomPassword(int length) {
        StringBuffer sb = new StringBuffer();

        Random r = new Random();

        for (int i = 0; i < length; i++) {
            sb.append((char) (r.nextInt(25) + 65)); // A - Z
        }

        return sb.toString();
    }

    private static final char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    // See: http://www.osix.net/modules/article/?id=42

    private static String hexStringFromBytes(byte[] b) {
        String hex = "";

        int msb;
        int lsb;
        int i;

        // MSB maps to idx 0
        for (i = 0; i < b.length; i++) {
            msb = ((int) b[i] & 0x000000FF) / 16;
            lsb = ((int) b[i] & 0x000000FF) % 16;
            hex = hex + hexChars[msb] + hexChars[lsb];
        }

        return (hex);
    }

    public static String encodePassword(String password) {
        if (password == null) {
            return null;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(password.getBytes());
            return hexStringFromBytes(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String escapeForXml(String aText) {
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(aText);
        char character = iterator.current();
        while (character != CharacterIterator.DONE) {
            if (character == '<') {
                result.append("&lt;");
            } else if (character == '>') {
                result.append("&gt;");
            } else if (character == '\"') {
                result.append("&quot;");
            } else if (character == '\'') {
                result.append("&#039;");
            } else if (character == '&') {
                result.append("&amp;");
            } else {
                //the char is not a special one
                //add it to the result as is
                result.append(character);
            }
            character = iterator.next();
        }
        return result.toString();
    }
}
