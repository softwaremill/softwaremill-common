package pl.softwaremill.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Random;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class RichString {
    private final String wrapped;

    public RichString(String wrapped) {
        this.wrapped = wrapped;
    }

    public static String generateRandom(int length) {
        StringBuffer sb = new StringBuffer();

        Random r = new Random();

        for (int i = 0; i < length; i++) {
            sb.append((char) (r.nextInt(25) + 65)); // A - Z
        }

        return sb.toString();
    }

    private static final char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    // See: http://www.osix.net/modules/article/?id=42

    private String hexStringFromBytes(byte[] b) {
        StringBuffer hex = new StringBuffer("");

        int msb;
        int lsb;
        int i;

        // MSB maps to idx 0
        for (i = 0; i < b.length; i++) {
            msb = ((int) b[i] & 0x000000FF) / 16;
            lsb = ((int) b[i] & 0x000000FF) % 16;
            hex.append(hexChars[msb]).append(hexChars[lsb]);
        }

        return hex.toString();
    }

    public String encodeAsPassword() {
        if (wrapped == null) {
            return null;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(wrapped.getBytes());
            return hexStringFromBytes(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String escapeForXml() {
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(wrapped);
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
