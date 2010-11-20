package pl.softwaremill.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
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

    public String encodeAsPassword() {
        if (wrapped == null) {
            return null;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(wrapped.getBytes());
            return new RichHexString(md.digest()).unwrap();
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

    /**
     * @return Splits the wrapped string using the comma and returns a list of non-empty strings that are the
     * result of the split.
     */
    public List<String> splitByCommaGetNonEmpty() {
        List<String> result = new ArrayList<String>();
        for (String ignoredProperty : wrapped.split("[,]")) {
            if (!"".equals(ignoredProperty)) {
                result.add(ignoredProperty);
            }
        }

        return result;
    }
}
